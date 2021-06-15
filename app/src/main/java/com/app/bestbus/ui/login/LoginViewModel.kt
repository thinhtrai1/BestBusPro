package com.app.bestbus.ui.login

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.data.login.LoginRepository
import com.app.bestbus.models.User
import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.Constant
import com.app.bestbus.utils.SharedPreferencesHelper
import com.app.bestbus.utils.showErrorToast
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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

    fun login(file: File?, onSuccess: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            when {
                isUpdate -> {
                    withContext(Dispatchers.IO) {
                        var imageFile: MultipartBody.Part? = null
                        file?.let {
                            val avatarRequest = RequestBody.create(MediaType.parse("image/*"), it)
                            imageFile = MultipartBody.Part.createFormData("image", it.name, avatarRequest)
                        }
                        val mediaType = MediaType.parse("text/plain")
                        loginRepository.updateProfile(
                            RequestBody.create(mediaType, user?.id.toString()),
                            RequestBody.create(mediaType, name),
                            RequestBody.create(mediaType, email),
                            RequestBody.create(mediaType, phone),
                            RequestBody.create(mediaType, password),
                            imageFile
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
}