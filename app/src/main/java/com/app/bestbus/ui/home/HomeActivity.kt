package com.app.bestbus.ui.home

import android.Manifest
import android.app.ActivityOptions
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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearSnapHelper
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityHomeBinding
import com.app.bestbus.ui.addTour.AddTourActivity
import com.app.bestbus.ui.login.LoginActivity
import com.app.bestbus.ui.scanTicket.ScanTicketActivity
import com.app.bestbus.ui.searchTour.SearchTourActivity
import com.app.bestbus.utils.Constant
import com.app.bestbus.utils.isPermissionGranted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private lateinit var mBinding: ActivityHomeBinding
    private val mViewModel: HomeViewModel by viewModels()
    private val mAnimationHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        LinearSnapHelper().attachToRecyclerView(mBinding.rcvBestDeals)

        mViewModel.dealAdapter.itemLayout = ViewGroup.LayoutParams(resources.displayMetrics.widthPixels * 3 / 4, ViewGroup.LayoutParams.MATCH_PARENT)

        mBinding.viewAnimate1.animation = AnimationUtils.loadAnimation(this, R.anim.home_logo_zoom_in)
        mBinding.viewAnimate2.animation = AnimationUtils.loadAnimation(this, R.anim.home_logo_zoom_in)
        mBinding.viewAnimate3.animation = AnimationUtils.loadAnimation(this, R.anim.home_logo_zoom_in)

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

//        mBinding.viewYourTicket.setOnClickListener {
//            if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
//                return@setOnClickListener
//            }
//            val files = File(Constant.TICKET_FOLDER).listFiles()
//            if (files.isNullOrEmpty()) {
//                showToast(getString(R.string.no_ticket_found))
//            } else {
//                val tickets = ArrayList<File>()
//                for (file in files) {
//                    val name = file.name
//                    if (name.toLowerCase(Locale.US).endsWith(".png")) {
//                        try {
//                            name.substring(0, 13).toLong()
//                            name.substring(13, name.length - 4).toInt().toString()
//                            tickets.add(0, file)
//                        } catch (e: Exception) {
//                            tickets.add(file)
//                        }
//                    }
//                }
//                with(Dialog(this)) {
//                    val recyclerView = RecyclerView(this@HomeActivity)
//                    recyclerView.adapter = MyTicketAdapter(this@HomeActivity, tickets)
//                    recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
//                    setContentView(recyclerView)
//                    window?.attributes?.width = RecyclerView.LayoutParams.MATCH_PARENT
//                    window?.attributes?.height = RecyclerView.LayoutParams.MATCH_PARENT
//                    show()
//                }
//            }
//        }

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
            if (isPermissionGranted(Manifest.permission.CAMERA)) {
                startActivity(Intent(this, ScanTicketActivity::class.java))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            }
            mBinding.viewContainer.closeDrawer(mBinding.viewMenuLeft)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) {
//                mBinding.viewYourTicket.performClick()
            } else if (requestCode == 1) {
                startActivity(Intent(this, ScanTicketActivity::class.java))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mBinding.viewAnimate1.animation.cancel()
        mBinding.viewAnimate2.animation.cancel()
        mBinding.viewAnimate3.animation.cancel()
    }

    override fun onResume() {
        mBinding.viewAnimate1.animation.start()
        mAnimationHandler.postDelayed({
            mBinding.viewAnimate2.animation.start()
            mAnimationHandler.postDelayed({
                mBinding.viewAnimate3.animation.start()
            }, 1000)
        }, 1000)
        super.onResume()
    }

    override fun onDestroy() {
        mAnimationHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}