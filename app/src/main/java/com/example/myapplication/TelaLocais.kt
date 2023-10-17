package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class TelaLocais : AppCompatActivity() {


    val callIntent: Intent = Uri.parse("tel:231458952").let { number ->
        Intent(Intent.ACTION_DIAL, number)
    }
    val webIntent1: Intent = Uri.parse("https://www.google.com/maps/place/Teclago-Equipamentos+Industriais,+Lda./@40.4375128,-8.7460115,15z/data=!4m2!3m1!1s0x0:0x4a8c489b3166799a?sa=X&ved=2ahUKEwiC2c30pIL_AhWDVaQEHX8bDuIQ_BJ6BAg9EAc").let { webpage ->
        Intent(Intent.ACTION_VIEW, webpage)
    }
    val webIntent2: Intent = Uri.parse("https://www.google.com/maps/place/40%C2%B026'02.5%22N+8%C2%B044'31.5%22W/@40.434034,-8.7427337,269m/data=!3m1!1e3!4m4!3m3!8m2!3d40.434034!4d-8.74209").let { webpage ->
        Intent(Intent.ACTION_VIEW, webpage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_locais)

        supportActionBar?.hide()




        val buttonAz = findViewById<TextView>(R.id.bt_armazem)

        buttonAz.setOnClickListener{

            try {
                startActivity(webIntent1)

            } catch (e: Exception) {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Operação não disponível!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
        }

        val buttonLj = findViewById<TextView>(R.id.bt_loja)
        buttonLj.setOnClickListener{

            try {
            startActivity(webIntent2)

            } catch (e: Exception) {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Operação não disponível!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
        }

        val buttonCon = findViewById<TextView>(R.id.bt_fixo)
        buttonCon.setOnClickListener{
            try {

                startActivity(callIntent)

            } catch (e: Exception) {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Operação não disponível!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }

        }
    }

}