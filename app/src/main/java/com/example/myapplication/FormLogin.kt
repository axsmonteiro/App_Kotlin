package com.example.myapplication


import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FormLogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_login)

        supportActionBar?.hide()


        auth = FirebaseAuth.getInstance()


        val registerText : TextView = findViewById(R.id.text_registo)

        registerText.setOnClickListener{
            val intent = Intent(this, FormRegisto::class.java)
            startActivity(intent)
        }

        //login
        val loginButton: Button = findViewById(R.id.bt_add)

        loginButton.setOnClickListener {

            performLogin()

        }

    }

    private fun validarTipoConta(uid: String) {

        if (uid == "lfKG0NYTrbMoj884uiBVtSjPX6v2") {
            val intent = Intent(this, TelaInicial::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart(){
        super.onStart()
        val userAtual = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        if(userAtual != null){

            validarTipoConta(uid)
        }
    }

    private fun performLogin() {
        //get inputs
        val email: EditText = findViewById(R.id.edit_email)
        val passwd: EditText = findViewById(R.id.edit_password)

        //check if null inputs
        if (email.text.isEmpty() || passwd.text.isEmpty()){
            val snackbar = Snackbar.make(findViewById(android.R.id.content), "Preencha todos os campos, por favor", Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
            return
        }

        val emailInput = email.text.toString()
        val passwdInput = passwd.text.toString()

        if (!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {




                // Verificar se o email é válido
                if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Email inválido!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                    return
                }

                // Verificar se o email e a senha correspondem a uma conta existente
                auth.signInWithEmailAndPassword(emailInput, passwdInput)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Autenticação bem-sucedida, navegar para a tela principal

                            val snackbar = Snackbar.make(findViewById(android.R.id.content), "Sucesso", Snackbar.LENGTH_SHORT)
                            snackbar.setBackgroundTint(Color.GREEN)
                            snackbar.show()

                            val user= auth.currentUser
                            val uid = user?.uid
                            if (uid != null) {
                                validarTipoConta(uid)
                                Log.w("textotextotextotextotexto", "Error adding $uid")

                            } else {
                                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Erro!", Snackbar.LENGTH_SHORT)
                                snackbar.setBackgroundTint(Color.RED)
                                snackbar.show()
                            }

                        } else {
                            // Exibir mensagem de erro apropriada com base na exceção retornada
                            val exception = task.exception
                            if (exception is FirebaseAuthInvalidUserException && exception.errorCode == "ERROR_USER_NOT_FOUND") {
                                val snackbar = Snackbar.make(findViewById(android.R.id.content), "A senha  e/ou email estão incorretos!", Snackbar.LENGTH_SHORT)
                                snackbar.setBackgroundTint(Color.RED)
                                snackbar.show()
                            } else if (exception is FirebaseAuthInvalidCredentialsException && exception.errorCode == "ERROR_WRONG_PASSWORD") {
                                val snackbar = Snackbar.make(findViewById(android.R.id.content), "A senha  e/ou email estão incorretos!", Snackbar.LENGTH_SHORT)
                                snackbar.setBackgroundTint(Color.RED)
                                snackbar.show()
                            } else {
                                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Erro ao fazer login.", Snackbar.LENGTH_SHORT)
                                snackbar.setBackgroundTint(Color.RED)
                                snackbar.show()
                            }
                        }
                    }


        } else {
            val snackbar = Snackbar.make(findViewById(android.R.id.content),"Email inválido!",Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
        }
    }

}

