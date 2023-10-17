package com.example.myapplication


import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FormRegisto : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_registo)

        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth


        val registerButton : Button = findViewById(R.id.bt_registo)

        registerButton.setOnClickListener {
            if (isNetworkConnected()) {
                performSignUp()
            } else {
                showNoInternetSnackbar()
            }
        }

        val login : TextView = findViewById(R.id.textView2_LoginNow)

        login.setOnClickListener {
            val intent = Intent(this, FormLogin::class.java)
            startActivity(intent)
        }



    }

    private fun performSignUp() {
        val email = findViewById<EditText>(R.id.edit_email)
        val passwd = findViewById<EditText>(R.id.edit_password)
        val username = findViewById<EditText>(R.id.edit_name)

        if (email.text.isEmpty() || passwd.text.isEmpty() || username.text.isEmpty()) {

            val snackbar = Snackbar.make(findViewById(android.R.id.content), "Por favor, preencha todos os campos", Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
            return
        }

        val inputEmail = email.text.toString()
        val inputPasswd = passwd.text.toString()
        val inputUsername = username.text.toString()

        // Verify email existence
        auth.fetchSignInMethodsForEmail(inputEmail)
            .addOnCompleteListener { task ->
                if (!inputUsername.isEmpty()) {
                    if (!inputEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods != null && signInMethods.isNotEmpty()) {
                            // Email already exists, handle accordingly
                            val snackbar = Snackbar.make(findViewById(android.R.id.content),"Este email já existe! Faça já o login.",Snackbar.LENGTH_SHORT)
                            snackbar.setBackgroundTint(Color.RED)
                            snackbar.show()
                        } else {
                            // Email does not exist, continue with sign-up process
                            // Verify password length
                            if (inputPasswd.length >= 6) {
                                auth.createUserWithEmailAndPassword(inputEmail, inputPasswd)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            // Sign up success, proceed to next activity
                                            val intent = Intent(this, FormLogin::class.java)
                                            startActivity(intent)

                                            val snackbar = Snackbar.make(findViewById(android.R.id.content),"Sucesso! Por favor, faça o login.",Snackbar.LENGTH_SHORT)
                                            snackbar.setBackgroundTint(Color.GREEN)
                                            snackbar.show()

                                            auth = Firebase.auth
                                            val user = auth.currentUser

                                            // Update user profile with display name
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName(inputUsername) // Set the name
                                                .build()
                                            user?.updateProfile(profileUpdates)
                                                ?.addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        // Name updated successfully
                                                        val intent = Intent(this, FormLogin::class.java)
                                                        startActivity(intent)

                                                        // Add username to Firestore
                                                        user?.let {addUsernameToFirestore(it.uid,inputUsername,inputEmail,inputPasswd)
                                                        }
                                                    } else {
                                                        // Name update failed
                                                    }
                                                }
                                        } else {
                                            // Sign up failed
                                            val snackbar = Snackbar.make(findViewById(android.R.id.content),"Registo Falhou !",Snackbar.LENGTH_SHORT)
                                            snackbar.setBackgroundTint(Color.RED)
                                            snackbar.show()
                                        }
                                    }
                            } else {
                                // Password length is less than 6
                                val snackbar = Snackbar.make(findViewById(android.R.id.content),"A palavra-passe deve ter no mínimo 6 caracteres!",Snackbar.LENGTH_SHORT)
                                snackbar.setBackgroundTint(Color.RED)
                                snackbar.show()
                            }
                        }
                    } else {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Email inválido!",Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
                } else {
                    val snackbar = Snackbar.make(findViewById(android.R.id.content),"Nome de utilizador inválido!",Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
    }
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun showNoInternetSnackbar() {
        val snackbar =Snackbar.make(findViewById(android.R.id.content), "Sem conexão com a internet", Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.RED)
        snackbar.show()
    }


    private fun addUsernameToFirestore(uid: String, username: String,email: String, passwd: String) {
        val user = hashMapOf("nome" to username,
            "email" to email,
            "password" to passwd)
        // Add a new document with a generated ID
        db.collection("Utilizadores")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}


