package com.app.bestbus.ui.selectSeat

import androidx.lifecycle.SavedStateHandle
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.models.Tour
import com.google.gson.Gson
import javax.inject.Inject

class SelectSeatViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel() {
    val tourData = Gson().fromJson(savedStateHandle.get<String>("tour"), Tour::class.java)!!
    val adapter = SeatAdapter(tourData.seatQuantity, tourData.seatSelected, tourData.count)
}