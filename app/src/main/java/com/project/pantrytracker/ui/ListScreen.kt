package com.project.pantrytracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.Firebase.ProductsViewModel
import com.project.pantrytracker.Firebase.addProductDb

@Composable
fun ListScreen(
    userData: UserData?,
    paddingValues: PaddingValues
) {
    val viewModel = ProductsViewModel()
    viewModel.getListenerProduct(userData)
    viewModel.getProducts(userData)

    val products = viewModel.products.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Button(onClick = { addProductDb(
            Product("test", "test", "test", emptyList(), "", true),
            userData
        ) }) {
            Text(text = "Sium")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(products.value) { item ->
                Text(
                    text = item.name,
                    modifier = Modifier.padding(10.dp),
                    color = Color.White
                )
            }
        }
    }
}
