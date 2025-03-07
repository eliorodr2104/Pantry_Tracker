package com.project.pantrytracker.items.uiItems

data class ColorItem(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float
) {
    constructor() : this (
        red   = 0f,
        green = 0f,
        blue  = 0f,
        alpha = 0f
    )
}