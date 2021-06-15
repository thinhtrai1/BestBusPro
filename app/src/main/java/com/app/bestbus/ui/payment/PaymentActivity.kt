package com.app.bestbus.ui.payment

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityPaymentBinding
import com.app.bestbus.databinding.DialogBookingSuccessBinding
import com.app.bestbus.ui.home.HomeActivity
import com.app.bestbus.utils.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

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
            imvHome.setOnClickListener {
                startActivity(
                    Intent(this@PaymentActivity, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
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
                    newSelect(edtCodeShipping)
                }
            }
            edtAtBusStation.setOnClickListener {
                newSelect(edtAtBusStation)
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

    private fun newSelect(viewSelect: View) {
        mViewModel.viewSelecting?.isSelected = false
        mViewModel.viewSelecting = viewSelect
        mViewModel.viewSelecting!!.isSelected = true
    }

    private fun booking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                return
            }
        }
        mViewModel.booking {
            showDialogSuccess(true)
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
                        Thread(Runnable {
                            val bitmap = Bitmap.createBitmap(binding.layoutTicket.width, binding.layoutTicket.height, Bitmap.Config.ARGB_8888)
                            binding.layoutTicket.draw(Canvas(bitmap))
                            val path = File(Constant.TICKET_FOLDER)
                            if (!path.exists()) {
                                path.mkdirs()
                            }
                            val imageFile = File(path, Util.getTicketFileName(it.tourData.date, it.tourData.startTime, it.id))
                            val outputStream = FileOutputStream(imageFile)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
                            outputStream.flush()
                            outputStream.close()
                        }).start()
                    }
                }
            }
        }
    }

    private fun generateQRCode(data: String?, size: Int = 256): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        try {
//            val hints: MutableMap<EncodeHintType?, Any?> = EnumMap(EncodeHintType::class.java)
//            hints[EncodeHintType.MARGIN] = 0
//            val bitMatrix = MultiFormatWriter().encode(code, BarcodeFormat.QR_CODE, size, size, hints)
            val bitMatrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.WHITE else Color.TRANSPARENT)
                }
            }
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) booking()
        }
    }
}