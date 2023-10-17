package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class ToolBar : AppCompatActivity() {
    private lateinit var binding: ToolBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool_bar)

        val bt_conta : ImageButton = findViewById(R.id.info_conta)

        bt_conta.setOnClickListener{
            val intent = Intent(this, TelaDadosPessoais::class.java)
            startActivity(intent)
        }
    }
}