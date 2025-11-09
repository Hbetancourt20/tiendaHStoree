package com.example.tiendaonlinehstore.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar // Import Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendaonlinehstore.R
import com.example.tiendaonlinehstore.adapters.ProductAdapter
import com.example.tiendaonlinehstore.database.CartDao
import com.example.tiendaonlinehstore.database.ProductDao
import com.example.tiendaonlinehstore.models.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductListActivity : AppCompatActivity(), ProductAdapter.OnItemClickListener {

    private lateinit var productDao: ProductDao
    private lateinit var cartDao: CartDao
    private lateinit var productAdapter: ProductAdapter

    private var selectedImageUri: Uri? = null
    private var productImageView: ImageView? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            productImageView?.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Productos"

        productDao = ProductDao(this)
        cartDao = CartDao(this)

        setupRecyclerView()

        val fabAddProduct: FloatingActionButton = findViewById(R.id.fabAddProduct)
        fabAddProduct.setOnClickListener {
            showAddEditProductDialog(null)
        }

        val fabGoToCart: FloatingActionButton = findViewById(R.id.fabGoToCart)
        fabGoToCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProductList()
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList(), this)
        recyclerView.adapter = productAdapter
    }

    private fun refreshProductList() {
        productAdapter.updateData(productDao.getAllProducts())
    }

    override fun onItemClick(product: Product) {
        product.id?.let {
            cartDao.addItemToCart(it, 1)
            Toast.makeText(this, "${product.name} añadido al carrito", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemLongClick(product: Product) {
        val options = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Elige una opción")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAddEditProductDialog(product)
                    1 -> showDeleteConfirmationDialog(product)
                }
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar '${product.name}'?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                product.id?.let {
                    productDao.deleteProduct(it)
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
                    refreshProductList()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showAddEditProductDialog(product: Product?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextProductName)
        val editTextDescription = dialogView.findViewById<EditText>(R.id.editTextProductDescription)
        val editTextPrice = dialogView.findViewById<EditText>(R.id.editTextProductPrice)
        val btnSelectImage = dialogView.findViewById<Button>(R.id.btnSelectImage)
        productImageView = dialogView.findViewById(R.id.imageViewProductPreview)
        
        selectedImageUri = null

        btnSelectImage.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        val dialogTitle = if (product == null) "Agregar Producto" else "Editar Producto"

        if (product != null) {
            editTextName.setText(product.name)
            editTextDescription.setText(product.description)
            editTextPrice.setText(product.price.toString())
            product.imageUri?.let {
                selectedImageUri = Uri.parse(it)
                productImageView?.setImageURI(selectedImageUri)
            }
        }

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(dialogView)
            .setPositiveButton(if (product == null) "Agregar" else "Guardar") { _, _ ->
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
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
