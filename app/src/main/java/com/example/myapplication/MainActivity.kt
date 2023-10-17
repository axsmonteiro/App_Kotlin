package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val bt_conta : ImageButton = findViewById(R.id.info_conta)
        val bt_compra : ImageButton = findViewById(R.id.conteinerComponents1)

        bt_compra.setOnClickListener{
            val intent = Intent(this, BuyProd::class.java)
            startActivity(intent)
        }
        val bt_historico : ImageButton = findViewById(R.id.conteinerComponents2)

        bt_historico.setOnClickListener{
            val intent = Intent(this, VerCompras::class.java)
            startActivity(intent)
        }

        bt_conta.setOnClickListener{
            val intent = Intent(this, TelaDadosPessoais::class.java)
            startActivity(intent)
        }
        val bt_Map : ImageButton = findViewById(R.id.conteinerComponents3)

        bt_Map.setOnClickListener{
            val intent = Intent(this, TelaLocais::class.java)
            startActivity(intent)
        }

    }
    override fun onBackPressed() {
        // não chame o super desse método
    }
}