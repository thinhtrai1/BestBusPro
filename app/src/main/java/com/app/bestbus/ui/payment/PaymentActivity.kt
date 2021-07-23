package com.app.bestbus.ui.payment

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityPaymentBinding
import com.app.bestbus.databinding.DialogBookingSuccessBinding
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.utils.Util
import com.app.bestbus.utils.isPermissionGranted
import com.app.bestbus.utils.showToast
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentActivity : BaseActivity() {
    private lateinit var mBinding: ActivityPaymentBinding
    private val mViewModel: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        mBinding.viewModel = mViewModel

        if (mViewModel.ticket != null) {
            showDialogSuccess(false)
            return
        }

        with(mBinding) {
            when(mViewModel.viewSelecting?.id) {
                R.id.edtCreditCard -> {
                    edtCreditCard.requestFocus()
                    edtCreditCard.isSelected = true
                }
                R.id.edtNetBanking -> {
                    edtNetBanking.requestFocus()
                    edtNetBanking.isSelected = true
                }
                R.id.edtCodeShipping -> {
                    edtCodeShipping.requestFocus()
                    edtCodeShipping.isSelected = true
                }
                R.id.edtAtBusStation -> {
                    edtAtBusStation.isSelected = true
                }
            }
            edtCreditCard.setOnClickListener {
                AlertDialog
                    .Builder(this@PaymentActivity)
                    .setMessage(getString(R.string.coming_soon))
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
            edtNetBanking.setOnClickListener {
                AlertDialog
                    .Builder(this@PaymentActivity)
                    .setMessage(getString(R.string.coming_soon))
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
            edtCodeShipping.setOnFocusChangeListener { _, b ->
                if (b) {
                    mViewModel.newSelect(edtCodeShipping)
                }
            }
            edtAtBusStation.setOnClickListener {
                mViewModel.newSelect(edtAtBusStation)
                edtCodeShipping.clearFocus()
            }
            btnConfirm.setOnClickListener {
                if (mViewModel.viewSelecting == null) {
                    showToast(getString(R.string.please_choose_payment_method))
                    return@setOnClickListener
                }
                AlertDialog
                    .Builder(this@PaymentActivity)
                    .setMessage(getString(R.string.are_your_sure))
                    .setPositiveButton(android.R.string.ok) { _, _ -> booking() }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }

        mViewModel.loading.observe {
            showLoading(it)
        }
    }

    private fun booking() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q || isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mViewModel.booking {
                showDialogSuccess(true)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }
    }
    
    private fun showDialogSuccess(isNew: Boolean) {
        mViewModel.ticket!!.let {
            var seat = ""
            for (i in 0 until it.seatList.size) {
                if (i == 15) {
                    seat = seat.substring(0, seat.length - 2) + "... (+${it.seatList.size - 15})  "
                    break
                } else {
                    var s: Int = it.seatList[i] / (it.tourData!!.count * 2) + 65
                    if (s > 90) {
                        s += 6
                    }
                    seat += s.toChar().toString().plus(it.seatList[i] % (it.tourData!!.count * 2) + 1) + ", "
                }
            }
            val ticketInformation = getString(R.string.id) + ": " + it.id + "\n" +
                    getString(R.string.your_name) + ": " + it.name + "\n" +
                    getString(R.string.email) + ": " + it.email + "\n" +
                    getString(R.string.phone) + ": " + it.phone + "\n" +
                    getString(R.string.bus) + ": " + it.tourData!!.tourName + "\n" +
                    getString(R.string.seats) + " " + seat.substring(0, seat.length - 2) + "." + "\n" +
                    getString(R.string.from) + ": " + it.tourData.fromCity + "\n" +
                    getString(R.string.to) + ": " + it.tourData.toCity + "\n" +
                    getString(R.string.start_time) + ": " + it.tourData.startTime + ", " + it.date + "\n" +
//                    getString(R.string.start_date) + ": " + it.date + "\n" +
                    getString(R.string.end_time) + ": " + Util.getEndTime(it.tourData.startTime, it.tourData.time) +
                    ", " + Util.getEndDate(it.date, it.tourData.startTime, it.tourData.time) + "\n" +
//                    getString(R.string.end_date) + ": " + Util.getEndDate(it.date, it.tourData.startTime, it.tourData.time) + "\n" +
                    getString(R.string.time) + ": " + getString(R.string.hours, Util.formatFloat(it.tourData.time)) + "\n" +
                    getString(R.string.payment_method) + ": " + it.paymentMethod + "\n" +
                    "--------------------------" + "\n" +
                    getString(R.string.total_amount) + " USD " + Util.formatFloat(it.totalAmount)
            Dialog(this@PaymentActivity).apply {
                val binding = DialogBookingSuccessBinding.inflate(LayoutInflater.from(this@PaymentActivity))
                setContentView(binding.root)
                setCancelable(false)
                window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                window?.attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
                window?.attributes?.height = WindowManager.LayoutParams.MATCH_PARENT
                show()
                binding.tvInformation.text = ticketInformation
                binding.imvQRCode.setImageBitmap(generateQRCode(it.qrCode))
                binding.btnFinish.setOnClickListener {
                    startActivity(
                        Intent(this@PaymentActivity, HomeActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
                if (isNew) {
                    binding.layoutTicket.post {
                        lifecycleScope.launch(Dispatchers.IO) {
                            val width = binding.layoutTicket.width
                            val height = binding.layoutTicket.height
                            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            binding.layoutTicket.draw(Canvas(bitmap))
//                            val path = File(Constant.TICKET_FOLDER)
//                            if (!path.exists()) {
//                                path.mkdirs()
//                            }
//                            val imageFile = File(path, Util.getTicketFileName(it.tourData.date, it.tourData.startTime, it.id))
//                            FileOutputStream(imageFile).use { outputStream ->
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
//                            }

                            val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                            } else {
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            }
                            val contentValues = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, Util.getTicketFileName(it.tourData.date, it.tourData.startTime, it.id))
                                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                put(MediaStore.Images.Media.WIDTH, width)
                                put(MediaStore.Images.Media.HEIGHT, height)
                            }
                            contentResolver.insert(imageUri, contentValues)?.let { uri ->
                                contentResolver.openOutputStream(uri).use { outputStream ->
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generateQRCode(data: String?, size: Int = 256): Bitmap? {
        return try {
            val bitMatrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    pixels[width * y + x] = if (bitMatrix.get(x, y)) Color.WHITE else Color.TRANSPARENT
                }
            }
            Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) booking()
        }
    }
}