package com.project.pantrytracker.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.categories.CategoryProducts
import com.project.pantrytracker.items.products.Product
import com.project.pantrytracker.items.products.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProductsViewModel : ViewModel() {
    private val _products        = MutableStateFlow<List<Product>>(emptyList())
    //private val _productsStorage = MutableStateFlow<List<Product>>(emptyList())
    private val _productsExpired = MutableStateFlow<List<Product>>(emptyList())
    private val _productsUsed    = MutableStateFlow<List<Product>>(emptyList())

    private val _productsStorage = MutableStateFlow<MutableMap<Storage, CategoryProducts>>(
        mutableMapOf()
    )

    val products       : StateFlow<List<Product>> = _products
    //val productsStorage: StateFlow<List<Product>> = _productsStorage
    val productsExpired: StateFlow<List<Product>> = _productsExpired
    val productsUsed   : StateFlow<List<Product>> = _productsUsed

    val productsStorage: StateFlow<MutableMap<Storage, CategoryProducts>> = _productsStorage

    var isLoading by mutableStateOf(true)

    // Firebase database reference getter
    private fun getDatabaseReference(
        userId: String
    ): DatabaseReference {

        return FirebaseDatabase.getInstance().reference.child("users/$userId/storages")
    }

    // Add or update product in the database
    fun addProduct (
        product: Product,
        nameStorage: String,
        user:        UserData?
    ) {

        user?.let { userData ->
            val database = getDatabaseReference(
                userId = userData.userId
            )

            database.child(nameStorage).addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var productAvailable = false
                    var productId = ""

                    // Check if the product already exists
                    for (productDB in snapshot.children) {
                        val productTemp = productDB.getValue(Product::class.java)
                        if (productTemp != null && productTemp.barcode == product.barcode) {
                            //productTemp.numberOfProducts += product.numberOfProducts
                            productId = productDB.key ?: ""
                            productAvailable = true
                            break
                        }
                    }

                    // Add or update product
                    addOrUpdateProduct (
                        userId = userData.userId,
                        product = product,
                        productAvailable = productAvailable,
                        productId = productId,
                        nameStorage = nameStorage
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    // Add or update a single product in the database
    private fun addOrUpdateProduct(
        userId: String,
        product: Product,
        productAvailable: Boolean,
        productId: String,
        nameStorage: String
    ) {
        val database = getDatabaseReference(
            userId = userId
        )

        if (productAvailable) {
            database.child(nameStorage).child(product.category).child(productId).setValue(product)

        } else {
            val newProductId = database.push().key.orEmpty()
            database.child(nameStorage).child(product.category).child(newProductId).setValue(product)

        }
    }

    // Get products from the database
    fun getStorageSingleCategoryProducts(
        user       : UserData?,
        nameStorage: String,
        category   : String
    ) {

        user?.let { userData ->
            val database = getDatabaseReference(
                userId = userData.userId
            )

            database.child(nameStorage).child(category).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val products = snapshot.children.mapNotNull {
                        it.getValue(Product::class.java)
                    }

                    _products.value = products
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    fun getAllStorageProducts(
        user       : UserData?
    ) {
        println("miao0")

        user?.let { userData ->
            val database = getDatabaseReference(userId = userData.userId)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val allProducts        = mutableMapOf<Storage, CategoryProducts>()
                    //val allExpiredProducts = mutableListOf<Product>()
                    //val allUsedProducts    = mutableListOf<Product>()

                    println("miao1")

                    for (storageSnapshot in snapshot.children) {

                        for (categorySnapshot in storageSnapshot.children) {
                            val categoryProducts: CategoryProducts?
                            val storageTemp     : Storage?

                            if (categorySnapshot.key != "info") {

                                categoryProducts = categorySnapshot.getValue(CategoryProducts::class.java)

                                println(categoryProducts)

                                //storageTemp = storageSnapshot.getValue(Storage::class.java)

                                //val products = categorySnapshot.children.mapNotNull {
                                //    it.getValue(Product::class.java)
                                //}

                                //allProducts.addAll(products)

                            }

                            if (categoryProducts != null && (storageTemp != null)) {
                                allProducts[storageTemp] = categoryProducts
                            }
                        }
                    }

                    //allProducts.forEach { item ->

                    //    if (item.isUsed) {
                    //        allUsedProducts.add(item)

                    //    } else if (isExpired(item.expiredDate)){
                    //        allExpiredProducts.add(item)
                    //    }

                    //}


                    _productsStorage.value = allProducts
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    fun getAllProducts(
        user: UserData?
    ) {

        user?.let { userData ->
            val database = getDatabaseReference(userId = userData.userId)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val allProducts        = mutableListOf<Product>()
                    val allExpiredProducts = mutableListOf<Product>()
                    val allUsedProducts    = mutableListOf<Product>()

                    for (storageSnapshot in snapshot.children) {

                        for (categorySnapshot in storageSnapshot.children) {

                            if (categorySnapshot.key != "info") {

                                val products = categorySnapshot.children.mapNotNull {
                                    it.getValue(Product::class.java)
                                }

                                allProducts.addAll(products)
                            }
                        }
                    }

                    allProducts.forEach { item ->

                        if (item.isUsed) {
                            allUsedProducts.add(item)

                        } else if (isExpired(item.expiredDate)){
                            allExpiredProducts.add(item)
                        }

                    }


                    _products.value        = allProducts
                    _productsExpired.value = allExpiredProducts
                    _productsUsed.value    = allUsedProducts
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    // Listener for product changes (added/removed/updated)
    fun getListenerProduct(
        user: UserData?,
        nameStorage: String

    ) {

        user?.let { userData ->
            val database = getDatabaseReference(
                userId = userData.userId
            )

            database.child(nameStorage).addChildEventListener(object : ChildEventListener {

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val removedProduct = snapshot.getValue(Product::class.java)

                    removedProduct?.let {
                        _products.value = _products.value.filter {
                            it.id != removedProduct.id
                        }
                    }
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    // Delete product from the database
    fun deleteProduct(
        user: UserData?,
        product: Product,
        nameStorage: String
    ) {

        user?.let { userData ->
            val database = getDatabaseReference(
                userId = userData.userId
            )

            database.child(nameStorage).child(product.category)
                .orderByChild("barcode")
                .equalTo(product.barcode)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {

                            val productSnapshot = snapshot.children.first()

                            productSnapshot.ref.removeValue().addOnSuccessListener {

                                _products.value = _products.value.filter {
                                    it.id != product.id
                                }

                            }.addOnFailureListener {
                                // Handle error
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    private fun isExpired(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false

        return try {


            val expirationDate = dateFormat.parse(dateString)
            val today = Calendar.getInstance().time
            expirationDate.after(today)

        } catch (e: Exception) {
            false
        }
    }
}