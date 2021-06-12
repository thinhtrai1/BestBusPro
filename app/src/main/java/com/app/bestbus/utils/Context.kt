package com.app.bestbus.utils

import android.widget.Toast
import com.app.bestbus.application.Application

fun showToast(message: String?) {
    Toast.makeText(Application.instance, message, Toast.LENGTH_SHORT).show()
}

fun showToast(stringRes: Int) {
    Toast.makeText(Application.instance, stringRes, Toast.LENGTH_SHORT).show()
}

fun getString(stringRes: Int) = Application.instance.getString(stringRes)