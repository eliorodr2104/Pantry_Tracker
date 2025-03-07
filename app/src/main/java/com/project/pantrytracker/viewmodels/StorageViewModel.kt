package com.project.pantrytracker.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.products.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StorageViewModel : ViewModel() {

    private val _storage = MutableStateFlow<List<Storage>>(emptyList())

    val storage: StateFlow<List<Storage>> = _storage

    var isLoading by mutableStateOf(true)

    // Firebase database reference getter
    private fun getDatabaseReference(
        userId: String
    ): DatabaseReference {

        return FirebaseDatabase.getInstance().reference.child("users/$userId/storages")
    }

    // Add or update product in the database
    fun addStorage (
        storage: Storage,
        user:        UserData?
    ) {

        user?.let { userData ->
            val database = getDatabaseReference(
                userId = userData.userId
            )

            database.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    var storageAvailable = false

                    // Check if the product already exists
                    for (productDB in snapshot.children) {
                        val storageTemp = productDB.getValue(Storage::class.java)

                        storageTemp?.let { storageDatabase ->

                            if (storageDatabase.name == storage.name) {
                                storageAvailable = true;
                            }

                        }

                        if (storageAvailable) break
                    }

                    // Add or update product
                    addOrUpdateProduct (
                        userId       = userData.userId,
                        storage      = storage
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
        storage: Storage,
    ) {
        val database = getDatabaseReference(userId = userId)

        database.child(storage.name).child("info").setValue(storage)
    }

    fun getAllStorage(
        user: UserData?

    ) {

        user?.let { userData ->
            val database = getDatabaseReference(userId = userData.userId)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val allStorage = mutableListOf<Storage>()

                    for (storageSnapshot in snapshot.children) {
                        val infoSnapshot = storageSnapshot.child("info")

                        infoSnapshot.getValue(Storage::class.java)?.let {
                            allStorage.add(it)
                        }
                    }

                    _storage.value = allStorage
                    println("Storage update vm: ${_storage.value}")
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}
