package com.example.tiendaonlinehstore.models


data class CartItem(
    val id: Long? = null,
    val productId: Long,
    var quantity: Int
)

data class CartItemDetails(
    val cartItemId: Long,
    val product: Product,
    var quantity: Int
)
