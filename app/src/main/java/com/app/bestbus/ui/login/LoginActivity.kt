package com.app.bestbus.ui.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityLoginBinding
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.utils.BindingAdapter.loadImage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var mBinding: ActivityLoginBinding
    private val mViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mBinding.viewModel = mViewModel

        with(mBinding) {
            if (mViewModel.isUpdate) {
                edtName.visibility = View.VISIBLE
                edtPhone.visibility = View.VISIBLE
                edtConfirmPassword.visibility = View.VISIBLE
                btnLogin.text = getString(R.string.save)
                tvSignUp.visibility = View.GONE
                if (mViewModel.imageUri != null) {
                    imvLogo.setImageURI(mViewModel.imageUri)
                } else if (mViewModel.user?.image != null) {
                    imvLogo.loadImage(mViewModel.user!!.image, resources.getDimensionPixelSize(R.dimen.home_logo_size).toFloat(), true)
                }
                edtName.setText(mViewModel.name)
                edtEmail.setText(mViewModel.email)
                edtPhone.setText(mViewModel.phone)
                edtPassword.setText(mViewModel.password)
                edtConfirmPassword.setText(mViewModel.confirmPassword)
                imvLogo.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
                        }
                    } else {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 0)
                    }
                }
            } else {
                tvSignUp.setOnClickListener {
                    if (mViewModel.isLogin.value!!) {
                        btnLogin.text = getString(R.string.register)
                        tvSignUp.text = getString(R.string.login)
                        edtName.visibility = View.VISIBLE
                        mViewModel.isLogin.value = false
                    } else {
                        btnLogin.text = getString(R.string.login)
                        tvSignUp.text = getString(R.string.register)
                        edtName.visibility = View.GONE
                        mViewModel.isLogin.value = true
                    }
                }
            }

            btnLogin.setOnClickListener {
                if (!isValid()) {
                    return@setOnClickListener
                }
                mViewModel.login(mViewModel.imageUri?.getRealPath()) {
                    startActivity(
                        Intent(this@LoginActivity, HomeActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }

        mViewModel.loading.observe {
            showLoading(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            mBinding.imvLogo.setImageURI(data.data)
            mViewModel.imageUri = data.data
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun Uri?.getRealPath(): File? {
        if (this != null) {
            contentResolver.query(this, arrayOf("_data"), null, null, null)?.apply {
                moveToFirst()
                return File(getString(getColumnIndexOrThrow("_data")))
            }
        }
        return null
    }

    private fun isValid(): Boolean {
        with(mBinding) {
            var b = true
            if (mViewModel.email.isEmpty()) {
                edtEmail.error = getString(R.string.tv_please_enter_email)
                b = false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(mViewModel.email).matches()) {
                edtEmail.error = getString(R.string.tv_invalid_email)
                b = false
            }
            if (mViewModel.password.isEmpty()) {
                edtPassword.error = getString(R.string.tv_please_enter_password)
                b = false
            }
            if (mViewModel.isUpdate) {
                if (mViewModel.password != mViewModel.confirmPassword) {
                    edtConfirmPassword.error = getString(R.string.tv_invalid_confirm_password)
                    b = false
                }
            } else if (!mViewModel.isLogin.value!! && mViewModel.name.isEmpty()) {
                edtName.error = getString(R.string.tv_please_enter_your_name)
                b = false
            }
            return b
        }
    }
}