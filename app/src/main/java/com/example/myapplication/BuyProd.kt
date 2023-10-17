package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AdapterProduto
import com.example.myapplication.Adapter.AdapterProdutoAcao
import com.example.myapplication.databinding.ActivityBarCodeScanBinding
import com.example.myapplication.model.Produto
import com.example.myapplication.model.ProdutoAcao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BuyProd : AppCompatActivity() {
    private lateinit var bt_comprar: Button
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_prod)

        supportActionBar?.hide()

        val recycler_prod = findViewById<RecyclerView>(R.id.recyvlerView_prod)
        recycler_prod.layoutManager = LinearLayoutManager(this)
        recycler_prod.setHasFixedSize(true)

        val listaProdutos: MutableList<ProdutoAcao> = mutableListOf()
        val adapterProd = AdapterProdutoAcao(this, listaProdutos)
        recycler_prod.adapter = adapterProd

        val db = Firebase.firestore

        db.collection("Stock")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    uid = document.id
                    val estado = document.getString("estado")
                    val tipo = document.getString("tipo")
                    val custo = document.getString("custo")

                    var estadoProduto = ""
                    if (tipo != null && estado != null && custo != null) {
                        if (estado == "0") {
                            estadoProduto = "Em Stock"

                            val produto = when (tipo) {
                                "Motores" -> ProdutoAcao(uid, R.drawable.ic_motor, tipo, estadoProduto, custo, "Comprar")
                                "Tubulação Hidráulica" -> ProdutoAcao(uid, R.drawable.ic_tubos, tipo, estadoProduto, custo , "Comprar")
                                "Bombas Elétricas" -> ProdutoAcao(uid, R.drawable.ic_bomba, tipo, estadoProduto, custo, "Comprar")
                                "Leds" -> ProdutoAcao(uid, R.drawable.ic_led, tipo, estadoProduto, custo, "Comprar")
                                "Cabos" -> ProdutoAcao(uid, R.drawable.ic_cabos, tipo, estadoProduto, custo, "Comprar")
                                else -> null
                            }

                            if (produto != null) {
                                listaProdutos.add(produto)
                            }
                        }
                    }
                }
                adapterProd.notifyDataSetChanged() // atualiza o adaptador com os novos produtos
            }
            .addOnFailureListener { exception ->
                // handle the failure
            }

        adapterProd.setOnItemClickListener(object : AdapterProdutoAcao.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val produto = listaProdutos[position]

                // Verificar o produto na base de dados com base no UID
                val db = Firebase.firestore
                val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid // Obter o UID do usuário atual

                db.collection("Stock").document(produto.uid)
                    .update("estado", "1")
                    .addOnSuccessListener {
                        // Atualização bem-sucedida
                        produto.estado = "1" // Atualizar o estado localmente
                        adapterProd.notifyItemChanged(position) // Atualizar a exibição do item na lista
                    }
                    .addOnFailureListener { exception ->
                        // Tratar a falha na atualização
                    }

                // Criar um novo documento na coleção "UserStock" com o UID do usuário e o UID do produto
                val userStock = hashMapOf(
                    "idStock" to produto.uid,
                    "idUser" to uidUsuario
                )

                db.collection("UserStock")
                    .add(userStock)
                    .addOnSuccessListener { documentReference ->
                        Log.d("this", "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w("this", "Error adding document", e)
                    }

                // Acessar outra página/package com.example.myapplication
                val intent = Intent(this@BuyProd, MainActivity::class.java)
                startActivity(intent)
            }
        })

    }
}
