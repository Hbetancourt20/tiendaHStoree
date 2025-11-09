package com.example.tiendaonlinehstore.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar // Import Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.adapters.CartAdapter
import com.example.tiendaonlinehstore.database.CartDao
import com.example.tiendaonlinehstore.models.CartItemDetails

class CartActivity : AppCompatActivity(), CartAdapter.OnCartItemInteractionListener {

    private lateinit var cartDao: CartDao
    private lateinit var cartAdapter: CartAdapter
    private lateinit var textViewTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Carrito de Compras"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Add back button

        cartDao = CartDao(this)
        textViewTotal = findViewById(R.id.textViewTotal)

        setupRecyclerView()

        val btnCheckout: Button = findViewById(R.id.btnCheckout)
        btnCheckout.setOnClickListener {
            handleCheckout()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        refreshCart()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(emptyList(), this)
        recyclerView.adapter = cartAdapter
    }

    private fun refreshCart() {
        val cartItems = cartDao.getAllCartItems()
        cartAdapter.updateData(cartItems)
        updateTotal(cartItems)
    }

    private fun updateTotal(cartItems: List<CartItemDetails>) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        textViewTotal.text = String.format("Total: $%.2f", total)
    }

    override fun onIncreaseQuantity(item: CartItemDetails) {
        val newQuantity = item.quantity + 1
        cartDao.updateCartItemQuantity(item.cartItemId, newQuantity)
        refreshCart()
    }

    override fun onDecreaseQuantity(item: CartItemDetails) {
        if (item.quantity > 1) {
            val newQuantity = item.quantity - 1
            cartDao.updateCartItemQuantity(item.cartItemId, newQuantity)
        } else {
            cartDao.removeCartItem(item.cartItemId)
        }
        refreshCart()
    }

    override fun onRemoveItem(item: CartItemDetails) {
        cartDao.removeCartItem(item.cartItemId)
        refreshCart()
    }

    private fun handleCheckout() {
        val cartItems = cartDao.getAllCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmar Compra")
            .setMessage("¿Estás seguro de que quieres finalizar tu compra?")
            .setPositiveButton("Sí, pagar") { _, _ ->
                cartDao.clearCart()
                Toast.makeText(this, "¡Gracias por tu compra!", Toast.LENGTH_LONG).show()
                refreshCart()
                finish()
            }
            .setNegativeButton("No, seguir comprando", null)
            .show()
    }
}
