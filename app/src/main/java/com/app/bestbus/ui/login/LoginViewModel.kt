package com.app.bestbus.ui.login

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.database.getIntOrNull
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.bestbus.application.Application
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.data.login.LoginRepository
import com.app.bestbus.models.User
import com.app.bestbus.utils.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    val isUpdate = savedStateHandle.get("isUpdate") ?: false
    val isLogin = MutableLiveData(true)
    val user: User? = Gson().fromJson(sharedPreferencesHelper[Constant.PREF_USER, ""], User::class.java)
    var name = user?.name ?: ""
    var email = user?.email ?: ""
    var phone = user?.phone ?: ""
    var password = user?.password ?: ""
    var confirmPassword = user?.password ?: ""
    var imageUri: Uri? = null
    val loading = MutableLiveData(false)

    fun login(onSuccess: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            when {
                isUpdate -> {
                    withContext(Dispatchers.IO) {
                        val mediaType = "text/plain".toMediaTypeOrNull()
                        loginRepository.updateProfile(
                            user?.id.toString().toRequestBody(mediaType),
                            name.toRequestBody(mediaType),
                            email.toRequestBody(mediaType),
                            phone.toRequestBody(mediaType),
                            password.toRequestBody(),
                            imageUri?.getMultipartBody("image")
                        )
                    }
                }
                isLogin.value!! -> {
                    withContext(Dispatchers.IO) {
                        loginRepository.login(email, password)
                    }
                }
                else -> {
                    withContext(Dispatchers.IO) {
                        loginRepository.signUp(email, password, name)
                    }
                }
            }.let {
                loading.value = false
                if (it is ApiResult.Success) {
                    sharedPreferencesHelper.getSharedPreferences().edit()
                        .putString(Constant.PREF_EMAIL, it.data.email)
                        .putString(Constant.PREF_PASSWORD, it.data.password)
                        .putString(Constant.PREF_USER, Gson().toJson(it.data))
                        .apply()
                    onSuccess()
                } else {
                    showErrorToast(it)
                }
            }
        }
    }

    private fun Uri.getMultipartBody(partKey: String): MultipartBody.Part? {
        val contentResolver = Application.instance.contentResolver
        var length = contentResolver.openFileDescriptor(this, "r")?.use { it.statSize.toInt() } ?: -1
        if (length == -1 && scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            contentResolver.query(this, arrayOf(OpenableColumns.SIZE), null, null, null)?.use {
                if (it.moveToFirst()) {
                    length = it.getIntOrNull(it.getColumnIndex(OpenableColumns.SIZE)) ?: -1
                }
            }
        }
        if (length == -1) return null
        val type = contentResolver.getType(this)?.toMediaTypeOrNull() ?: return null
        return contentResolver.openInputStream(this)?.use { inputStream ->
            MultipartBody.Part.createFormData(
                partKey, "fileName.".plus(type.subtype), inputStream.readBytes().toRequestBody(type, 0, length)
            )
        }
    }
}