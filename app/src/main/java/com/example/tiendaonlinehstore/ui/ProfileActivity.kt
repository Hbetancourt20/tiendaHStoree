package com.example.tiendaonlinehstore.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaonlinehstore.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnGoToProducts = findViewById<Button>(R.id.btnGoToProducts)
        val btnGoToMap = findViewById<Button>(R.id.btnGoToMap)

        btnGoToProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        btnGoToMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}
