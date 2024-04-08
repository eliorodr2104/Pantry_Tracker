package com.project.pantrytracker.Firebase

import com.google.firebase.database.FirebaseDatabase
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData

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