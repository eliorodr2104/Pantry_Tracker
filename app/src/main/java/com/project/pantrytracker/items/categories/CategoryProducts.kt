package com.project.pantrytracker.items.categories

import com.project.pantrytracker.items.products.Product

data class CategoryProducts(
    val category: String,
    val products: List<Product>
)
