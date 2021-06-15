package com.app.bestbus.ui.addTour

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityAddTourBinding
import com.app.bestbus.databinding.DialogTimePickerBinding
import com.app.bestbus.utils.ApiResult
import com.app.bestbus.utils.ApiService
import com.app.bestbus.utils.showErrorToast
import com.app.bestbus.utils.showToast
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddTourActivity : BaseActivity() {
    @Inject
    lateinit var apiService: ApiService

    private lateinit var mBinding: ActivityAddTourBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_tour)

        mBinding.edtStartTime.setOnClickListener {
            Dialog(this).apply {
                val binding = DialogTimePickerBinding.inflate(LayoutInflater.from(this@AddTourActivity))
                setContentView(binding.root)
                show()
                binding.btnCancel.setOnClickListener {
                    dismiss()
                }
                binding.btnOk.setOnClickListener {
                    dismiss()
                    mBinding.edtStartTime.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String.format("%02d", binding.timerPicker.hour) + ":" + String.format("%02d", binding.timerPicker.minute)
                    } else {
                        @Suppress("DEPRECATION")
                        String.format("%02d", binding.timerPicker.currentHour) + ":" + String.format("%02d", binding.timerPicker.currentMinute)
                    }
                }
            }
        }

        mBinding.btnConfirm.setOnClickListener {
            if (!isValid()) {
                return@setOnClickListener
            }
            showLoading(true)
            lifecycleScope.launch {
                ApiResult.getResult {
                    with(mBinding) {
                        apiService.addTour(
                            edtTourName.text.toString().trim(),
                            edtOldPrice.text.toString(),
                            edtPrice.text.toString(),
                            edtStartTime.text.toString(),
                            edtTime.text.toString(),
                            edtFrom.text.toString().trim(),
                            edtTo.text.toString().trim(),
                            edtSeatQuantity.text.toString(),
                            edtCount.text.toString(),
                            edtVAT.text.toString()
                        )
                    }
                }.let {
                    showLoading(false)
                    if (it is ApiResult.Success) {
                        showToast(it.data.toString())
                    } else {
                        showErrorToast(it)
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        with(mBinding) {
            var b = true
            if (edtTourName.text.isBlank()) {
                edtTourName.error = ""
                b = false
            }
            if (edtOldPrice.text.isBlank()) {
                edtOldPrice.setText(edtPrice.text.toString())
            }
            if (edtPrice.text.isBlank()) {
                edtPrice.error = ""
                b = false
            }
            if (edtVAT.text.isBlank()) {
                edtVAT.setText("0.05")
            }
            if (edtStartTime.text.isBlank()) {
                edtStartTime.error = ""
                b = false
            }
            if (edtTime.text.isBlank()) {
                edtTime.error = ""
                b = false
            }
            if (edtFrom.text.isBlank()) {
                edtFrom.error = ""
                b = false
            }
            if (edtTo.text.isBlank()) {
                edtTo.error = ""
                b = false
            }
            if (edtSeatQuantity.text.isBlank()) {
                edtSeatQuantity.error = ""
                b = false
            }
            if (edtCount.text.isBlank()) {
                edtCount.setText("3")
            }
            return b
        }
    }
}