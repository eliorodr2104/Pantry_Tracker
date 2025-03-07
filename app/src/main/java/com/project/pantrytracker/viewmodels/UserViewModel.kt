package com.project.pantrytracker.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.project.pantrytracker.Firebase.LoginGoogle.UserData

class UserViewModel : ViewModel(){

    // Firebase database reference getter
    private fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("users")
    }

    // Create user in database
    fun createUser(user: UserData?) {
        user?.let {
            val userDetails = getDatabaseReference().child(it.userId).child("userDetails")

            val detailsUser = hashMapOf(
                "name" to it.username.orEmpty(),
                "email" to it.email.orEmpty()
            )

            userDetails.setValue(detailsUser)
        }
    }

}