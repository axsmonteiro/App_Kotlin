package com.example.myapplication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AdapterProduto
import com.example.myapplication.Adapter.AdapterProdutoAcao
import com.example.myapplication.databinding.ActivityListaProdEditarBinding
import com.example.myapplication.model.Produto
import com.example.myapplication.model.ProdutoAcao
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.checkerframework.common.returnsreceiver.qual.This

class ListaProdEditar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_prod_editar)
        supportActionBar?.hide()

        val recycler_prod = findViewById<RecyclerView>(R.id.recyvlerView_prod)
        recycler_prod.layoutManager = LinearLayoutManager(this)
        recycler_prod.setHasFixedSize(true)

        // Configurar o adapter
        val listaProdutos: MutableList<ProdutoAcao> = mutableListOf()
        val adapterProd = AdapterProdutoAcao(this, listaProdutos)
        recycler_prod.adapter = adapterProd

        val db = Firebase.firestore

        db.collection("Stock")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val uid = document.id
                    val estado = document.getString("estado")
                    val tipo = document.getString("tipo")
                    val custo = document.getString("custo")

                    var estadoProduto = ""
                    if (tipo != null && estado != null && custo != null) {
                        if (estado == "0") {
                            estadoProduto = "Em Stock"
                        } else if (estado == "1") {
                            estadoProduto = "Fora de Stock"
                        }
                        val produto = when (tipo) {
                            "Motores" -> ProdutoAcao(uid,R.drawable.ic_motor, tipo, estadoProduto, custo, "Editar")
                            "Tubulação Hidráulica" -> ProdutoAcao(uid,R.drawable.ic_tubos, tipo, estadoProduto, custo, "Editar")
                            "Bombas Elétricas" -> ProdutoAcao(uid,R.drawable.ic_bomba, tipo, estadoProduto, custo, "Editar")
                            "Leds" -> ProdutoAcao(uid,R.drawable.ic_led, tipo, estadoProduto, custo, "Editar")
                            "Cabos" -> ProdutoAcao(uid,R.drawable.ic_cabos, tipo, estadoProduto, custo, "Editar")
                            else -> null
                        }
                        if (produto != null)
                            listaProdutos.add(produto)
                    }
                }
                adapterProd.notifyDataSetChanged() // Atualiza o adaptador com os novos produtos
            }
            .addOnFailureListener { exception ->
                // Lida com a falha

            }

        adapterProd.setOnItemClickListener(object : AdapterProdutoAcao.OnItemClickListener {
            override fun onItemClick(position: Int) {

                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Editado com sucesso!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.GREEN)
                snackbar.show()

                // Obter o produto selecionado
                val produto = listaProdutos[position]

                // Criar uma Intent para iniciar a atividade EditarProd
                val intent = Intent(this@ListaProdEditar, EditarProd::class.java)

                // Passar os valores do produto como parâmetros para a atividade EditarProd
                intent.putExtra("uid", produto.uid)
                intent.putExtra("tipo", produto.tipo)
                intent.putExtra("custo", produto.custo)

                startActivity(intent)
            }
        })

    }
}
