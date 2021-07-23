package com.app.bestbus.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {
    private lateinit var mBinding: ActivityDetailBinding
    private val mViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        mBinding.viewModel = mViewModel
        mBinding.tour = mViewModel.tourData

        resources.displayMetrics.let {
            mBinding.rcvSeats.layoutManager = GridLayoutManager(this, if (it.densityDpi < 600) it.widthPixels / 100 else it.widthPixels / 100 - 1)
        }
    }
}