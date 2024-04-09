package com.project.pantrytracker.ui

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.common.util.concurrent.ListenableFuture
import com.project.pantrytracker.BarcodeScanner.BarCodeAnalyser
import com.project.pantrytracker.DataItems.CategoriesItemData
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.Firebase.addProductDb
import com.project.pantrytracker.R
import com.project.pantrytracker.barcodeApi.BarcodeApi
import com.project.pantrytracker.enumsData.Screens
import java.util.concurrent.ExecutorService
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
    userData: UserData?,
    paddingValues: PaddingValues,
    changeMenu: (Screens) -> Unit
) {
    // Inizializzazione del NavController per la navigazione tra i composable
    val navController = rememberNavController()

    // Definizione di una variabile mutabile di stato per il prodotto
    var product by remember {
        mutableStateOf(Product("", "", "", emptyList(), "", false))
    }

    //Definizione del NavHost per gestire la navigazione tra le schermate
    NavHost(navController = navController, startDestination = "initial_scan") {
        //Schermata iniziale per la scansione del prodotto
        composable("initial_scan") {
            InitialScanScreen(
                navController = navController,
                paddingValues
            )
        }

        //Schermata per la scansione del prodotto
        composable("scan_screen") {
            ScanScreen(
                changeProduct = { item ->
                    if (item != null)
                        product = item
                },
                navController = navController,
                paddingValues = paddingValues
            )
        }

        //Schermata per la modifica del prodotto scannerizzato
        composable("modifier_product") {
            EditProductScan(
                product = product,
                userData = userData,
                paddingValues = paddingValues,
                changeMenu = changeMenu
            )
        }
    }
}

/**
 * Metodo che definisce un composable per la schermata iniziale, con la guida dell'uso.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param navController Oggetto per la navigazione tra i composable.
 * @param paddingValues Spaziatura intorno al contenuto del composable per lo Scaffolf.
 */
@Composable
private fun InitialScanScreen(
    navController: NavController,
    paddingValues: PaddingValues
) {
    //Column iniziale che occupa tutto lo schermo e contiene la schermata iniziale.
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(30.dp)
    ) {

        //Box che contiene la card con la scritta di guida.
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            //Card che contiene le indicazioni per l'utilizzo dell'app
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                //Column per ordinale le scritte in verticale.
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    //Text che contiene il titolo della guida.
                    Text(
                        text = "Scan Barcode",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )

                    //Text che contiene l'istruzioni per l'utilizzo
                    Text(
                        text = "Scan the barcode of the product to add it into the app",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        //Box contenente l'immagine di guida.
        Box(
            contentAlignment = Alignment.Center,
        ) {
            //Immagine di guida con un Barcode scannerizzato.
            //TODO Da modificare perché non entra bene con la schermata,
            // e anche gestire il colore con la modalità del dispositivo.
            Image(
                painterResource(R.drawable.barcode_image),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(330.dp)
            )
        }

        //Button che va alla schermata successiva
        Button(
            onClick = { navController.navigate("scan_screen") }, //Funzione eseguita all'esecuzione, cambia la schermata
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 70.dp,
                    end = 70.dp
                )
        ) {
            //Text con il testo del pulsante.
            Text(
                text = "Scan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(5.dp)
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
fun ScanScreen(
    navController: NavController,
    changeProduct: (Product?) -> Unit,
    paddingValues: PaddingValues
) {
    //Column iniziale che occupa tutto lo schermo e ha al suo interno tutta la grafica
    //della schermata
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
            .padding(paddingValues)
            .padding(
                top = 100.dp,
                bottom = 100.dp
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        //Card decorativa che al suo interno ha la visuale della camera. (Occupa tutto lo schermo).
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    shape = RoundedCornerShape(
                        size = 15.dp
                    )
                )
        ) {
            //Column per ordinare in maniera verticale centrata il testo e la view della camera.
            //TODO Da togliere un po' lo space, perché la visuale della telecamera è troppo piccola.
            // (FORSE HO RISOLTO! CAZZO SI!!)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(35.dp)
            ) {
                //Text che ha come testo ha ciò che si dovrebbe fare nella schermata
                Text(
                    text = "Scan the product",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                //Box contenente la visuale della telecamera
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    //Funzione che mostra la telecamera in uso
                    CameraBarcodeScan(
                        changeProduct = changeProduct,
                        navController = navController
                    )
                }
            }
        }
    }
}

/**
 * Metodo che definisce un composable per la modifica del prodotto scansionato.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param product Oggetto con le informazioni del prodotto scansionato.
 * @param userData Oggetto con l'utente che ha eseguito il login.
 * @param paddingValues Spaziatura intorno al contenuto del composable per lo Scaffolf.
 * @param changeMenu Funzione per cambiare il menù una volta salvato il prodotto.
 */
@Composable
private fun EditProductScan(
    product: Product,
    userData: UserData?,
    paddingValues: PaddingValues,
    changeMenu: (Screens) -> Unit
) {

    //Variabile di stato e mutabile che contiene il barcode del
    // prodotto scannerizzato, se esso c'è.
    var barcodeText by remember {
        mutableStateOf(product.barcode)
    }

    //Variabile di stato e mutabile che contiene il nome del
    // prodotto scannerizzato, se esso c'è.
    var nameText by remember {
        mutableStateOf(product.name)
    }
    //Variabile di stato e mutabile che contiene la quantità del
    // prodotto scannerizzato, se esso c'è.
    var quantityText by remember {
        mutableStateOf(product.quantity)
    }

    //Variabile di stato e mutabile che contiene i marchi del
    // prodotto scannerizzato, se esse ci sono.
    var brandsText by remember {
        mutableStateOf(product.brands.joinToString())
    }

    //Variabile di stato e mutabile che contiene la categoria del
    // prodotto scannerizzato, se essa c'è.
    var category by remember {
        mutableStateOf(product.category)
    }

    //Column principale, la quale occupa tutta la dimensione dello schermo e ha al suo interno
    //i componenti visuali.
    Column(
        verticalArrangement = Arrangement.spacedBy(25.dp),
        modifier = Modifier
            .padding(paddingValues)
            .padding(15.dp)
    ) {
        //Row con il testo principale e il pulsante per andare avanti.
        //TODO Eseguire dei maledetti controlli perché si rischia di far esplodere gli oggetti,
        // perché gli utenti sono stupidi.
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            //Text con il nome della schermata che si sta utilizzando.
            Text(
                text = "Add New Object",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            //Button il quale serve per salvare l'oggetto scannerizzato e successivamente modificato.
            //TODO Devo controllare l'oggetto che sto per salvare.
            Button(
                onClick = {
                    /*
                     * Controllo che ci sia un utente loggatto prima di salvare il prodotto nel DB.
                     *
                     * TODO Aggiungere ulteriore controllo su ogni variabile che va a finire come
                     *  parametro dell'oggetto, perché la gente scrive a cazzo (coño de la madre,
                     *  mmgvasea, no funciona bien).
                     */
                    if (userData != null) {

                        //Funzione che salva il prodotto preso come parametro nel DB Realtime.
                        //TODO Da ottimizzare di più, si rischiano conflitti I'M FUCKING INSANE.
                        addProductDb(
                            product = Product(
                                barcode = barcodeText,
                                name = nameText,
                                quantity = quantityText,
                                brands = if (brandsText.contains(",")) brandsText.split(",") else listOf(brandsText),
                                category = category,
                                availability = true
                            ),
                            user = userData
                        )
                    }

                    //Torna nella schermata iniziale del menù.
                    //(in teoria funge "spero").
                    changeMenu(Screens.HOME)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                //Text con il testo per il pulsante.
                Text(text = "Add")
            }
        }

        //Chiamata alla funzione composable che contiene la grafica per modificare l'oggetto.
        ListScanItem(
            barcodeText = barcodeText,
            changeBarcodeText = { barcodeText = it },
            nameText = nameText,
            changeNameText = { nameText = it },
            quantityText = quantityText,
            changeQuantityText = { quantityText = it },
            brandsText = brandsText,
            changeBrandsText = { brandsText = it },
            changeCategory = { category = it }
        )
    }
}

/**
 * Metodo che definisce un composable per la modifica di tutti i parametri del prodotto scansionato.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param barcodeText Stringa che contiene il barcode del prodotto.
 * @param changeBarcodeText Funzione per modificare la stringa contenente il barcode del prodotto.
 * @param nameText Stringa che contiene il nome del prodotto.
 * @param changeNameText Funzione per modificare la stringa contenete il nome del prodotto.
 * @param quantityText Stringa che contiene la dimensione del prodotto.
 * @param changeQuantityText Funzione per modificare la stringa contenente la dimensione del prodotto.
 * @param brandsText Stringa che contiene i marchi del prodotto.
 * @param changeBrandsText Funzione per modificare la stringa contenente i marchi del prodotto.
 * @param changeCategory Funzione per modificare la srringa contenente la categoria del prodotto.
 */
@Composable
private fun ListScanItem(
    barcodeText: String,
    changeBarcodeText: (String) -> Unit,
    nameText: String,
    changeNameText: (String) -> Unit,
    quantityText: String,
    changeQuantityText: (String) -> Unit,
    brandsText: String,
    changeBrandsText: (String) -> Unit,
    changeCategory: (String) -> Unit
) {
    //Lista con delle categorie in forma di test.
    val testItemsCategories = listOf(
        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),

        CategoriesItemData(
            "Fresh bread"
        ),
    )

    //Column principale, la quale occupa tutta la dimensione dello schermo e ha al suo interno
    //i componenti visuali.
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        //TextField customizzato per la modifica del barcode del prodotto.
        //TODO Devo renderlo non personalizzabile se las stringa non è vuota,
        // perché il barcode è sempre quello, e l'utente coglione se modifica
        // rischia di fare casino, quindi, sono un coglione.
        // Devo anche aggiungere un effetto visuale per i controlli.
        CustomTextField(
            value = barcodeText,
            onValueChange = changeBarcodeText,
            titleTextField = "Barcode",
            labelTextField = {
                Text(
                    text = "Write the product barcode"
                )
            }
        )

        //TextField customizzato per la modifica del nome del prodotto.
        // TODO Devo anche aggiungere un effetto visuale per i controlli.
        CustomTextField(
            value = nameText,
            onValueChange = changeNameText,
            titleTextField = "Name product",
            labelTextField = {
                Text(
                    text = "Write the name product"
                )
            }
        )

        //TextField customizzato per la modifica della dimensione del prodotto.
        // TODO Devo anche aggiungere un effetto visuale per i controlli.
        CustomTextField(
            value = quantityText,
            onValueChange = changeQuantityText,
            titleTextField = "Quantity product",
            labelTextField = {
                Text(
                    text = "Write the quantity of product"
                )
            }
        )

        //TextField customizzato per la modifica dei marchi del prodotto.
        // TODO Devo anche aggiungere un effetto visuale per i controlli.
        //  Devo anche aggiungere un controllo che i marchi possano essere soltanto spazziati con
        //  delle virgole, sennò al creare la lista con i marchi si richia il crash dell'app,
        //  dio non c'è la faccio più con tutti sti controlli del cazzo.
        CustomTextField(
            value = brandsText,
            onValueChange = changeBrandsText,
            titleTextField = "Brands products",
            labelTextField = {
                Text(
                    text = "Write the brands of the product"
                )
            }
        )

        //Column che contiene il testo che dice il sotto menù e una griglia con le
        // categorie da selezionare.
        Column {
            //Text con il nome del sotto menù.
            Text(
                text = "Category",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(
                        start = 5.dp,
                        bottom = 10.dp
                    )
            )

            //LazyHorizontalGrid che contiene in forma di griglia le categorie che
            // possono essere scelte.
            LazyHorizontalGrid(
                rows = GridCells.Adaptive(60.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //Itera la lista di categorie per essere mosrate nella griglia.
                items(testItemsCategories) { item ->

                    //Item della singola categoria.
                    CategoryItem(
                        categoriesItemData = item,
                        changeCategory = changeCategory
                    )
                }
            }
        }
    }
}

/**
 * Metodo che definisce un composable per gli item delle categorie che può essere
 * aggiunta al prodotto scansionato.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param categoriesItemData Oggetto con le informazioni della categoria a mostrare.
 * @param changeCategory Funzione per cambiare la categoria che è stata scelta.
 */
@Composable
private fun CategoryItem(
    categoriesItemData: CategoriesItemData,
    changeCategory: (String) -> Unit
) {
    //Box iniziale con l'item dentro.
    Box {
        //Box che contiene la forma visiva della categoria e il testo.
        Box(
            modifier = Modifier
                .clip(
                    shape = RoundedCornerShape(
                        size = 10.dp
                    )
                )
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                //Azione che fa quando viene cliccato.
                //TODO Devo far si che quando venga cliccato, quel oggetto cambi colore per
                // segnalare che è stato premuto, e quando un'altro venga selezionato
                // l'altro abbia il colore precedente (non so come cazzo si faccia).
                .clickable { changeCategory(categoriesItemData.nameCategory) },
        ) {
            //Text con il nome della categoria mostrata.
            Text(
                text = categoriesItemData.nameCategory,
                modifier = Modifier
                    .padding(15.dp)
            )
        }
    }
}

/**
 * Metodo che definisce un composable per la creazione di un TextField custom.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param value String che contiene il valore della stringa.
 * @param onValueChange Funzione per modificare il valore della stringa.
 * @param titleTextField Stringa che contiene il titolo del textfield.
 * @param labelTextField Funzione composable per il label interno del textfield.
 */
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    titleTextField: String,
    labelTextField: @Composable () -> Unit
){
    //Column che contiene il titolo e la textfield allineate a sinistra.
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        //Text che contiene il titolo della textfield
        Text(
            text = titleTextField,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(
                    start = 5.dp
                )
        )

        //TextField con i parametri settati.
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape =  RoundedCornerShape(5.dp),
            label = labelTextField,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

/**
 * Metodo che definisce un composable per la telecamera che scannerizza i barcode.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param changeProduct Funzione per modificare il prodotto preso dall'api.
 * @param navController Oggetto per la navigazione tra i composable.
 */
@Composable
private fun CameraBarcodeScan(
    changeProduct: (Product?) -> Unit,
    navController: NavController
) {
    //Variabile che ha il contesto della schermata in processo.
    val context = LocalContext.current

    //Variabile che ha il ciclo di vita della schermata corrente
    val lifecycleOwner = LocalLifecycleOwner.current

    //Variabile di stato mutabile che contiene il Preview della telecamera.
    var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }

    //Variabile di stato mutabile che contiene il barcode scannerizzato.
    var barCodeVal by remember { mutableStateOf("") }

    //Variabile di stato mutabile che contiene un valore booleano per
    // fermare il processo della telecamera.
    var cameraPaused by remember { mutableStateOf(false) }


    LaunchedEffect(
        key1 = barCodeVal != "" // Lancia l'effetto quando il valore del codice a barre non è vuoto.
    ) {

        //Verifica se il codice a barre non è vuoto.
        if (barCodeVal != "") {
            //Cerca il prodotto utilizzando il codice a barre.
            val product = BarcodeApi().searchBarcode(barCodeVal)

            //Mostra un messaggio Toast con il nome del prodotto (commentato)
            //Toast.makeText(context, product?.name ?: "niente", Toast.LENGTH_SHORT).show()

            //Cambia il prodotto corrente con quello restituito dalla
            //ricerca del codice a barre con l'api.
            changeProduct(product)

            //Naviga verso il composable "modifier_product".
            navController.navigate("modifier_product")

            //Resettare il valore del codice a barre a una stringa vuota.
            barCodeVal = ""
        }
    }

    //AndroidView rappresenta un'anteprima della fotocamera per la scansione di codici a barre.
    //TODO (BUG) Devo sistemare che all'avvio della fotocamera essa si spegne per un secondo
    // senza motivo apparente. (che cazzo ha!!!!)
    AndroidView(
        //Factory per creare l'AndroidView
        factory = { AndroidViewContext ->
            //Creazione di un PreviewView con determinate le sue determinate proprietà.
            PreviewView(AndroidViewContext).apply {
                //Impostazione del tipo di scala per riempire il centro.
                this.scaleType = PreviewView.ScaleType.FILL_CENTER

                //Impostazione dei parametri per corrispondere al genitore.
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )

                //Impostazione della modalità di implementazione
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        //Funzione di aggiornamento per configurare l'anteprima della fotocamera e la scansione del codice a barre.
        update = { previewView ->
            //Creazione di un selettore della fotocamera per scegliere la fotocamera posteriore.
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            //Creazione di un servizio executor per le operazioni della fotocamera.
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

            //Ottenimento di un oggetto future per il provider della fotocamera.
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)

            //Aggiunta di un ascoltatore
            cameraProviderFuture.addListener({
                //Inizializzazione di un'anteprima per visualizzare la fotocamera.
                preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                //Prende il provider della fotocamera dall'oggetto future.
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                //Crea un analizzatore di codici a barre.
                val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { barcodeValue ->
                            barCodeVal = barcodeValue

                            //Stoppa la telecamera e il rilevamento dei codici a barre
                            cameraProvider.unbindAll()
                            cameraPaused = true
                        }
                    }
                }

                //Crea un caso d'uso di analisi delle immagini per la scansione dei codici a barre.
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        if (!cameraPaused) {
                            it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                        }
                    }

                try {
                    //Scollega la telecamera prima di ricollegarla.
                    cameraProvider.unbindAll()

                    //Controlla se la telecamera non dev'essere stoppata, s'è
                    // non è così la raiccende.
                    if (!cameraPaused) {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    }

                    //Se la destinazione cambia da quella corrente, scollega la telecamera.
                    navController.addOnDestinationChangedListener { _, destination, _ ->
                        if (destination.id != destination.id) {
                            cameraProvider.unbindAll()
                        }
                    }

                } catch (e: Exception) {
                    Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}