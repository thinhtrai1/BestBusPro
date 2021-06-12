package com.app.bestbus.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.bestbus.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProgressDialog = Dialog(this).apply {
            setContentView(R.layout.progress_dialog)
            setCancelable(false)
            window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            mProgressDialog.show()
        } else {
            mProgressDialog.dismiss()
        }
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(R.anim.anim_scale_open, R.anim.anim_scale_exit)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_scale_open, R.anim.anim_scale_exit)
    }
}