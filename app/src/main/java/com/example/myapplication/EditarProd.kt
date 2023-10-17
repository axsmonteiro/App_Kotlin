package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.myapplication.databinding.ActivityEditarProdBinding
import com.example.myapplication.databinding.ActivityTelaInsereProdutoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditarProd : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val db = Firebase.firestore
    private lateinit var binding: ActivityEditarProdBinding
    private lateinit var categoria: Spinner
    private lateinit var idProduto: TextView
    private lateinit var custoText: EditText
    private lateinit var bt_submit: Button
    private lateinit var bt_del: Button

    private lateinit var categoriaSelecionada: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarProdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        categoria = findViewById(R.id.categorias_spinner)
        idProduto = findViewById(R.id.id_produto)
        custoText = findViewById(R.id.custo)
        bt_submit = findViewById(R.id.bt_add)
        bt_del = findViewById(R.id.bt_del)

        val adapter: ArrayAdapter<*> =
            ArrayAdapter.createFromResource(this, R.array.tipo, R.layout.color_spinner_layout)

        adapter.setDropDownViewResource(R.layout.spiner_dropdwon_layout)
        categoria.adapter = adapter
        categoria.onItemSelectedListener = this

        val uid = intent.getStringExtra("uid")
        val tipo = intent.getStringExtra("tipo")
        var custo = intent.getStringExtra("custo")

        if (custo != null) {
            if (!custo.contains("€")) {
                custo += "€"
            }
        }

        // Exibir os valores nos campos de texto
        idProduto.text = uid
        categoria.setSelection(getCategoriaPosition(tipo))
        custoText.setText(custo)

        bt_del.setOnClickListener {
            // Verificar a conexão com a internet
            if (!isConnectedToInternet()) {
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Sem conexão com a internet",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
                return@setOnClickListener
            }

            (if (uid != null) {
                db.collection("Stock").document(uid)
                    .delete()
                    .addOnSuccessListener {

                        val snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            "Apagado com Sucesso!",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()
                        val intent = Intent(this@EditarProd, TelaInicial::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        val snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            "Erro ao apagar o produto!",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                    }
            })
        }

        bt_submit.setOnClickListener {
            // Verificar a conexão com a internet
            if (!isConnectedToInternet()) {
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"Sem conexão com a internet",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
                return@setOnClickListener
            }

            // Recuperar os novos valores do produto
            val novoTipo = categoriaSelecionada
            var novoCusto = custoText.text.toString()

            // Verificar se o custo não contém o símbolo "€" e adicioná-lo
            if (!novoCusto.contains("€")) {
                novoCusto += "€"
            }

            Log.d("adasd","$uid,$novoTipo,$novoCusto")

            // Verificar se os campos foram preenchidos
            if (novoTipo.isNotEmpty() && novoCusto.isNotEmpty()) {
                // Recuperar o UID do produto a ser atualizado
                val uidProduto = idProduto.text.toString()

                // Construir o objeto com os novos valores
                val produtoAtualizado = hashMapOf(
                    "tipo" to novoTipo,
                    "custo" to novoCusto
                    // Adicione aqui outros campos a serem atualizados
                )

                // Atualizar o documento na coleção "Stock"
                db.collection("Stock")
                    .document(uidProduto)
                    .update(produtoAtualizado as Map<String, Any>)
                    .addOnSuccessListener {
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Produto atualizado com sucesso!",Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()

                        val intent = Intent(this@EditarProd, TelaInicial::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { exception ->
                        val snackbar = Snackbar.make(findViewById(android.R.id.content),"Falha ao atualizar o produto!",Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.RED)
                        snackbar.show()
                        Log.e("EditarProd", "Erro ao atualizar o produto", exception)
                    }
            } else {
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"Preencha todos os campos!",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }

        }

    }

    private fun isConnectedToInternet(): Boolean {
        // Verificar a conexão com a internet aqui
        // Retorne true se estiver conectado ou false caso contrário
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun getCategoriaPosition(tipo: String?): Int {
        val tiposArray = resources.getStringArray(R.array.tipo)

        // Encontrar a posição do tipo no array de tipos
        for (i in tiposArray.indices) {
            if (tiposArray[i] == tipo) {
                return i
            }
        }

        return 0 // Valor padrão caso o tipo não seja encontrado
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position)
        // Faça o que for necessário com o item selecionado
        categoriaSelecionada = selectedItem.toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // O código a ser executado quando nenhum item é selecionado
    }
}
