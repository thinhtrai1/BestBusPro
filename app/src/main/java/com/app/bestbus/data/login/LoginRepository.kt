package com.app.bestbus.data.login

import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class LoginRepository  @Inject constructor(private val apiService: ApiService) {
    suspend fun login(email: String, password: String) = ApiResult.getResult {
        apiService.login(email, password)
    }

    suspend fun signUp(email: String, password: String, name: String) = ApiResult.getResult {
        apiService.signUp(email, password, name)
    }

    suspend fun updateProfile(
        userId: RequestBody,
        name: RequestBody,
        email: RequestBody,
        phone: RequestBody,
        password: RequestBody,
        image: MultipartBody.Part?
    ) = ApiResult.getResult {
        apiService.updateProfile(userId, name, email, phone, password, image)
    }
}