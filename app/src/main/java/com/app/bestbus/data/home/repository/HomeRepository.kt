package com.app.bestbus.data.home.repository

import com.app.bestbus.models.Deal
import com.app.bestbus.models.Offer
import com.app.bestbus.utils.ApiResult

interface HomeRepository {
    suspend fun getDeal(): ApiResult<ArrayList<Deal>>
    suspend fun getOffer(): ApiResult<ArrayList<Offer>>
}