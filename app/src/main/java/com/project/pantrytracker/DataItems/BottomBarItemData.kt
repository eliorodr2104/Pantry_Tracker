package com.project.pantrytracker.DataItems

import androidx.compose.runtime.Composable

/**
 * Data class con i parametri per magazzinare i valori dei pulsanti della barra di navigazione.
 * @author Eliomar Alejandro Rodriguez Ferrer.
 * @param selected Per indicare se il pulsante è premuto.
 * @param onClick Funzione di callback per fare qualcosa quando il pulsante viene premuto.
 * @param icon Funzione composable con l'icona del pulsante da mostrare.
 * @param enable Variabile Boolean che determina se il pulsante è abilitato.
 * @param label Funzione composable che ha il label da mostrare sotto il pulsante.
 * @param alwaysShowLabel Variabile Boolean che determina se mostrare il label.
 */
data class BottomBarItemData(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val enable: Boolean = true,
    val label: @Composable () -> Unit,
    val alwaysShowLabel: Boolean
)
