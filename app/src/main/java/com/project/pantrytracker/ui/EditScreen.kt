package com.project.pantrytracker.ui

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.pantrytracker.items.categories.Categories
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductEditViewModel

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
fun EditProductScan(
    editProductsViewModel: ProductEditViewModel,
    categoriesViewModel: CategoriesViewModel,
    navController: NavController,
) {
    val categories by categoriesViewModel.categories.collectAsState()

    if (categoriesViewModel.categoriesSelected.name == "")
        categoriesViewModel.updateCategorySelected(
            categories.find {
                it.id == editProductsViewModel.product.value.category
            } ?: Categories()
        )

    val showAlertBarcode by remember { mutableStateOf(false) }
    val showAlertName by remember { mutableStateOf(false) }
    val showAlertQuantity by remember { mutableStateOf(false) }
    val showAlertBrands by remember { mutableStateOf(false) }

    /*
    ColumnForm {
        CustomTextField(
            value = editProductsViewModel.barcodeText,
            onValueChange = { editProductsViewModel.updateBarcodeText(it) },
            titleTextField = "Barcode",
            modifier = Modifier.fillMaxWidth(),
            isError = showAlertBarcode
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        )

        CustomTextField(
            value = editProductsViewModel.nameText,
            onValueChange = { editProductsViewModel.updateNameText(it) },
            titleTextField = "Name",
            modifier = Modifier.fillMaxWidth(),
            isError = showAlertName
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        )

        CustomTextField(
            value = editProductsViewModel.quantityText,
            onValueChange = { editProductsViewModel.updateQuantityText(it) },
            titleTextField = "Quantity",
            modifier = Modifier.fillMaxWidth(),
            isError = showAlertQuantity
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        )

        CustomTextField(
            value = editProductsViewModel.brandsText,
            onValueChange = { editProductsViewModel.updateBrandsText(it) },
            titleTextField = "Brands",
            modifier = Modifier.fillMaxWidth(),
            isError = showAlertBrands
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        )

        ItemForm {
            Text(
                text = "Amount",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            FadingEdgeNumberPicker(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                onNumberSelected = { editProductsViewModel.updateNumberOfProducts(it) },
                initialNumber = editProductsViewModel.numberOfProductsText
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
        )

        ItemForm (
            modifier = Modifier
                .clickable {
                    navController.navigate("select_categories")
                }
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = categoriesViewModel.categoriesSelected.name,
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
    }
    */
}

/**
 * Metodo che definisce un composable per la creazione di un TextField custom.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param value String che contiene il valore della stringa.
 * @param onValueChange Funzione per modificare il valore della stringa.
 * @param titleTextField Stringa che contiene il titolo del textfield.
 * @param labelTextField Funzione composable per il label interno del textfield.
 * @param isError Variabile Boolean, la quale serve a notificare se c'è un errore nell'inserimento
 * dei dati.
 * @param modifier Variabile Modifier per modificare l'aspetto del TextField.
 */
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    titleTextField: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = titleTextField,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth(0.5f),
            keyboardOptions = KeyboardOptions.Companion.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            )
        )
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun FadingEdgeNumberPicker(
    modifier: Modifier,
    initialNumber: Int,
    onNumberSelected: (Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    // Calcola l'indice dell'elemento centrale basato sul numero iniziale
    val initialIndex = (initialNumber - 1).coerceIn(0, 50)

    // Imposta la posizione iniziale della LazyRow
    LaunchedEffect(initialIndex) {
        lazyListState.scrollToItem(initialIndex)
    }

    val leftRightFade = Brush.horizontalGradient(
        0f to Transparent,
        0.5f to Red,
        0.5f to Red,
        1f to Transparent
    )

    LazyRow(
        state = lazyListState,
        flingBehavior = snapBehavior,
        modifier = modifier
            .fadingEdge(brush = leftRightFade),
        contentPadding = PaddingValues(start = 60.dp, end = 60.dp)
    ) {
        items(51) {
            if (it > 0) {
                Text(
                    text = it.toString(),
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier
                        .size(30.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            val centralIndex = firstVisibleItemIndex + 1
            if (centralIndex in 1..50) {
                onNumberSelected(centralIndex)
            }
        }
    }
}