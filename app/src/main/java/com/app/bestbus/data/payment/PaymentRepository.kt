package com.app.bestbus.data.payment

import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import retrofit2.http.Field
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val apiClient: ApiService) {
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
        apiClient.booking(userId, name, email, phone, tourId, date, seatSelected, paymentMethod, paymentInformation, totalAmount)
    }
}