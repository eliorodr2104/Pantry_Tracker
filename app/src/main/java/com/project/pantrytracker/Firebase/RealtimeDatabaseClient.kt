package com.project.pantrytracker.Firebase

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun createUserDb(user: UserData?) {
    val database = FirebaseDatabase.getInstance()
    val usersNodeReference: DatabaseReference
    val userDetails: DatabaseReference
    val detailsUser: HashMap<String, String>

    if (user != null) {
        usersNodeReference = database.getReference("users").child(user.userId)

        userDetails = usersNodeReference.child("userDetails")

        detailsUser = hashMapOf(
            "name" to user.username.toString(),
            "email" to user.email.toString()
        )

        userDetails.setValue(detailsUser)
    }
}

fun addProductDb(
    product: Product,
    user: UserData?
) {
    var productAvailable = false
    val database: DatabaseReference
    var productId: String

    if (user != null) {
        database = FirebaseDatabase.getInstance().reference.child("users/${user.userId}/products")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { productDB ->
                    val productTemp = productDB.getValue(Product::class.java)

                    if (productTemp != null) {
                        if (product.barcode == productTemp.barcode) {
                            productTemp.numberOfProducts += product.numberOfProducts

                            productId = productDB.key.toString()
                            productAvailable = true

                            addSingleProduct(
                                user = user,
                                productAvailable = true,
                                productId = productId,
                                product = productTemp
                            )
                        }
                    }
                }

                if (!productAvailable) {
                    addSingleProduct(
                        user = user,
                        productAvailable = false,
                        productId = "",
                        product = product
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}


private fun addSingleProduct(
    user: UserData?,
    productAvailable: Boolean,
    productId: String,
    product: Product
) {
    if (user != null) {
        val uid = user.userId
        val newProductId: String
        val database = FirebaseDatabase.getInstance().getReference("users/$uid")

        if (productAvailable) {
            database.child("products/$productId").setValue(product)

        } else {
            newProductId = database.push().key.toString()

            database.child("products/$newProductId").setValue(product)
        }
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
