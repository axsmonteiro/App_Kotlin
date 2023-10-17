package com.example.myapplication


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


open class TelaDadosPessoais : AppCompatActivity() {

    private lateinit var username: TextView
    private lateinit var emailUser: TextView
    private lateinit var logOutButton : Button
    private lateinit var editButton : Button

    private var db = Firebase.firestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_dados_pessoais)

        supportActionBar?.hide()

        username = findViewById(R.id.textNomeUser)
        emailUser = findViewById(R.id.textEmailUser)
        logOutButton = findViewById(R.id.bt_logout)
        editButton = findViewById(R.id.bt_editar)

        logOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
            finish()
        }

        editButton.setOnClickListener{
            val intent = Intent(this, EditarDados::class.java)
            startActivity(intent)
        }


        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("Utilizadores").document(userId)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val nome = it.data?.get("nome")?.toString()
                val email = it.data?.get("email")?.toString()

                username.text = nome
                emailUser.text = email


            }
        }
            .addOnFailureListener {

                val snackbar = Snackbar.make(findViewById(android.R.id.content),"Erro!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }


    }


}
