package com.example.tiendaonlinehstore.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.example.tiendaonlinehstore.models.Product

class ProductDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addProduct(product: Product): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.description)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, product.imageUri)
        }
        val id = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_PRODUCTS}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID))
                val name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME))
                val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
                val imageUri = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI))
                productList.add(Product(id, name, description, price, imageUri))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return productList
    }

    fun updateProduct(product: Product): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.name)
            put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.description)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.price)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI, product.imageUri)
        }
        val rowsAffected = db.update(DatabaseHelper.TABLE_PRODUCTS, values, "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?", arrayOf(product.id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteProduct(productId: Long): Int {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(DatabaseHelper.TABLE_PRODUCTS, "${DatabaseHelper.COLUMN_PRODUCT_ID} = ?", arrayOf(productId.toString()))
        db.close()
        return rowsAffected
    }
}
