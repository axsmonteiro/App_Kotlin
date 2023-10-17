package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.widget.Toast
import com.example.myapplication.databinding.ActivityBarCodeScanBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class BarCodeScan : AppCompatActivity() {
    private lateinit var binding: ActivityBarCodeScanBinding
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    var interntData = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarCodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

    }

    private fun passarDados() {
        val dado = binding.textBarcodeValue.text.toString()
        val intent = Intent(this, TelaInsereProduto::class.java)
        intent.putExtra("id_produto", dado)
        startActivity(intent)
    }

    private fun iniBc() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .build()
        binding.surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(p0: SurfaceHolder) {
                try {
                    cameraSource.start(binding.surfaceView!!.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                cameraSource.stop()
            }

        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Código de barras captado!",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setBackgroundTint(Color.GREEN)
                snackbar.show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                val barcodes = detections?.detectedItems
                if (barcodes != null) {
                    if (barcodes.size() != 0) {
                        binding.textBarcodeValue!!.post {
                            binding.btAction!!.text = "SERCH ITEN"
                            interntData = barcodes.valueAt(0).displayValue
                            binding.textBarcodeValue.setText(interntData)
                            binding.btAction.setOnClickListener {
                                if (!isConnectedToInternet()) {
                                    val snackbar = Snackbar.make(
                                        findViewById(android.R.id.content),
                                        "Sem conexão com a Internet",
                                        Snackbar.LENGTH_SHORT
                                    )
                                    snackbar.setBackgroundTint(Color.RED)
                                    snackbar.show()
                                    return@setOnClickListener
                                }
                                val snackbar = Snackbar.make(
                                    findViewById(android.R.id.content),
                                    "Código de barras captado!",
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.setBackgroundTint(Color.GREEN)
                                snackbar.show()
                                passarDados()
                            }
                        }
                    }
                }
            }

        })
    }

    private fun isConnectedToInternet(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        iniBc()
    }
}
