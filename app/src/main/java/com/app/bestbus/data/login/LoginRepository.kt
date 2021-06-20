package com.app.bestbus.data.login

import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun login(email: String, password: String) = ApiResult.getResult {
        apiService.login(email, password)
    }

    suspend fun signUp(
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        image: MultipartBody.Part?
    ) = ApiResult.getResult {
        apiService.signUp(email, password, name, phone, image)
    }

    suspend fun updateProfile(
        userId: RequestBody,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        image: MultipartBody.Part?
    ) = ApiResult.getResult {
        apiService.updateProfile(userId, email, password, name, phone, image)
    }
}