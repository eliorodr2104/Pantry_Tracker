package com.project.pantrytracker.DataItems

import androidx.compose.runtime.Composable

data class BottomBarItemData(
    val selected: Boolean,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
    val enable: Boolean = true,
    val label: @Composable () -> Unit,
    val alwaysShowLabel: Boolean
)
