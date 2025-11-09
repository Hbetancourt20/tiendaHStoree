package com.example.tiendaonlinehstore.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.models.Product

class ProductAdapter(private var productList: List<Product>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
        fun onItemLongClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = productList.size

    fun updateData(newProductList: List<Product>){
        productList = newProductList
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewProductName)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewProductDescription)
        private val priceTextView: TextView = itemView.findViewById(R.id.textViewProductPrice)
        private val productImageView: ImageView = itemView.findViewById(R.id.imageViewProduct)

        fun bind(product: Product) {
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            priceTextView.text = String.format("$%.2f", product.price)

            product.imageUri?.let {
                try {
                    val imageUri = Uri.parse(it)
                    itemView.context.contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    productImageView.setImageURI(imageUri)
                } catch (e: Exception) {
                    productImageView.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } ?: run {
                productImageView.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            itemView.setOnClickListener {
                listener.onItemClick(product)
            }

            itemView.setOnLongClickListener {
                listener.onItemLongClick(product)
                true
            }
        }
    }
}
