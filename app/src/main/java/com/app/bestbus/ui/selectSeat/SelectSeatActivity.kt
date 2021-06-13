package com.app.bestbus.ui.selectSeat

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivitySelectSeatBinding
import com.app.bestbus.ui.detail.DetailActivity
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.utils.showToast
import com.google.gson.Gson

class SelectSeatActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySelectSeatBinding
    private val mViewModel: SelectSeatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= 29) {
            display?.getRealMetrics(metrics)
        } else {
            windowManager.defaultDisplay?.getRealMetrics(metrics)
        }
        mViewModel.adapter.itemLayout = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, metrics.widthPixels / (mViewModel.tourData.count * 2 + 1))
        val padding = metrics.widthPixels / (mViewModel.tourData.count * 6)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_seat)
        mBinding.lifecycleOwner = this

        mBinding.rcvSeat.setPadding(padding, 0, padding, 0)
        mBinding.rcvSeat.adapter = mViewModel.adapter
        mBinding.rcvSeat.layoutManager = GridLayoutManager(this, mViewModel.tourData.count)

        mBinding.btnSelectSeat.setOnClickListener {
            if (mViewModel.adapter.selectingList.isNotEmpty()) {
                mViewModel.tourData.seatSelected = mViewModel.adapter.selectingList.apply { sort() }
                startActivity(
                    Intent(this, DetailActivity::class.java)
                    .putExtra("tour", Gson().toJson(mViewModel.tourData))
                )
            } else {
                showToast(getString(R.string.please_choose_seat))
            }
        }

        mBinding.imvHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}