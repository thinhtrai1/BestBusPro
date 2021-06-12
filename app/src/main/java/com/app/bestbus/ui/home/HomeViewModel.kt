package com.app.bestbus.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.data.home.repository.HomeRepository
import com.app.bestbus.models.User
import com.app.bestbus.utils.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val sharedPreferencesHelper: SharedPreferencesHelper,
    homeRepository: HomeRepository
) : BaseViewModel() {

    val user = MutableLiveData(Gson().fromJson(sharedPreferencesHelper[Constant.PREF_USER], User::class.java))
    val dealAdapter = DealAdapter()
    val offerAdapter = OfferAdapter()
    val dealLoading = MutableLiveData(true)
    val offerLoading = MutableLiveData(true)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                homeRepository.getDeal()
            }.let {
                dealLoading.value = false
                if (it is ApiResult.Success) {
                    dealAdapter.deals.addAll(it.data)
                    dealAdapter.notifyDataSetChanged()
                } else {
                    showErrorToast(it)
                }
            }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                homeRepository.getOffer()
            }.let {
                offerLoading.value = false
                if (it is ApiResult.Success) {
                    offerAdapter.offers.addAll(it.data)
                    offerAdapter.notifyDataSetChanged()
                } else {
                    showErrorToast(it)
                }
            }
        }
    }
}