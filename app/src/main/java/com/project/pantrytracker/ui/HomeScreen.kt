package com.project.pantrytracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.products.Storage
import com.project.pantrytracker.ui.theme.ProductsScreen
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductEditViewModel
import com.project.pantrytracker.viewmodels.ProductsViewModel
import com.project.pantrytracker.viewmodels.StorageViewModel

@Composable
fun HomeScreen(
    paddingValues        : PaddingValues,
    userData             : UserData?,
    viewModelStorage     : StorageViewModel,
    viewModelProducts    : ProductsViewModel,
    viewModelCategories  : CategoriesViewModel,
    editProductsViewModel: ProductEditViewModel
) {
    var storageSelected by remember { mutableStateOf(Storage()) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    //LaunchedEffect(navBackStackEntry?.destination?.route) {
    //    when (navBackStackEntry?.destination?.route) {
    //        "list_products", "filter_products" -> viewModelCategories.updateCategorySelected(Categories())
    //    }
    //}

    Surface (
        color = MaterialTheme.colorScheme.surface
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            /*

            TopAppBar(
                titleText = when(navBackStackEntry?.destination?.route) {
                    "list_products" -> "Products"
                    "filter_products", "select_categories" -> "Categories"
                    "show_info_item" -> "Edit"
                    else -> ""
                },
                subTitleText = when(navBackStackEntry?.destination?.route) {
                    "list_products" -> "List of your products"
                    "filter_products", "select_categories" -> "List of your categories"
                    "show_info_item" -> "Form to edit the product"
                    else -> ""
                }
            ) {
                IconButton(
                    onClick = {
                        when (navBackStackEntry?.destination?.route) {
                            "filter_products" -> {
                                navController.navigate("list_products") {
                                    popUpTo("filter_products") { inclusive = true }
                                }
                            }

                            "list_products" -> {
                                navController.navigate("filter_products") {
                                    popUpTo("list_products") { inclusive = true }
                                }
                            }

                            "show_info_item" -> {
                                viewModelProducts.addProduct(
                                    product = Product(
                                        barcode = editProductsViewModel.barcodeText,
                                        name = editProductsViewModel.nameText,
                                        quantity = editProductsViewModel.quantityText,
                                        brands = if (editProductsViewModel.brandsText.contains(",")) editProductsViewModel.brandsText.split(",") else listOf(
                                            editProductsViewModel.brandsText
                                        ),
                                        category = viewModelCategories.categoriesSelected.id,
                                        numberOfProducts = editProductsViewModel.numberOfProductsText
                                    ),
                                    user = userData,
                                    nameStorage =
                                )

                                editProductsViewModel.setProduct(Product())
                                viewModelCategories.updateCategorySelected(Categories())

                                navController.navigate("list_products") {
                                    popUpTo("show_info_item") { inclusive = true }
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = when (navBackStackEntry?.destination?.route) {
                            "show_info_item" -> Icons.Filled.CloudUpload

                            "list_products" -> Icons.Outlined.FilterList

                            "filter_products" -> Icons.Filled.FilterList

                            else -> { Icons.Filled.FilterList }
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            */

            NavHost(
                navController = navController,
                startDestination = "panoramic_menu"
            ) {

                composable(
                    route = "panoramic_menu"

                ) {

                    PanoramicInfo(
                        viewModelStorage = viewModelStorage,
                        viewModelProducts = viewModelProducts,
                        viewModelCategories = viewModelCategories,
                        navController = navController,
                        changeStorageSelected = {
                            storageSelected = it
                        },
                        userData = userData
                    )

                }

                composable(
                    route = "items_menu"
                ) {
                    ProductsScreen(
                        viewModelProducts = viewModelProducts,
                        viewModelCategories = viewModelCategories,
                        editProductsViewModel = editProductsViewModel,
                        navController = navController,
                        userData = userData,
                        storageSelected = storageSelected
                    )
                }

            }

            /*

            NavHost(
                navController = navController,
                startDestination = "list_products"
            ) {

                //TODO(Deprecated)
                composable(
                    route = "list_products"
                ) {

                    Crossfade(
                        targetState = viewModelProducts.isLoading,
                        label = "crossfade"

                    ) { loading ->

                        if (loading) {

                            // Mostra l'animazione di caricamento
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {



                            ListProducts(
                                viewModelProducts = viewModelProducts,
                                viewModelCategories = viewModelCategories,
                                editProductsViewModel = editProductsViewModel,
                                navController = navController,
                                userData = userData
                            )
                        }
                    }
                }

                composable(
                    route = "filter_products"
                ) {
                    Crossfade(targetState = viewModelCategories.isLoading, label = "crossfade") { loading ->
                        if (loading) {
                            // Mostra l'animazione di caricamento
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {
                            SelectCategories(
                                categoriesViewModel = viewModelCategories,
                                filterMode = true
                            )
                        }
                    }
                }

                composable(
                    route = "select_categories"
                ) {
                    SelectCategories(
                        categoriesViewModel = viewModelCategories
                    )
                }

                composable(
                    route = "show_info_item"
                ) {
                    EditProductScan(
                        editProductsViewModel = editProductsViewModel,
                        categoriesViewModel = viewModelCategories,
                        navController = navController,
                    )
                }
            }

            */
        }
    }

}