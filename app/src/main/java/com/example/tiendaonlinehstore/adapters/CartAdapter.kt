package com.example.tiendaonlinehstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.models.CartItemDetails

class CartAdapter(
    private val listener: OnCartItemInteractionListener
) : ListAdapter<CartItemDetails, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    interface OnCartItemInteractionListener {
        fun onIncreaseQuantity(item: CartItemDetails)
        fun onDecreaseQuantity(item: CartItemDetails)
        fun onRemoveItem(item: CartItemDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewCartItemName)
        private val priceTextView: TextView = itemView.findViewById(R.id.textViewCartItemPrice)
        private val quantityTextView: TextView = itemView.findViewById(R.id.textViewCartItemQuantity)
        private val increaseButton: Button = itemView.findViewById(R.id.btnIncreaseQuantity)
        private val decreaseButton: Button = itemView.findViewById(R.id.btnDecreaseQuantity)
        private val removeButton: ImageButton = itemView.findViewById(R.id.btnRemoveFromCart)

        init {
            increaseButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onIncreaseQuantity(getItem(adapterPosition))
                }
            }
            decreaseButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onDecreaseQuantity(getItem(adapterPosition))
                }
            }
            removeButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onRemoveItem(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: CartItemDetails) {
            nameTextView.text = item.product.name
            priceTextView.text = String.format("$%.2f", item.product.price)
            quantityTextView.text = item.quantity.toString()
        }
    }
}

class CartItemDiffCallback : DiffUtil.ItemCallback<CartItemDetails>() {
    override fun areItemsTheSame(oldItem: CartItemDetails, newItem: CartItemDetails): Boolean {
        return oldItem.cartItemId == newItem.cartItemId
    }

    override fun areContentsTheSame(oldItem: CartItemDetails, newItem: CartItemDetails): Boolean {
        return oldItem == newItem
    }
}
