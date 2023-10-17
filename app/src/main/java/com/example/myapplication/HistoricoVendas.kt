package com.example.myapplication

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AdapterProduto
import com.example.myapplication.model.Produto
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoricoVendas : AppCompatActivity() {

    private var totalCusto: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_vendas)

        supportActionBar?.hide()

        val recycler_prod = findViewById<RecyclerView>(R.id.recyvlerView_prod)
        recycler_prod.layoutManager= LinearLayoutManager(this)
        recycler_prod.setHasFixedSize(true)

        //config adapter
        val listaProdutos : MutableList<Produto> = mutableListOf()
        val adapterProd= AdapterProduto(this,listaProdutos)
        recycler_prod.adapter= adapterProd

        val db = Firebase.firestore
        var custoTotal = findViewById<TextView>(R.id.custo)


        db.collection("Stock")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val uid = document.id
                    val estado = document.getString("estado")
                    val tipo = document.getString("tipo")
                    val custo = document.getString("custo")

                    var estadoProduto = ""
                    if (tipo != null && estado != null && custo != null){
                        if (estado =="1"){
                            estadoProduto = "Fora de Stock"
                            val produto = when (tipo) {
                                "Motores" -> Produto(R.drawable.ic_motor, tipo, estadoProduto, custo )
                                "Tubulação Hidráulica" -> Produto(R.drawable.ic_tubos, tipo, estadoProduto, custo)
                                "Bombas Elétricas" -> Produto(R.drawable.ic_bomba, tipo, estadoProduto, custo)
                                "Leds" -> Produto(R.drawable.ic_led, tipo, estadoProduto, custo)
                                "Cabos" -> Produto(R.drawable.ic_cabos, tipo, estadoProduto, custo)
                                else -> null
                            }
                            if (produto != null) {
                                listaProdutos.add(produto)
                                numTotalVendido(custo)
                                custoTotal.setText(totalCusto.toString() + "€" )
                            }
                        }
                    }
                }
                adapterProd.notifyDataSetChanged() // atualiza o adaptador com os novos produtos
            }
            .addOnFailureListener { exception ->
                // handle the failure
            }
    }

    private fun numTotalVendido(custo: String){
        val numericString = custo.replace("€", "").trim()
        val valor = numericString.toDouble()
        totalCusto += valor
    }
}
