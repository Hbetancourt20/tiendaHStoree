package com.example.tiendaonlinehstore.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.adapters.ProductAdapter
import com.example.tiendaonlinehstore.database.CartDao
import com.example.tiendaonlinehstore.database.ProductDao
import com.example.tiendaonlinehstore.models.Product
import com.example.tiendaonlinehstore.ui.CartActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListFragment : Fragment(), ProductAdapter.OnItemClickListener {

    private lateinit var productDao: ProductDao
    private lateinit var cartDao: CartDao
    private lateinit var productAdapter: ProductAdapter

    private var selectedImageUri: Uri? = null
    private var productImageView: ImageView? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            productImageView?.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productDao = ProductDao(requireContext())
        cartDao = CartDao(requireContext())

        setupRecyclerView(view)

        val fabAddProduct: FloatingActionButton = view.findViewById(R.id.fabAddProduct)
        fabAddProduct.setOnClickListener {
            showAddEditProductDialog(null)
        }

        val fabGoToCart: FloatingActionButton = view.findViewById(R.id.fabGoToCart)
        fabGoToCart.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProductList()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList(), this)
        recyclerView.adapter = productAdapter
    }

    private fun refreshProductList() {
        productAdapter.updateData(productDao.getAllProducts())
    }

    override fun onItemClick(product: Product) {
        product.id?.let {
            cartDao.addItemToCart(it, 1)
            Toast.makeText(requireContext(), getString(R.string.product_added_to_cart, product.name), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemLongClick(product: Product) {
        val options = arrayOf(getString(R.string.edit), getString(R.string.delete))
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_options)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAddEditProductDialog(product)
                    1 -> showDeleteConfirmationDialog(product)
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_delete_confirmation)
            .setMessage(getString(R.string.dialog_message_delete_confirmation, product.name))
            .setPositiveButton(R.string.yes_delete) { _, _ ->
                product.id?.let {
                    productDao.deleteProduct(it)
                    Toast.makeText(requireContext(), R.string.product_deleted, Toast.LENGTH_SHORT).show()
                    refreshProductList()
                }
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showAddEditProductDialog(product: Product?) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_product, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextProductName)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextProductDescription)
        val editTextPrice = dialogView.findViewById<EditText>(R.id.editTextProductPrice)
        val btnSelectImage = dialogView.findViewById<Button>(R.id.btnSelectImage)
        productImageView = dialogView.findViewById(R.id.imageViewProductPreview)

        selectedImageUri = null

        btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        val dialogTitle = if (product == null) getString(R.string.dialog_title_add_product) else getString(R.string.dialog_title_edit_product)

        if (product != null) {
            editTextName.setText(product.name)
            editTextDescription.setText(product.description)
            editTextPrice.setText(product.price.toString())
            product.imageUri?.let {
                selectedImageUri = Uri.parse(it)
                productImageView?.setImageURI(selectedImageUri)
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton(if (product == null) R.string.add else R.string.save) { _, _ ->
                val name = editTextName.text.toString()
                val description = editTextDescription.text.toString()
                val price = editTextPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isNotEmpty() && price > 0) {
                    val imageUriString = selectedImageUri?.toString()

                    val newProduct = if (product == null) {
                        Product(name = name, description = description, price = price, imageUri = imageUriString)
                    } else {
                        product.copy(name = name, description = description, price = price, imageUri = imageUriString)
                    }
                    if (product == null) productDao.addProduct(newProduct) else productDao.updateProduct(newProduct)
                    refreshProductList()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
