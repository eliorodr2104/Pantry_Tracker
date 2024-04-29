package com.project.pantrytracker.DataItems

data class ProductApi(
    val barcode: String = "",
    val name: String = "",
    val quantity: String = "",
    val brands: List<String> = listOf(),
    val category: String = "",
    val numberOfProducts: Int = 1,
    val exception: Exception? = null
)