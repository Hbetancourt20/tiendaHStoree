package com.example.tiendaonlinehstore.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import com.example.tiendaonlinehstore.models.CartItemDetails
import com.example.tiendaonlinehstore.models.Product

class CartDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addItemToCart(productId: Long, quantity: Int) {
        val db = dbHelper.writableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_CART_ITEMS,
            arrayOf(DatabaseHelper.COLUMN_CART_QUANTITY),
            "${DatabaseHelper.COLUMN_CART_PRODUCT_ID} = ?",
            arrayOf(productId.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            val currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_QUANTITY))
            val newQuantity = currentQuantity + quantity
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CART_QUANTITY, newQuantity)
            }
            db.update(DatabaseHelper.TABLE_CART_ITEMS, values, "${DatabaseHelper.COLUMN_CART_PRODUCT_ID} = ?", arrayOf(productId.toString()))
        } else {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CART_PRODUCT_ID, productId)
                put(DatabaseHelper.COLUMN_CART_QUANTITY, quantity)
            }
            db.insert(DatabaseHelper.TABLE_CART_ITEMS, null, values)
        }
        cursor.close()
        db.close()
    }

    @SuppressLint("Range")
    fun getAllCartItems(): List<CartItemDetails> {
        val cartItems = mutableListOf<CartItemDetails>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT
                c.${DatabaseHelper.COLUMN_CART_ID},
                c.${DatabaseHelper.COLUMN_CART_QUANTITY},
                p.${DatabaseHelper.COLUMN_PRODUCT_ID},
                p.${DatabaseHelper.COLUMN_PRODUCT_NAME},
                p.${DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION},
                p.${DatabaseHelper.COLUMN_PRODUCT_PRICE},
                p.${DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI} 
            FROM
                ${DatabaseHelper.TABLE_CART_ITEMS} c
            INNER JOIN
                ${DatabaseHelper.TABLE_PRODUCTS} p ON c.${DatabaseHelper.COLUMN_CART_PRODUCT_ID} = p.${DatabaseHelper.COLUMN_PRODUCT_ID}
        """

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val cartItemId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CART_ID))
                val quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CART_QUANTITY))

                val productId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID))
                val name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME))
                val description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION))
                val price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
                val imageUri = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URI))

                val product = Product(productId, name, description, price, imageUri)
                val cartItemDetails = CartItemDetails(cartItemId, product, quantity)

                cartItems.add(cartItemDetails)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cartItems
    }

    fun updateCartItemQuantity(cartItemId: Long, newQuantity: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_QUANTITY, newQuantity)
        }
        val rows = db.update(DatabaseHelper.TABLE_CART_ITEMS, values, "${DatabaseHelper.COLUMN_CART_ID} = ?", arrayOf(cartItemId.toString()))
        db.close()
        return rows
    }

    fun removeCartItem(cartItemId: Long): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete(DatabaseHelper.TABLE_CART_ITEMS, "${DatabaseHelper.COLUMN_CART_ID} = ?", arrayOf(cartItemId.toString()))
        db.close()
        return rows
    }
    
    fun clearCart(): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete(DatabaseHelper.TABLE_CART_ITEMS, null, null)
        db.close()
        return rows
    }
}
