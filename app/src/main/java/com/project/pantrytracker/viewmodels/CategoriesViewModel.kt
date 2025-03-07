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
import com.project.pantrytracker.items.categories.Categories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoriesViewModel: ViewModel() {
    private val _categories = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> = _categories

    var categoriesSelected by mutableStateOf(Categories())
        private set

    var categoryFilterSelected by mutableStateOf(Categories())

    var isLoading by mutableStateOf(true)

    private fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("categories")
    }

    fun createCategoryDb(category: Categories) {
        val categoryRef = getDatabaseReference().push()
        val categoryId = categoryRef.key
        val newCategory = Categories(id = categoryId ?: "", name = category.name)

        categoryRef.setValue(newCategory)
    }

    fun updateCategorySelected(category: Categories) {
        categoriesSelected = category
    }

    fun getCategories() {
        val database = getDatabaseReference()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull {
                    it.getValue(Categories::class.java)
                }
                _categories.value = categories
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Add or update product in the database
    fun addCategoryDb(category: Categories) {
        val database = getDatabaseReference()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var categoryAvailable = false
                var categoryId = ""

                // Check if the product already exists
                for (categoryDb in snapshot.children) {
                    val categoryTemp = categoryDb.getValue(Categories::class.java)
                    if (categoryTemp != null && categoryTemp.id == category.id) {
                        categoryId = categoryDb.key ?: ""
                        categoryAvailable = true
                        break
                    }
                }

                // Add or update product
                addOrUpdateCategory(category, categoryAvailable, categoryId)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Add or update a single product in the database
    private fun addOrUpdateCategory(category: Categories, categoryAvailable: Boolean, categoryId: String) {
        val database = getDatabaseReference()

        if (categoryAvailable) {
            database.child(categoryId).setValue(category)

        } else {
            val newCategoryId = database.push().key.orEmpty()
            database.child(newCategoryId).setValue(Categories(id = newCategoryId, name = category.name))
        }
    }

    // Listener for product changes (added/removed/updated)
    fun getListenerCategories() {
        val database = getDatabaseReference()

        database.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val removedCategories = snapshot.getValue(Categories::class.java)
                removedCategories?.let {
                    _categories.value = _categories.value.filter { it.id != removedCategories.id }
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newCategories = snapshot.getValue(Categories::class.java)
                newCategories?.let {
                    _categories.value += it
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}