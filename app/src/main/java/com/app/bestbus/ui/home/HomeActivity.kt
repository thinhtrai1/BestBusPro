package com.app.bestbus.ui.home

import android.Manifest
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Pair
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityHomeBinding
import com.app.bestbus.ui.addTour.AddTourActivity
import com.app.bestbus.ui.login.LoginActivity
import com.app.bestbus.ui.scanTicket.ScanTicketActivity
import com.app.bestbus.ui.searchTour.SearchTourActivity
import com.app.bestbus.utils.Constant
import com.app.bestbus.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private lateinit var mBinding: ActivityHomeBinding
    private val mViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        LinearSnapHelper().attachToRecyclerView(mBinding.rcvBestDeals)

        val metrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= 29) {
            display?.getRealMetrics(metrics)
        } else {
            windowManager.defaultDisplay?.getRealMetrics(metrics)
        }
        mViewModel.dealAdapter.itemLayout = ViewGroup.LayoutParams(metrics.widthPixels * 3 / 4, ViewGroup.LayoutParams.MATCH_PARENT)

        listener()
    }

    private fun listener() {
        mBinding.imvLogo.setOnClickListener {
            startActivity(
                Intent(this, SearchTourActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(mBinding.imvLogo, "logo")).toBundle()
            )
        }

        mBinding.imvMenu.setOnClickListener {
            mBinding.viewContainer.openDrawer(mBinding.viewMenuLeft)
        }

        mBinding.viewBookTour.setOnClickListener {
            startActivity(
                Intent(this, SearchTourActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(mBinding.iconBookTour, "logo")).toBundle()
            )
        }

        mBinding.viewLogin.setOnClickListener {
            startActivity(
                Intent(this, LoginActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(mBinding.iconLogin, "iconLogo")).toBundle()
            )
        }

        mBinding.viewYourTicket.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                    return@setOnClickListener
                }
            }
            val files = File(Constant.TICKET_FOLDER).listFiles()
            if (files.isNullOrEmpty()) {
                showToast(getString(R.string.no_ticket_found))
            } else {
                val tickets = ArrayList<File>()
                for (file in files) {
                    val name = file.name
                    if (name.toLowerCase(Locale.US).endsWith(".png")) {
                        try {
                            name.substring(0, 13).toLong()
                            name.substring(13, name.length - 4).toInt().toString()
                            tickets.add(0, file)
                        } catch (e: Exception) {
                            tickets.add(file)
                        }
                    }
                }
                with(Dialog(this)) {
                    val recyclerView = RecyclerView(this@HomeActivity)
                    recyclerView.adapter = MyTicketAdapter(this@HomeActivity, tickets)
                    recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
                    setContentView(recyclerView)
                    window?.attributes?.width = RecyclerView.LayoutParams.MATCH_PARENT
                    window?.attributes?.height = RecyclerView.LayoutParams.MATCH_PARENT
                    show()
                }
            }
        }

        mBinding.viewUpdateProfile.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).putExtra("isUpdate", true))
        }

        mBinding.viewAddTour.setOnClickListener {
            startActivity(Intent(this, AddTourActivity::class.java))
        }

        mBinding.viewLogout.setOnClickListener {
            mViewModel.user.value = null
            mViewModel.sharedPreferencesHelper[Constant.PREF_USER] = null
        }

        mBinding.viewScanTicket.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
                }
            } else {
                startActivity(Intent(this, ScanTicketActivity::class.java))
            }
            mBinding.viewContainer.closeDrawer(mBinding.viewMenuLeft)
        }
    }

    override fun onResume() {
        super.onResume()
        mBinding.viewAnimate1.clearAnimation()
        mBinding.viewAnimate2.clearAnimation()
        mBinding.viewAnimate3.clearAnimation()
        mBinding.viewAnimate1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.home_logo_zoom_in))
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.viewAnimate2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.home_logo_zoom_in))
            Handler(Looper.getMainLooper()).postDelayed({
                mBinding.viewAnimate3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.home_logo_zoom_in))
            }, 1000)
        }, 1000)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) {
                mBinding.viewYourTicket.performClick()
            } else if (requestCode == 1) {
                startActivity(Intent(this, ScanTicketActivity::class.java))
            }
        }
    }
}