package com.app.bestbus.data.home.repository

import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(private val apiService: ApiService) : HomeRepository {
    override suspend fun getDeal() = ApiResult.getResult {
        apiService.getDeal()
    }

    override suspend fun getOffer() = ApiResult.getResult {
        apiService.getOffer()
    }
}