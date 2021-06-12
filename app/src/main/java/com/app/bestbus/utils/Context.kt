package com.app.bestbus.utils

import android.widget.Toast
import com.app.bestbus.R
import com.app.bestbus.application.Application
import java.io.IOException

fun showToast(message: String?) {
    Toast.makeText(Application.instance, message, Toast.LENGTH_SHORT).show()
}

fun showToast(stringRes: Int) {
    Toast.makeText(Application.instance, stringRes, Toast.LENGTH_SHORT).show()
}

fun showErrorToast(throwable: ApiResult<Any>) {
    if (throwable is ApiResult.Error) {
        if (throwable.exception is IOException) {
            showToast(R.string.please_check_the_network_connection)
        } else {
            showToast(throwable.exception.message)
        }
    }
}

fun getString(stringRes: Int) = Application.instance.getString(stringRes)

fun getString(stringRes: Int, vararg formatArgs: Any?): String {
    return Application.instance.getString(stringRes, *formatArgs)
}