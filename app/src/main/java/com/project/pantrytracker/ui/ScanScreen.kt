package com.project.pantrytracker.ui

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.barcode.BarcodeScanner.BarCodeAnalyser
import com.project.pantrytracker.barcode.barcodeApi.BarcodeApi
import com.project.pantrytracker.items.categories.Categories
import com.project.pantrytracker.items.products.Product
import com.project.pantrytracker.items.products.ProductApi
import com.project.pantrytracker.items.enumsData.Screens
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductEditViewModel
import com.project.pantrytracker.viewmodels.ProductsViewModel
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.util.concurrent.Executors

/**
 * Metodo che definisce un composable per la scansione e l'apposita modifica di prodotti.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param userData Oggetto contenente i dati dell'utente loggato.
 * @param paddingValues Spaziatura intorno al contenuto del composable per lo Scaffolf.
 * @param changeMenu Funzione per cambiare il menu dell'applicazione.
 */
@Composable
fun UseScanScreen(
    categoriesViewModel: CategoriesViewModel,
    productsViewModel: ProductsViewModel,
    userData: UserData?,
    changeMenu: (Screens) -> Unit
) {
    // Inizializzazione del NavController per la navigazione tra i composable
    val navController = rememberNavController()

    val editProductsViewModel = viewModel<ProductEditViewModel>()

    // Osserva i cambiamenti di destinazione del NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(navBackStackEntry?.destination?.route) {
        when (navBackStackEntry?.destination?.route) {
            "scan_screen" -> changeMenu(Screens.SCAN)
            "modifier_product" -> changeMenu(Screens.MODIFY_PRODUCT)
            "select_categories" -> changeMenu(Screens.CATEGORIES_SELECT)
        }
    }

    //Definizione del NavHost per gestire la navigazione tra le schermate
    NavHost(navController = navController, startDestination = "scan_screen") {
        //Schermata per la scansione del prodotto
        composable(
            route = "scan_screen"
        ) {
            ScanScreen(
                changeProduct = { item ->
                    if (item != null)
                        editProductsViewModel.setProduct(item)
                },
                navController = navController,
                changeMenu = changeMenu
            )
        }

        //Schermata per la modifica del prodotto scannerizzato
        composable(
            route = "modifier_product"
        ) {


            Column(
                modifier = Modifier
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                TopAppBar(
                    titleText = "Edit",
                    subTitleText = "Form to edit the product"
                ) {
                    IconButton(
                        onClick = {
                            /*
                            productsViewModel.addProduct(
                                product = Product(
                                    barcode = editProductsViewModel.barcodeText,
                                    name = editProductsViewModel.nameText,
                                    quantity = editProductsViewModel.quantityText,
                                    brands = if (editProductsViewModel.brandsText.contains(",")) editProductsViewModel.brandsText.split(",") else listOf(
                                        editProductsViewModel.brandsText
                                    ),
                                    category = categoriesViewModel.categoriesSelected.id,
                                    numberOfProducts = editProductsViewModel.numberOfProductsText
                                ),
                                user = userData
                            )
                            */

                            editProductsViewModel.setProduct(Product())
                            categoriesViewModel.updateCategorySelected(Categories())


                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                EditProductScan(
                    editProductsViewModel = editProductsViewModel,
                    categoriesViewModel = categoriesViewModel,
                    navController = navController
                )
            }
        }

        composable(
            route = "select_categories"
        ) {
            SelectCategories(
                modifier = Modifier
                    .padding(
                        start = 15.dp,
                        end = 15.dp
                    ),
                categoriesViewModel = categoriesViewModel
            )
        }
    }
}

/**
 * Metodo che definisce un composable per la scansione del prodotto.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param navController Oggetto per la navigazione tra i composable.
 * @param changeProduct Funzione per modificare il prodotto una volta scannerizzato.
 * @param paddingValues Spaziatura intorno al contenuto del composable per lo Scaffolf.
 */
@Composable
private fun ScanScreen(
    navController: NavController,
    changeProduct: (Product?) -> Unit,
    changeMenu: (Screens) -> Unit
) {
    var showAlertDialog by remember {
        mutableStateOf(false)
    }
    var titleErrorAlert by remember {
        mutableStateOf("")
    }
    var bodyErrorAlert by remember {
        mutableStateOf("")
    }

    var tempProductApi by remember {
        mutableStateOf(ProductApi())
    }

    //Variabile di stato mutabile che contiene un valore booleano per
    // fermare il processo della telecamera.
    var cameraPaused by remember { mutableStateOf(false) }

    //Column iniziale che occupa tutto lo schermo e ha al suo interno tutta la grafica
    //della schermata
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {

        TopAppBar(
            titleText = "Scan",
            subTitleText = "Scan the barcode"
        )

        //Box contenente la visuale della telecamera
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    shape = ShapeDefaults.Medium
                )
        ) {
            //Funzione che mostra la telecamera in uso
            CameraBarcodeScan(
                changeProduct = changeProduct,
                navController = navController,
                changeShowAlertDialog = { showAlertDialog = it },
                changeTextError = { titleErrorAlert = it },
                changeBodyError = { bodyErrorAlert = it },
                changeTempProductApi = { tempProductApi = it },
                cameraPaused = cameraPaused,
                changeCameraPaused = { cameraPaused = it },

                changeMenu = changeMenu
            )
        }

        /*
        if (showAlertDialog)
            AlertDialogError(
                changeViewDialog = { showAlertDialog = it },
                titleText = titleErrorAlert,
                bodyText = bodyErrorAlert,
                changeProduct = changeProduct,
                product = Product(
                    barcode = tempProductApi.barcode,
                    name = tempProductApi.name,
                    quantity = tempProductApi.quantity,
                    brands = tempProductApi.brands,
                    category = tempProductApi.category,
                    numberOfProducts = tempProductApi.numberOfProducts
                ),
                changeCameraPaused = { cameraPaused = it },
                navController = navController
            )
        */
    }
}

/**
 * Metodo che definisce un composable per la telecamera che scannerizza i barcode.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param changeProduct Funzione per modificare il prodotto preso dall'api.
 * @param changeShowAlertDialog Funzione che prende nella firma un Boolean e modifica la visibilità
 * del Alert.
 * @param changeTextError Funzione che prende nella firma una Stringa e cambia il titolo del Alert.
 * @param changeBodyError Funzione che prende nella firma una Stringa e cambia il body del Alert.
 * @param changeTempProductApi Funzione che prende nella firma un oggetto ProductApi e lo cambia per
 * quello corrente.
 * @param navController Oggetto per la navigazione tra i composable.
 * @param cameraPaused Variabile Boolean che viene utilizzata per stabilire lo stato della fotocamera.
 * @param changeCameraPaused Funzione che prende nella firma un Boolean e cambia lo stato della fotocamera.
 */
@Composable
private fun CameraBarcodeScan(
    changeProduct: (Product?) -> Unit,
    changeShowAlertDialog: (Boolean) -> Unit,
    changeTextError: (String) -> Unit,
    changeBodyError: (String) -> Unit,
    changeTempProductApi: (ProductApi) -> Unit,
    changeMenu: (Screens) -> Unit,
    navController: NavController,
    cameraPaused: Boolean,
    changeCameraPaused: (Boolean) -> Unit
) {
    var tempProductApi by remember { mutableStateOf(ProductApi()) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }
    var barCodeVal by remember { mutableStateOf("") }

    LaunchedEffect(barCodeVal) {
        if (barCodeVal.isNotEmpty()) {
            tempProductApi = BarcodeApi().searchBarcode(barCodeVal)
            changeTempProductApi(tempProductApi)

            when(tempProductApi.exception) {
                is ConnectTimeoutException -> {
                    changeTextError("Error connection")
                    changeBodyError("The connection to the API failed")
                    changeShowAlertDialog(true)
                }
                is HttpRequestTimeoutException -> {
                    changeTextError("Error request")
                    changeBodyError("The request to api was unsuccessful")
                    changeShowAlertDialog(true)
                }
                is SocketTimeoutException -> {
                    changeTextError("Error socket")
                    changeBodyError("The session with the api was interrupted")
                    changeShowAlertDialog(true)
                }
                null -> {
                    /*
                    changeProduct(
                        Product(
                            barcode = tempProductApi.barcode,
                            name = tempProductApi.name,
                            quantity = tempProductApi.quantity,
                            brands = tempProductApi.brands,
                            category = tempProductApi.category,
                            numberOfProducts = tempProductApi.numberOfProducts
                        )
                    )
                    */
                    changeMenu(Screens.MODIFY_PRODUCT)
                    navController.navigate("modifier_product")
                }
            }
            barCodeVal = ""
        }
    }

    // Ensure camera provider is unbound when composable is disposed
    DisposableEffect(context) {
        onDispose {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            cameraProvider.unbindAll()
        }
    }

    AndroidView(
        factory = { androidViewContext ->
            PreviewView(androidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                preview = androidx.camera.core.Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { barcodeValue ->
                            barCodeVal = barcodeValue
                            cameraProvider.unbindAll()
                            changeCameraPaused(true)
                        }
                    }
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().apply {
                        if (!cameraPaused) {
                            setAnalyzer(cameraExecutor, barcodeAnalyser)
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    if (!cameraPaused) {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

/**
 * Funzione Composable AlertDialogError, la quale visualizza eventuali errori dalla richiesta HTTP.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param changeViewDialog Funzione che prende come prametro un Boolean la quale serve a modficare
 * lo stato della visibilità del Alert.
 * @param titleText Variabile di tipo String che ha il titolo del Alert.
 * @param bodyText Variabile di tipo String che ha la spiegazione del Alert.
 * @param changeProduct Funzione la quale prende come parametro un oggetto Product e
 * serve a modificare il prodotto a quello corrente.
 * @param product Variabile prodotto la quale è una istanza dell'oggetto Product.
 * @param changeCameraPaused Funzione che prende come parametro un Boolean, la quale serve a stabilire
 * lo stato della fotocamera come non pausato se si devide scannerizzare di nuovo il prodotto.
 * @param navController Variabile di tipo NavController la quale serve a modificare la vista principale.
 */
@Composable
private fun AlertDialogError(
    changeViewDialog: (Boolean) -> Unit,
    titleText: String,
    bodyText: String,
    changeProduct: (Product) -> Unit,
    product: Product,
    changeCameraPaused: (Boolean) -> Unit,
    navController: NavController
) {
    AlertDialog(
        onDismissRequest = {
            changeViewDialog(false)
        },

        title = {
            Text(
                text = titleText
            )
        },

        text = {
            Text(
                text = bodyText
            )
        },

        confirmButton = {
            Button(
                onClick = {
                    changeProduct(product)
                    navController.navigate("modifier_product")
                    changeViewDialog(false)
                }
            ) {
                Text("Create manually")
            }
        },

        dismissButton = {
            Button(
                onClick = {
                    changeCameraPaused(false)
                    changeViewDialog(false)
                }
            ) {
                Text("Re-scan")
            }
        }
    )
}