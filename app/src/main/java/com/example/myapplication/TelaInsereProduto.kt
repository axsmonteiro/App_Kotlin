package com.example.myapplication


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityTelaInsereProdutoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class TelaInsereProduto : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val db = Firebase.firestore
    private lateinit var binding: ActivityTelaInsereProdutoBinding
    private lateinit var categoria: Spinner
    private lateinit var idProduto : TextView
    private lateinit var custo : EditText
    private lateinit var bt_submit : Button

    private lateinit var categoriaSelecionada: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaInsereProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        receberId()

        categoria = findViewById(R.id.categorias_spinner)
        idProduto = findViewById(R.id.id_produto)
        custo = findViewById(R.id.custo)

        bt_submit = findViewById(R.id.bt_add)

        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(this,R.array.tipo,R.layout.color_spinner_layout)

        adapter.setDropDownViewResource(R.layout.spiner_dropdwon_layout) // Use o novo layout personalizado para o dropdown do Spinner
        categoria.adapter = adapter
        categoria.onItemSelectedListener = this







        bt_submit.setOnClickListener {
            val inputIdProduto = idProduto.text.toString()
            var inputCusto = custo.text.toString()

            if(inputCusto!="" && inputIdProduto!="Nenhum código de barras detetado" && categoriaSelecionada!="") {
                if (!inputCusto.contains("€")) {
                    inputCusto += "€"
                    addStock(categoriaSelecionada, inputIdProduto, inputCusto)
                }



                val intent = Intent(this, TelaInicial::class.java)
                startActivity(intent)
            }
        }



    }

    private fun receberId(){
        val id_produto = intent.getStringExtra("id_produto")
        binding.idProduto.text = id_produto

    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position)

        // Faça o que for necessário com o item selecionado
        categoriaSelecionada = selectedItem.toString()

    }





    override fun onNothingSelected(parent: AdapterView<*>?) {
        // O código a ser executado quando nenhum item é selecionado
    }





    private fun addStock(categoriaSelecionada: String,idProduto: String,custo: String){

        val stock = hashMapOf(
            "custo" to custo,
            "id" to idProduto,
            "tipo" to categoriaSelecionada,
            "estado" to "0"

        )

        // Add a new document with a generated ID
        db.collection("Stock")
            .add(stock)
            .addOnSuccessListener { documentReference ->
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"Produto inserido!",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.GREEN)
                snackbar.show()
            }
            .addOnFailureListener { e ->
                val snackbar = Snackbar.make(findViewById(android.R.id.content),"Erro ao inserir produto!",Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
    }

}