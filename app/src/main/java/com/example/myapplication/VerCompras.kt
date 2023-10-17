package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapter.AdapterProduto
import com.example.myapplication.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VerCompras : AppCompatActivity() {
    private val db = Firebase.firestore // Instância do Firestore
    private lateinit var adapterProd: AdapterProduto // Propriedade do AdapterProduto
    private lateinit var listaProdutos: MutableList<Produto> // Propriedade da lista de produtos
    private var totalCusto: Double = 0.0 // Variável para rastrear o custo total

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_compras)
        supportActionBar?.hide()

        val recycler_prod = findViewById<RecyclerView>(R.id.recyvlerView_prod)
        recycler_prod.layoutManager = LinearLayoutManager(this)
        recycler_prod.setHasFixedSize(true)

        // Inicializar a lista de produtos
        listaProdutos = mutableListOf()

        // Configurar o adapter
        adapterProd = AdapterProduto(this, listaProdutos)
        recycler_prod.adapter = adapterProd

        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid // Obter o UID do usuário atual



        db.collection("UserStock")
            .whereEqualTo("idUser", uidUsuario)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val idStock = document.getString("idStock")

                    if (idStock != null)
                        mostrarCompras(idStock)
                }

                // Exibir o custo total

            }
            .addOnFailureListener { exception ->
                // Tratar a falha na busca
            }
    }

    private fun mostrarCompras(idStock: String) {

        var custoTotal = findViewById<TextView>(R.id.custo)


        db.collection("Stock")
            .whereIn(FieldPath.documentId(), listOf(idStock)) // Consulta usando o ID do estoque
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val estado = document.getString("estado")
                    val tipo = document.getString("tipo")
                    val custo = document.getString("custo")

                    var estadoProduto = ""
                    if (tipo != null && estado != null && custo != null) {
                        if (estado == "1") {
                            estadoProduto = ""

                            val produto = when (tipo) {
                                "Motores" -> Produto(R.drawable.ic_motor, tipo, estadoProduto, custo)
                                "Tubulação Hidráulica" -> Produto(R.drawable.ic_tubos, tipo, estadoProduto, custo)
                                "Bombas Elétricas" -> Produto(R.drawable.ic_bomba, tipo, estadoProduto, custo)
                                "Leds" -> Produto(R.drawable.ic_led, tipo, estadoProduto, custo)
                                "Cabos" -> Produto(R.drawable.ic_cabos, tipo, estadoProduto, custo)
                                else -> null
                            }
                            if (produto != null) {
                                listaProdutos.add(produto)
                                calcularCustoTotal(custo)
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

    private fun calcularCustoTotal(custo: String) {
        val custoSemSimbolo = custo.replace("€", "")
        val custoNumerico = custoSemSimbolo.toDouble()
        totalCusto += custoNumerico
    }

}
