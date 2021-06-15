package com.app.bestbus.data.tourList

import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import javax.inject.Inject

class TourListRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getTour(from: String, to: String) = ApiResult.getResult {
        apiService.getTour(from, to)
    }
}