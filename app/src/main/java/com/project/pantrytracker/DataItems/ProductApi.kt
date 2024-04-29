package com.project.pantrytracker.DataItems

/**
 * Rappresenta un prodotto proveniente da una chiamata API, con informazioni quali codice a barre,
 * nome, quantità, marche, categoria, numero di prodotti e un'eventuale eccezione.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param barcode Variabile di tipo String con il codice a barre del prodotto (opzionale, default: "").
 * @param name Variabile di tipo String con il nome del prodotto (opzionale, default: "").
 * @param quantity Variabile di tipo String con la quantità del prodotto (opzionale, default: "").
 * @param brands Lista di tipo String con le marche associate al prodotto (opzionale, default: lista vuota).
 * @param category Variabile di tipo String con la categoria del prodotto (opzionale, default: "").
 * @param numberOfProducts Variabile di tipo Int con il numero di prodotti disponibili (opzionale, default: 1).
 * @param exception Vaiabile di tipo Exception con un'eccezione eventualmente generata
 * durante la chiamata API (opzionale, default: null).
 */
data class ProductApi(
    val barcode: String = "",
    val name: String = "",
    val quantity: String = "",
    val brands: List<String> = listOf(),
    val category: String = "",
    val numberOfProducts: Int = 1,
    val exception: Exception? = null
)
