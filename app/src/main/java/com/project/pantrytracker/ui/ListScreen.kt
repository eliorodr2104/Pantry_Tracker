package com.project.pantrytracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.Firebase.ProductsViewModel

@Preview(device = "id:pixel_8_pro", showSystemUi = true, showBackground = true)
@Composable
fun ListScreen(
    userData: UserData? = UserData("", "", "", null),
    paddingValues: PaddingValues = PaddingValues(16.dp)
) {

    val viewModel = ProductsViewModel()
    viewModel.getListenerProduct(userData)
    viewModel.getProducts(userData)

    val products = viewModel.products.collectAsState(initial = emptyList())


    val productsTest = listOf(
        Product(
            "2222222222222",
            "Testcnsihcihadisocosi1",
            "",
            emptyList(),
            "",
            1
        ),

        Product(
            "33333333333333",
            "Test2",
            "",
            emptyList(),
            "",
            1
        )
    )

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(
                start = 10.dp,
                end = 10.dp
            ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(products.value) { item ->
            ItemProductList(product = item)
        }
    }
}

@Composable
fun ItemProductList(product: Product = Product("222222222", "test", "2l", listOf(), "", 1)) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = CardDefaults.elevatedShape
            )
            .clickable {

            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    modifier = Modifier
                        .widthIn(
                            max = 180.dp
                        ),
                    text = product.name,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )

                Text(
                    text = product.barcode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            NumberPicker(
                changeNumber = { product.numberOfProducts = it },
                number = product.numberOfProducts,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}