package com.project.pantrytracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.project.pantrytracker.Firebase.LoginGoogle.UserData

@Composable
fun ProfileScreen(
    userData: UserData?,
    logout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            if (userData?.profilePictureUrl != null) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            if (userData?.username != null) {
                Text(
                    text = userData.username,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onLoggedIn(userData) }) {
                Text(text = "Add node DB")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = logout) {
                Text(text = "logout")
            }
        }
    }
}

private fun onLoggedIn(user: UserData?) {
    val database = FirebaseDatabase.getInstance()

    if (user?.userId != null) {
        val uid = user.userId
        val userRef = database.getReference("users").child(uid)

        // Crea una lista di prodotti
        val products = listOf(
            mapOf("nome" to "latte"),
            mapOf("nome" to "pane"),
            mapOf("nome" to "frutta")
        )

        // Crea un nuovo nodo "prodotti" sotto il nodo con UID
        val productsRef = userRef.child("prodotti")

        // Imposta il valore del nodo "prodotti" sulla lista di prodotti
        productsRef.setValue(products)
    }
}
