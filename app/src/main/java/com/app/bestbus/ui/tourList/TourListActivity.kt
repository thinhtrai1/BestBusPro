package com.app.bestbus.ui.tourList

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityTourListBinding
import com.app.bestbus.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TourListActivity : BaseActivity() {
    private lateinit var mBinding: ActivityTourListBinding
    private val mViewModel: TourListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tour_list)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.imvHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        mViewModel.loading.observe {
            showLoading(it)
        }
    }
}