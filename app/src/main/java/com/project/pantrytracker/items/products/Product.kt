package com.project.pantrytracker.items.products

import java.util.UUID

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
    var id: String = UUID.randomUUID().toString(),

    val barcode        : String,
    val name           : String,
    val category       : String = "",
    val quantity       : Int,
    val quantityProduct: Int,
    val price          : Float,
    val notes          : String,
    val addDate        : String,
    val expiredDate    : String,
    val isUsed         : Boolean = false
) {
    constructor() : this(
        id               = UUID.randomUUID().toString(),
        barcode          = ""  ,
        name             = ""  ,
        category         = ""  ,
        quantity         = 0   ,
        quantityProduct  = 0   ,
        price            = 0.0f,
        notes            = ""  ,
        addDate          = ""  ,
        expiredDate      = ""
    )
}
