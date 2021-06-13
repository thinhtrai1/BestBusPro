package com.app.bestbus.ui.searchTour

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivitySearchTourBinding
import com.app.bestbus.ui.tourList.TourListActivity
import com.app.bestbus.utils.Constant
import java.util.*

class SearchTourActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySearchTourBinding
    private val mViewModel: SearchTourViewModel by viewModels()
    private val mCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_tour)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel.apply {
            date.value = Constant.dateFormat.format(mCalendar.time)
        }

        mBinding.edtDate.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mCalendar.set(year, month, dayOfMonth)
                mViewModel.date.value = Constant.dateFormat.format(mCalendar.time)
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        mBinding.btnSearch.setOnClickListener {
            startActivity(
                Intent(this, TourListActivity::class.java)
                    .putExtra("from", mViewModel.from)
                    .putExtra("to", mViewModel.to)
                    .putExtra("date", mViewModel.date.value)
            )
        }
    }
}