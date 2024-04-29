package com.project.pantrytracker.DataItems

/**
 * Rappresenta un prodotto con informazioni quali codice a barre, nome, quantità, marche,
 * categoria e numero di prodotti.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 *
 * @param barcode Variabile di tipo String con il codice a barre del prodotto.
 * @param name Variabile di tipo String con il nome del prodotto.
 * @param quantity Variabile di tipo String con la quantità del prodotto.
 * @param brands Lista di tipo String con le marche associate al prodotto.
 * @param category Variabile di tipo String con la categoria del prodotto (opzionale, default: "").
 * @param numberOfProducts Variabile di tipo Int con il numero di prodotti disponibili.
 */
data class Product(
    var barcode: String,
    var name: String,
    val quantity: String,
    val brands: List<String>,
    val category: String = "",
    var numberOfProducts: Int
)
 //{
//constructor() : this("", "", "", listOf(), "", 1)
//}
