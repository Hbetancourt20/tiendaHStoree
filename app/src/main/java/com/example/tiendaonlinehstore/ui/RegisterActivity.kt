package com.example.tiendaonlinehstore.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.database.UserDao
import com.example.tiendaonlinehstore.models.User

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtEmailRegister = findViewById<EditText>(R.id.edtEmailRegister)
        val edtPasswordRegister = findViewById<EditText>(R.id.edtPasswordRegister)
        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val userDao = UserDao(this)

        btnRegister.setOnClickListener {
            val email = edtEmailRegister.text.toString()
            val password = edtPasswordRegister.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    val newUser = User(email = email, password = password)
                    val result = userDao.addUser(newUser)
                    if (result > -1) {
                        Toast.makeText(this, R.string.registration_successful, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, R.string.registration_error_email_exists, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, R.string.registration_error_password_mismatch, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
