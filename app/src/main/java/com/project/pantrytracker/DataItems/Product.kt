package com.project.pantrytracker.DataItems

data class Product(
    val barcode: String,
    val name: String,
    val quantity: String,
    val brands: List<String>,
    val category: String = "",
    val availability: Boolean
) {
    constructor() : this("", "", "", listOf(), "", false)
}
