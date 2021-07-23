package com.app.bestbus.ui.scanTicket

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.bestbus.R
import com.app.bestbus.base.BaseActivity
import com.app.bestbus.databinding.ActivityScanTicketBinding
import com.app.bestbus.utils.BindingAdapter.setVisible
import com.app.bestbus.utils.Util
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanTicketActivity : BaseActivity(), BarcodeCallback {
    private lateinit var mBinding: ActivityScanTicketBinding
    private val mViewModel: ScanTicketViewModel by viewModels()
    private var isScanning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan_ticket)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScan()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1000)
        }

        with(mBinding) {
            scannerView.setStatusText(null)
            btnScanMore.setOnClickListener {
                if (isScanning) {
                    isScanning = false
                    btnScanMore.text = getString(R.string.scan_more)
                    scannerView.pause()
                } else {
                    isScanning = true
                    tvTicket.text = ""
                    btnScanMore.text = getString(android.R.string.cancel)
                    scannerView.resume()
                }
            }

            mViewModel.loading.observe {
                progressBar.setVisible(it)
            }
        }
    }

    private fun startScan() {
        mBinding.scannerView.setStatusText(null)
        mBinding.scannerView.decodeSingle(this)
    }

    override fun barcodeResult(result: BarcodeResult?) {
        isScanning = false
        with(mBinding) {
            btnScanMore.text = getString(R.string.scan_more)
            scannerView.pause()
            if (result?.text == null) {
                tvTicket.text = getString(R.string.an_error_occurred)
            } else {
                mViewModel.scanTicket(result.text) {
                    var seat = ""
                    for (i in it.seatList) {
                        var s: Int = i / (it.tourData!!.count * 2) + 65
                        if (s > 90) {
                            s += 6
                        }
                        seat += s.toChar().toString().plus(i % (it.tourData!!.count * 2) + 1) + ", "
                    }
                    val ticketInformation = """
                            ID: ${it.id}
                            Name: ${it.name}
                            Email: ${it.email}
                            Phone: ${it.phone}
                            Bus: ${it.tourData!!.tourName}
                            Seat: ${seat.substring(0, seat.length - 2) + "."}
                            From: ${it.tourData.fromCity}
                            To: ${it.tourData.toCity}
                            Start time: ${it.tourData.startTime}
                            Start date: ${it.date}
                            End time: ${Util.getEndTime(it.tourData.startTime, it.tourData.time)}
                            End date: ${Util.getEndDate(it.date, it.tourData.startTime, it.tourData.time)}
                            Time: ${getString(R.string.hours, Util.formatFloat(it.tourData.time))}
                            Payment method: ${it.paymentMethod}
                            --------------------------
                            Total Amount: USD ${Util.formatFloat(it.totalAmount)}
                            """.trimIndent()
                    tvTicket.text = ticketInformation
                }
            }
        }
    }

    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1000) {
                startScan()
            }
        }
    }

    override fun onResume() {
        mBinding.scannerView.resume()
        super.onResume()
    }

    override fun onPause() {
        mBinding.scannerView.pause()
        super.onPause()
    }
}