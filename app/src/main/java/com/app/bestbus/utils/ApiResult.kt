package com.app.bestbus.utils

import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()

    companion object {
        inline fun <T> getResult(action: () -> Response<T>): ApiResult<T> {
            try {
                with(action()) {
                    if (isSuccessful) {
                        body()?.let {
                            return Success(it)
                        }
                    }
                    return Error(Exception(message()))
                }
            } catch (e: Exception) {
                return Error(e)
            }
        }
    }
}