package com.app.bestbus.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityDetailBinding
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.ui.payment.PaymentActivity
import com.google.gson.Gson

class DetailActivity : BaseActivity() {
    private lateinit var mBinding: ActivityDetailBinding
    private val mViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        mBinding.it = mViewModel.tourData

        val count = DisplayMetrics().apply {
            if (Build.VERSION.SDK_INT >= 29) {
                display?.getRealMetrics(this)
            } else {
                windowManager.defaultDisplay?.getRealMetrics(this)
            }
        }.widthPixels / 100
        mBinding.rcvSeats.layoutManager = GridLayoutManager(this, if (resources.displayMetrics.densityDpi < 600) count else count - 1)
        mBinding.rcvSeats.adapter = mViewModel.adapter

        mBinding.btnBook.setOnClickListener {
            startActivity(
                Intent(this, PaymentActivity::class.java)
                    .putExtra("tour", Gson().toJson(mViewModel.tourData))
            )
        }

        mBinding.imvHome.setOnClickListener {
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}