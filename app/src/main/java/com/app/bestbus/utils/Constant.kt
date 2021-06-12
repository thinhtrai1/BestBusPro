package com.app.bestbus.utils

import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*

object Constant {
    const val BASE_URL = "http://ducthinh-bestbus.000webhostapp.com/"

    const val PREF_EMAIL = "PREF_EMAIL"
    const val PREF_PASSWORD = "PREF_PASSWORD"
    const val PREF_USER = "PREF_USER"

    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
    val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
    val TICKET_FOLDER = Environment.getExternalStorageDirectory().path + "/BESTBUS/Ticket"
}