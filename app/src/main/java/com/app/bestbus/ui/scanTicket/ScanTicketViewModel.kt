package com.app.bestbus.ui.scanTicket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.models.Ticket
import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import com.app.bestbus.utils.showErrorToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanTicketViewModel @Inject constructor(private val apiService: ApiService) : BaseViewModel() {
    val loading = MutableLiveData(false)
    fun scanTicket(input: String, callback: (Ticket) -> Unit) {
        loading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ApiResult.getResult {
                    apiService.scanTicket(input)
                }
            }.let {
                loading.value = false
                if (it is ApiResult.Success) {
                    callback(it.data)
                } else {
                    showErrorToast(it)
                }
            }
        }
    }
}