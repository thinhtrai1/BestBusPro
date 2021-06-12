package com.app.bestbus.utils

import com.app.bestbus.application.Application

class SharedPreferencesHelper {
    private val mSharedPreferences = Application.instance.getSharedPreferences("BEST_BUS_2021", 0)

    operator fun get(key: String, defaultValue: String? = null): String? {
        return mSharedPreferences.getString(key, defaultValue)
    }

    operator fun set(key: String, data: String?) {
        mSharedPreferences.edit().putString(key, data).apply()
    }
}