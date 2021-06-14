package com.app.bestbus.ui.payment

import android.widget.TextView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.data.payment.PaymentRepository
import com.app.bestbus.models.Ticket
import com.app.bestbus.models.Tour
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
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    sharedPreferencesHelper: SharedPreferencesHelper,
    private val savedStateHandle: SavedStateHandle,
    private val paymentRepository: PaymentRepository
) : BaseViewModel() {
    private val mGson = Gson()
    private val user: User? = mGson.fromJson(sharedPreferencesHelper[Constant.PREF_USER, ""], User::class.java)
    val name = user?.name ?: ""
    val email = user?.email ?: ""
    val phone = user?.phone ?: ""

    fun booking(paymentMethod: String, paymentInformation: String, callback: (ApiResult<Ticket>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                mGson.fromJson(savedStateHandle.get<String>("tour"), Tour::class.java).let {
                    paymentRepository.booking(
                        user?.id,
                        name,
                        email,
                        phone,
                        it.id,
                        it.date,
                        mGson.toJson(it.seatSelected),
                        paymentMethod,
                        paymentInformation,
                        it.amount
                    )
                }
            }.let {
                callback(it)
            }
        }
    }
}