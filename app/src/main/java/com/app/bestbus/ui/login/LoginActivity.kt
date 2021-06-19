package com.app.bestbus.ui.login

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityLoginBinding
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.utils.BindingAdapter.loadImage
import com.app.bestbus.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var mBinding: ActivityLoginBinding
    private val mViewModel: LoginViewModel by viewModels()
    private var mTempFile: File? = null

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

            imvLogo.setOnClickListener {
                AlertDialog.Builder(this@LoginActivity)
                    .setPositiveButton(getString(R.string.from_gallery)) { _, _ ->
                        if (ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
                            }
                        } else {
                            mPickForResult.launch("image/*")
                        }
                    }
                    .setNegativeButton(getString(R.string.take_a_picture)) { _, _ ->
                        if (ContextCompat.checkSelfPermission(this@LoginActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 1)
                            }
                        } else {
                            takeImage()
                        }
                    }
                    .setTitle(getString(R.string.choose_one))
                    .show()
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
                mPickForResult.launch("image/*")
            } else if (requestCode == 1) {
                takeImage()
            }
        }
    }

    private val mPickForResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            editImage(it)
        }
    }

    private fun takeImage() {
        mTempFile = File.createTempFile("temp_", ".jpg", externalCacheDir)
        val tempUri = FileProvider.getUriForFile(this@LoginActivity, "com.app.bestbus.fileprovider", mTempFile!!)
        mTakeForResult.launch(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).putExtra(MediaStore.EXTRA_OUTPUT, tempUri)
        )
    }

    private val mTakeForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            MediaScannerConnection.scanFile(this, arrayOf(mTempFile!!.path), null) { _, uri ->
                editImage(uri)
            }
        }
    }

    private fun editImage(uri: Uri) {
        mViewModel.imageUri = uri
        val editIntent = Intent(Intent.ACTION_EDIT)
            .setDataAndType(uri, "image/*")
//            .setPackage("com.android.gallery3d")
            .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            mEditForResult.launch(editIntent)
        } catch (e: ActivityNotFoundException) {
            showToast("Cannot edit photo")
        }
    }

    private val mEditForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.let {
            mViewModel.imageUri = it
        }
        mBinding.imvLogo.setImageURI(mViewModel.imageUri)
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

    override fun onDestroy() {
        externalCacheDir?.delete()
        super.onDestroy()
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