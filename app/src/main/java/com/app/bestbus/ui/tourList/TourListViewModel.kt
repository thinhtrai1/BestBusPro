package com.app.bestbus.ui.tourList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.data.tourList.TourListRepository
import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.showErrorToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TourListViewModel @Inject constructor(savedStateHandle: SavedStateHandle, tourListRepository: TourListRepository) : BaseViewModel() {
    val from = savedStateHandle.get("from") ?: ""
    val to = savedStateHandle.get("to") ?: ""
    val date: String? = savedStateHandle.get("date")
    val adapter = TourListAdapter(date)
    val loading = MutableLiveData(true)

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tourListRepository.getTour(from, to)
            }.let {
                loading.value = false
                if (it is ApiResult.Success) {
                    adapter.tours.addAll(it.data)
                    adapter.notifyDataSetChanged()
                } else {
                    showErrorToast(it)
                }
            }
        }
    }
}