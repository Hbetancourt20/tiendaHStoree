package com.example.tiendaonlinehstore.models

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String?,
    val price: Double,
    val imageUri: String? = null
)
