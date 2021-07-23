package com.app.bestbus.ui.detail

import android.content.Intent
import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.models.Tour
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.ui.payment.PaymentActivity
import com.google.gson.Gson
import java.util.ArrayList
import javax.inject.Inject

class DetailViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : BaseViewModel() {
    val tourData = Gson().fromJson(savedStateHandle.get<String>("tour"), Tour::class.java)!!
    val adapter: SimpleSeatAdapter

    init {
        val seats = ArrayList<String>()
        for (i in tourData.seatSelected) {
            var s: Int = i / (tourData.count * 2) + 65
            if (s > 90) {
                s += 6
            }
            seats.add(s.toChar().toString().plus(i % (tourData.count * 2) + 1))
        }
        adapter = SimpleSeatAdapter(seats)
    }

    fun book(v: View) {
        v.context.apply {
            startActivity(
                Intent(this, PaymentActivity::class.java)
                    .putExtra("tour", Gson().toJson(tourData))
            )
        }
    }

    fun home(v: View) {
        v.context.apply {
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}