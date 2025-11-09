package com.example.tiendaonlinehstore.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.tiendaonlinehstore.models.User

class UserDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addUser(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_EMAIL, user.email)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, user.password)
        }
        val id = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getUser(email: String, pass: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            null, // All columns
            "${DatabaseHelper.COLUMN_USER_EMAIL} = ? AND ${DatabaseHelper.COLUMN_USER_PASSWORD} = ?",
            arrayOf(email, pass),
            null, null, null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID))
            val userEmail = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMAIL))
            val userPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PASSWORD))
            user = User(id = userId, email = userEmail, password = userPassword)
        }
        cursor.close()
        db.close()
        return user
    }
}
