package com.project.pantrytracker.items.products

import com.project.pantrytracker.items.uiItems.ColorItem

data class Storage (
    val name: String = "",
    val icon: Int,
    val color: ColorItem
    //val products: List<Product> = emptyList()
) {
    constructor() : this (
        name  = "",
        icon  = 0,
        color = ColorItem()
    )
}