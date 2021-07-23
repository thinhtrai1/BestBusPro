package com.app.bestbus.ui.searchTour

import android.app.DatePickerDialog
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.app.bestbus.base.BaseViewModel
import com.app.bestbus.ui.tourList.TourListActivity
import com.app.bestbus.utils.Constant
import java.util.*

class SearchTourViewModel : BaseViewModel() {
    private val mCalendar: Calendar = Calendar.getInstance()
    var from = ""
    var to = ""
    val date = MutableLiveData(Constant.dateFormat.format(mCalendar.time))

    fun onDate(v: View) {
        DatePickerDialog(v.context, { _, year, month, dayOfMonth ->
            mCalendar.set(year, month, dayOfMonth)
            date.value = Constant.dateFormat.format(mCalendar.time)
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun onSearch(v: View) {
        v.context.apply {
            startActivity(
                Intent(this, TourListActivity::class.java)
                    .putExtra("from", from)
                    .putExtra("to", to)
                    .putExtra("date", date.value)
            )
        }
    }
}