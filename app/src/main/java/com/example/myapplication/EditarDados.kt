package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EditarDados : AppCompatActivity() {

    private lateinit var username : EditText
    private lateinit var emailUser : EditText
    private lateinit var passwdUser : EditText
    private lateinit var submitButton : Button

    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_dados)

        supportActionBar?.hide()

        auth = Firebase.auth

        username = findViewById(R.id.textNomeUser)
        emailUser = findViewById(R.id.textEmailUser)

        passwdUser = findViewById(R.id.textPassUser)

        submitButton = findViewById(R.id.bt_submit)


        submitButton.setOnClickListener{
            // Obtém o ID do usuário atualmente logado
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            // Obtém os valores dos campos de texto
            val nome = username.text.toString()
            val email = emailUser.text.toString()
            val password = passwdUser.text.toString()

            Log.d(this.toString(),nome)


            // Verifica se os campos não estão vazios
            if (nome.isNotEmpty() && email.isNotEmpty() && password.isEmpty()) {

                // Valida os dados inseridos nos campos de texto


                    // Atualiza os campos desejados do documento
                    val ref = db.collection("Utilizadores").document(userId)
                    ref.update(mapOf(
                        "nome" to nome,
                        "email" to email

                    )).addOnSuccessListener {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Dados atualizados com sucesso!",Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()

                        updateUserEmail(email)

                        val intent = Intent(this, TelaDadosPessoais::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Erro ao atualizar os dados!",Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }


            } else if(nome.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {



                // Atualiza os campos desejados do documento
                val ref = db.collection("Utilizadores").document(userId)
                ref.update(mapOf(
                    "nome" to nome,
                    "email" to email,
                    "password" to password
                )).addOnSuccessListener {
                    val snackbar = Snackbar.make(findViewById(android.R.id.content),"Dados atualizados com sucesso!",Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.GREEN)
                    snackbar.show()

                    updateUserEmail(email)
                    updateUserPassword(password)

                    val intent = Intent(this, TelaDadosPessoais::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    val snackbar = Snackbar.make(findViewById(android.R.id.content),"Erro ao atualizar os dados!",Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }

            }



        }

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("Utilizadores").document(userId)
        ref.get().addOnSuccessListener {
            if (it != null) {
                val nome = it.data?.get("nome")?.toString()
                val email = it.data?.get("email")?.toString()

                username.setText(nome)
                emailUser.setText(email)


            }
        }
            .addOnFailureListener {
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"Erro!",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }

    }

    private fun updateUserEmail(email: String) {

        // Get the current user
        val user = FirebaseAuth.getInstance().currentUser

        // Check to see if the user is null
        requireNotNull(user) { "User must not be null" }

        // Update the user's password
        user.apply {

            updateEmail(email)
        }

        // Show a message to the user
        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Email alterado com sucesso!",Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.GREEN)
        snackbar.show()
    }


    private fun updateUserPassword(password: String) {

        // Get the current user
        val user = FirebaseAuth.getInstance().currentUser

        // Check to see if the user is null
        requireNotNull(user) { "User must not be null" }

        // Update the user's password
        user.apply {
            updatePassword(password)

        }

        // Show a message to the user
        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Palavra-passe alterada com sucesso!",Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.GREEN)
        snackbar.show()
    }
}


