package com.example.tiendaonlinehstore.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaonlinehstore.MainActivity
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.database.UserDao
import com.example.tiendaonlinehstore.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmailLogin = findViewById<EditText>(R.id.edtEmailLogin)
        val edtPasswordLogin = findViewById<EditText>(R.id.edtPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegistro = findViewById<TextView>(R.id.txtRegistro)

        val userDao = UserDao(this)
        val sessionManager = SessionManager(this)

        btnLogin.setOnClickListener {
            val email = edtEmailLogin.text.toString().trim()
            val password = edtPasswordLogin.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val user = userDao.getUser(email, password)
                if (user != null) {
                    sessionManager.saveUserEmail(email)
                    Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, R.string.login_error_credentials, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            }
        }

        txtRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
