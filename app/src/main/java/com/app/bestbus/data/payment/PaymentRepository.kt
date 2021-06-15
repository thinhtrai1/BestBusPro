package com.app.bestbus.data.payment

import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun booking(
        userId: Int?,
        name: String,
        email: String,
        phone: String,
        tourId: Int,
        date: String?,
        seatSelected: String,
        paymentMethod: String,
        paymentInformation: String,
        totalAmount: Float
    ) = ApiResult.getResult {
        apiService.booking(userId, name, email, phone, tourId, date, seatSelected, paymentMethod, paymentInformation, totalAmount)
    }
}