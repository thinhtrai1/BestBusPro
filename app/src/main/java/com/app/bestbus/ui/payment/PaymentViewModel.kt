package com.app.bestbus.ui.payment

import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.bestbus.R
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.data.payment.PaymentRepository
import com.app.bestbus.models.Ticket
import com.app.bestbus.models.Tour
import com.app.bestbus.models.User
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.utils.*
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
    val loading = MutableLiveData(false)
    var name = user?.name ?: ""
    var email = user?.email ?: ""
    var phone = user?.phone ?: ""
    var creditCard = ""
    var netBanking = ""
    var codeShipping = ""
    var viewSelecting: View? = null
    var ticket: Ticket? = null

    fun booking(callback: (Ticket) -> Unit) {
        loading.value = true
        val paymentMethod: String
        val paymentInformation: String
        when (viewSelecting!!.id) {
            R.id.edtCreditCard -> {
                paymentMethod = "DEBIT/CREDIT_CARD"
                paymentInformation = creditCard
            }
            R.id.edtNetBanking -> {
                paymentMethod = "NET_BANKING"
                paymentInformation = netBanking
            }
            R.id.edtCodeShipping -> {
                paymentMethod = "CODE_SHIPPING"
                paymentInformation = codeShipping
            }
            else -> {
                paymentMethod = "AT_STATION"
                paymentInformation = ""
            }
        }
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
                loading.value = false
                if (it is ApiResult.Success) {
                    ticket = it.data
                    callback(it.data)
                } else {
                    showErrorToast(it)
                }
            }
        }
    }

    fun onHome(v: View) {
        v.context.apply {
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun newSelect(viewSelect: View) {
        viewSelecting?.isSelected = false
        viewSelecting = viewSelect
        viewSelecting!!.isSelected = true
    }
}