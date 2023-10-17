package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityTelaInicialBinding

class TelaInicial : AppCompatActivity() {
    private var requestCamera: ActivityResultLauncher<String>? = null
    private lateinit var binding: ActivityTelaInicialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaInicialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(this, BarCodeScan::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }

        binding.conteinerComponents3.setOnClickListener {
            if (isCameraPermissionGranted()) {
                val intent = Intent(this, BarCodeScan::class.java)
                startActivity(intent)
            } else {
                requestCameraPermission()
            }
        }

        supportActionBar?.hide()

        val bt_conta: ImageButton = findViewById(R.id.info_conta)
        bt_conta.setOnClickListener {
            val intent = Intent(this, TelaDadosPessoais::class.java)
            startActivity(intent)
        }

        val bt_Map: ImageButton = findViewById(R.id.conteinerComponents3)
        bt_Map.setOnClickListener {
            if (isCameraPermissionGranted()) {
                val intent = Intent(this, BarCodeScan::class.java)
                startActivity(intent)
            } else {
                requestCameraPermission()
            }
        }

        val bt_listStock: ImageButton = findViewById(R.id.conteinerComponents2)
        bt_listStock.setOnClickListener {
            val intent = Intent(this, ListaProdEditar::class.java)
            startActivity(intent)
        }

        val bt_historico: ImageButton = findViewById(R.id.conteinerComponents1)
        bt_historico.setOnClickListener {
            val intent = Intent(this, HistoricoVendas::class.java)
            startActivity(intent)
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestCamera?.launch(Manifest.permission.CAMERA)
    }

    override fun onBackPressed() {
        // não chame o super desse método
    }
}
