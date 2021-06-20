package com.app.bestbus.ui.searchTour

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivitySearchTourBinding

class SearchTourActivity : BaseActivity() {
    private lateinit var mBinding: ActivitySearchTourBinding
    private val mViewModel: SearchTourViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_tour)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
    }
}