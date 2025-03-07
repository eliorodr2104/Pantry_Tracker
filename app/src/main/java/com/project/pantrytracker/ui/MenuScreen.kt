package com.project.pantrytracker.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.enumsData.Screens
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductEditViewModel
import com.project.pantrytracker.viewmodels.ProductsViewModel
import com.project.pantrytracker.viewmodels.StorageViewModel

@Composable
fun MenuScreen(
    userData          : UserData?,
    signOut           : suspend () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val viewModelProducts     = viewModel<ProductsViewModel>   ()
    val viewModelStorage      = viewModel<StorageViewModel>    ()
    val categoriesViewModel   = viewModel<CategoriesViewModel> ()
    val editProductsViewModel = viewModel<ProductEditViewModel>()

    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    // Crea il launcher per richiedere il permesso
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()

    ) { isGranted ->

        if (isGranted) {
            navController.navigate("scan")

        }

    }

    var screenOption by remember {
        mutableStateOf(Screens.HOME)
    }

    var paddingValues: PaddingValues


    LaunchedEffect(Unit) {
        viewModelStorage.getAllStorage(
            user = userData
        )

        viewModelProducts.getAllStorageProducts(
            user = userData
        )

        //viewModelProducts.getAllProducts(
        //    user = userData
        //)

        //viewModelProducts.getListenerProduct(
        //    userData
        //)

        //viewModelProducts.getProducts(
        //    userData
        //)

        //categoriesViewModel.getListenerCategories()
        //categoriesViewModel.getCategories()
    }


    Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Scaffold(
            //floatingActionButton = {
                //if (navBackStackEntry?.destination?.route == "home") {

                    /*

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center

                    ) {
                        SmallFloatingActionButton(
                            onClick = {
                                navController.navigate("user_info")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Location FAB"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ExtendedFloatingActionButton(
                            onClick = {
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        navController.navigate("scan")
                                    }

                                    ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity,
                                        Manifest.permission.CAMERA
                                    ) -> {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }

                                    else -> {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Scan product"
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "Scan"
                            )
                        }
                    }

                    */
                //}
            //}

        ) { paddingValuesStandard ->

            paddingValues = PaddingValues(
                top = paddingValuesStandard.calculateTopPadding(),
                bottom = paddingValuesStandard.calculateBottomPadding()
            )

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {

                composable(
                    route = "home"
                ) {
                    HomeScreen(
                        paddingValues = paddingValues,
                        viewModelStorage = viewModelStorage,
                        viewModelProducts = viewModelProducts,
                        viewModelCategories = categoriesViewModel,
                        editProductsViewModel = editProductsViewModel,
                        userData = userData
                    )
                }

                composable(
                    route = "scan"
                ) {
                    UseScanScreen(
                        categoriesViewModel = categoriesViewModel,
                        changeMenu = { screenOption = it },
                        userData = userData,
                        productsViewModel = viewModelProducts
                    )
                }

                composable(
                    route = "user_info"
                ) {
                    UserScreen(
                        paddingValues = paddingValues,
                        userData = userData,
                        productsViewModel = viewModelProducts,
                        categoriesViewModel = categoriesViewModel,
                        signOut = signOut
                    )
                }
            }
        }
    }
}