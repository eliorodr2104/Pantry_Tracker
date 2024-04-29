package com.project.pantrytracker.DataItems

data class Product(
    var barcode: String,
    var name: String,
    val quantity: String,
    val brands: List<String>,
    val category: String = "",
    var numberOfProducts: Int
) {
    constructor() : this("", "", "", listOf(), "", 1)
}
