package com.project.pantrytracker.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.project.pantrytracker.items.products.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProductEditViewModel : ViewModel() {
    private val _product = MutableStateFlow(Product())
    val product: StateFlow<Product> = _product

    // Variabili di stato per i campi di input
    var barcodeText by mutableStateOf(product.value.barcode)
        private set

    var nameText by mutableStateOf(product.value.name)
        private set

    var quantityText by mutableStateOf(product.value.quantity)
        private set

    //var brandsText by mutableStateOf(product.value.brands.toString())
    //    private set

    //var numberOfProductsText by mutableIntStateOf(product.value.numberOfProducts)
    //    private set

    fun setProduct(product: Product) {
        _product.value = product

        barcodeText = product.barcode
        nameText = product.name
        //quantityText = if (product.quantity == "null") "" else product.quantity
        //brandsText = product.brands.joinToString()
        //numberOfProductsText = product.numberOfProducts
    }

    fun updateBarcodeText(newBarcode: String) {
        barcodeText = newBarcode
    }

    fun updateNameText(newName: String) {
        nameText = newName
    }

    //fun updateQuantityText(newQuantity: String) {
    //    quantityText = newQuantity
    //}

    //fun updateBrandsText(newBrands: String) {
    //    brandsText = newBrands
    //}

    //fun updateNumberOfProducts(newNumber: Int) {
    //    numberOfProductsText = newNumber
    //}
}