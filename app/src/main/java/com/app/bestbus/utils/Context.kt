package com.app.bestbus.utils

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.app.bestbus.R
import com.app.bestbus.application.Application
import java.io.IOException

fun showToast(message: String?) {
    Toast.makeText(Application.instance, message, Toast.LENGTH_SHORT).show()
}

fun showToast(stringRes: Int) {
    Toast.makeText(Application.instance, stringRes, Toast.LENGTH_SHORT).show()
}

fun showErrorToast(result: ApiResult<Any>) {
    if (result is ApiResult.Error) {
        if (result.exception is IOException) {
            showToast(R.string.please_check_the_network_connection)
        } else {
            showToast(result.exception.message)
        }
    }
}

fun getString(stringRes: Int) = Application.instance.getString(stringRes)

fun getString(stringRes: Int, vararg formatArgs: Any?) = Application.instance.getString(stringRes, *formatArgs)

fun Context.isPermissionGranted(vararg permissions: String): Boolean {
    return permissions.indexOfFirst { ContextCompat.checkSelfPermission(this, it) != 0 } == -1
}