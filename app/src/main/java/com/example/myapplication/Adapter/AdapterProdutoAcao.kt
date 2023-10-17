package com.example.myapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.Produto
import com.example.myapplication.model.ProdutoAcao

class AdapterProdutoAcao (private  val context: Context, private  val produtos: MutableList<ProdutoAcao> ): RecyclerView.Adapter<AdapterProdutoAcao.ProdutoViewHolder>(){

    private var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val intemList = LayoutInflater.from(context).inflate(R.layout.produto_acao,parent,false)
        var holder = ProdutoViewHolder(intemList)
        return holder
    }
    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        holder.img.setImageResource(produtos[position].foto)
        holder.tipo.text= produtos[position].tipo
        holder.estado.text= produtos[position].estado
        holder.custo.text= produtos[position].custo
        holder.bt.setText(produtos[position].bt)
        holder.bt.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }

    }
    override fun getItemCount(): Int = produtos.size


    inner class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img= itemView.findViewById<ImageView>(R.id.img_prod)
        val tipo= itemView.findViewById<TextView>(R.id.tipo_prod)
        val estado= itemView.findViewById<TextView>(R.id.estado)
        val custo= itemView.findViewById<TextView>(R.id.custo)
        val bt = itemView.findViewById<Button>(R.id.bt_acao)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}


