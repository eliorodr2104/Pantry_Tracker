package com.project.pantrytracker.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.products.Product
import com.project.pantrytracker.items.products.Storage
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductEditViewModel
import com.project.pantrytracker.viewmodels.ProductsViewModel

@Composable
fun ProductsScreen(
    viewModelProducts    : ProductsViewModel,
    viewModelCategories  : CategoriesViewModel,
    editProductsViewModel: ProductEditViewModel,
    navController        : NavController,
    userData             : UserData?,
    storageSelected      : Storage
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface
            )
    ) {

        ManagementScreenRow(
            navController = navController,
            modifier = Modifier
                .align(
                    alignment = Alignment.TopCenter
                )
        )

        ProductList(
            storage = storageSelected,
            userData = userData,
            viewModelProducts = viewModelProducts,
            modifier = Modifier
                .align(
                    alignment = Alignment.Center
                )
        )

        ManagementProducts(
            navController = navController,
            viewModelProducts = viewModelProducts,
            nameStorage = storageSelected.name,
            userData = userData,
            modifier = Modifier
                .align(
                    alignment = Alignment.BottomCenter
                )
        )
    }

}

@Composable
private fun ProductList(
    modifier          : Modifier,
    viewModelProducts: ProductsViewModel,
    userData         : UserData?,
    storage          : Storage,
) {

    val products by viewModelProducts.productsStorage.collectAsState(initial = emptyMap())

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        viewModelProducts.getAllStorageProducts(
            user = userData
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = 35.dp,
                start = 15.dp,
                end = 15.dp
            ),

        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start

    ) {

        Text(
            text = storage.name,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold

        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            value = searchText,
            onValueChange = {
                searchText = it

                //filteredProducts = if (it != "") {
                //    products.filter { product ->
                //        product.name.contains(searchText, ignoreCase = true)
                //    }

                //} else
                //    products
            },
            shape = ShapeDefaults.Medium,
            label = {
                Text(
                    text = "Search product",

                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Product",

                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.DocumentScanner,
                    contentDescription = "Search barcode Product",

                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor   = Color.Transparent,
                errorIndicatorColor     = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor  = Color.Transparent,

                focusedContainerColor   = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),

            singleLine = true
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.tertiary.copy(
                alpha = 0.3f
            ),
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 10.dp
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            products[storage]?.let {
                items(
                    items = it.products,
                    key = { it.id }
                ) { product ->
                    Text(product.name)
                }
            }
        }
    }
}

@Composable
private fun ManagementProducts(
    viewModelProducts: ProductsViewModel,
    navController    : NavController,
    nameStorage      : String,
    userData         : UserData?,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                all = 15.dp
            )
            .clip(
                shape = ShapeDefaults.Medium
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            .padding(
                all = 10.dp
            ),

        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),

            modifier = Modifier
                .clickable {
                    viewModelProducts.addProduct(
                        user = userData,
                        nameStorage = nameStorage,
                        product = Product(
                            barcode          = "0000000001"  ,
                            name             = "Test1"  ,
                            category         = "Snacks"  ,
                            quantity         = 1   ,
                            quantityProduct  = 1   ,
                            price            = 5.0f,
                            notes            = ""  ,
                            addDate          = "17/02/25"  ,
                            expiredDate      = "21/02/25"
                        )
                    )
                    //navController.navigate("panoramic_menu")
                }
        ) {

            Icon(
                imageVector = Icons.Outlined.AddBox,
                contentDescription = "Back menu"
            )

            Text(
                text = "Add product"
            )

        }

        Icon(
            imageVector = Icons.Outlined.DocumentScanner,
            contentDescription = "Scan Product"
        )

    }
}

@Composable
private fun ManagementScreenRow(
    navController: NavController,
    modifier      : Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 15.dp,
                end   = 15.dp
            ),

        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),

            modifier = Modifier
                .clickable {
                    navController.navigate("panoramic_menu")
                }
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Back menu",

                tint = blueIcon
            )

            Text(
                text  = "Panoramic",
                color = blueIcon
            )

        }

        Icon(
            imageVector = Icons.Outlined.Menu,
            contentDescription = "Settings list"
        )

    }

}

/*
@Composable
private fun ListProducts (
    viewModelProducts: ProductsViewModel,
    viewModelCategories: CategoriesViewModel,
    editProductsViewModel: ProductEditViewModel,
    navController: NavController,
    userData: UserData?
) {
    val products by viewModelProducts.products.collectAsState(initial = emptyList())
    val categoriesSelected = viewModelCategories.categoryFilterSelected

    var filteredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }

    var isRefreshing by remember { mutableStateOf(false) }

    var isSearchVisible by remember { mutableStateOf(false) }

    LaunchedEffect(products, categoriesSelected) {
        if (categoriesSelected.name != "") {
            val filtered = products.filter { product ->
                product.category == categoriesSelected.id
            }
            filteredProducts = filtered

        } else
            filteredProducts = products
    }

    LaunchedEffect(isRefreshing) {
        delay(1000)
        isRefreshing = false
    }

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 0)
                        isSearchVisible = true

                    if (dragAmount < 0)
                        isSearchVisible = false

                }
            },
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModelProducts.getProducts(userData)
        }
    ) {
        Column {
            AnimatedVisibility(
                visible = isSearchVisible
            ) {
                //TOLTO TEXTFIELD PER IMPLEMENTARLO

            LazyColumn(
                modifier = Modifier
                    .clip(shape = ShapeDefaults.Medium)
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
            ) {
                itemsIndexed(
                    items = filteredProducts,
                    key = { _, item -> item.id }
                ) { index, item ->

                    ItemColumnFormWithDivider(
                        showDivider = index < filteredProducts.lastIndex
                    ) {
                        SwipeBox(
                            onDelete = {
                                viewModelProducts.deleteProduct(user = userData, product = item)
                            },
                            modifier = Modifier
                                .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                .clickable {
                                    editProductsViewModel.setProduct(item)
                                    navController.navigate("show_info_item")
                                }
                        ) {
                            ItemProduct(product = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwipeBox (
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()

    lateinit var icon: ImageVector
    lateinit var alignment: Alignment
    val color: Color

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> {
            icon = Icons.Outlined.Delete
            alignment = Alignment.CenterEnd
            color = MaterialTheme.colorScheme.errorContainer
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon = Icons.Outlined.Edit
            alignment = Alignment.CenterStart
            color = MaterialTheme.colorScheme.primaryContainer
        }

        SwipeToDismissBoxValue.Settled -> {
            icon = Icons.Outlined.Delete
            alignment = Alignment.CenterEnd
            color = MaterialTheme.colorScheme.errorContainer
        }
    }

    SwipeToDismissBox(
        modifier = modifier.animateContentSize(),
        state = swipeState,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            ) {
                Icon(
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    imageVector = icon, contentDescription = null,
                    tint = if (color == MaterialTheme.colorScheme.primaryContainer) color else MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        content()
    }

    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }
}

@Composable
private fun ItemProduct (
    product: Product
) {

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer
            )
            .clip(
                shape = ShapeDefaults.Medium
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
            modifier = Modifier
                .size(
                    size = 15.dp
                )
        )
    }
}
*/