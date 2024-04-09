package com.project.pantrytracker.Firebase

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.ui.ListScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun createUserDb(user: UserData?) {
    val database = FirebaseDatabase.getInstance()

    if (user?.userId != null) {
        val uid = user.userId
        val userRef = database.getReference("users").child(uid)

        val productsRef = userRef.child("products")

        val defaultProduct = listOf("default")

        productsRef.setValue(defaultProduct)
    }
}

fun addProductDb(
    product: Product,
    user: UserData?
) {
    val database = FirebaseDatabase.getInstance()

    if (user?.userId != null) {
        val uid = user.userId

        val userRef = database.getReference("users/$uid")
        val productId = userRef.push().key

        userRef.child("products/$productId").setValue(product)
    }
}

class ProductsViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    fun getProducts(user: UserData?) {
        if (user != null) {
            // Crea un riferimento al database
            val database = FirebaseDatabase.getInstance().reference.child("users/${user.userId}/products")

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                        .filter { product -> product.availability }

                    _products.value = products
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun getListenerProduct(user: UserData?) {
        if (user != null) {
            val database = FirebaseDatabase.getInstance().reference.child("users/${user.userId}/products")

            database.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    getProducts(user)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    getProducts(user)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    getProducts(user)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    getProducts(user)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}
