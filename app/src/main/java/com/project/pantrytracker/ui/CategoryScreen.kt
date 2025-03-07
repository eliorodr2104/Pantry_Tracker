package com.project.pantrytracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.pantrytracker.items.categories.Categories
import com.project.pantrytracker.viewmodels.CategoriesViewModel

@Composable
fun SelectCategories(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    categoriesViewModel: CategoriesViewModel,
    filterMode: Boolean = false
) {
    val categories by categoriesViewModel.categories.collectAsState()

    Crossfade(targetState = categoriesViewModel.isLoading, label = "crossfade") { loading ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .clip(shape = ShapeDefaults.Medium)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
                itemsIndexed(
                    items = categories,
                    key = { _, item -> item.id }
                ) { index, item ->

                    ItemColumnFormWithDivider(
                        showDivider = index < categories.lastIndex
                    ) {
                        ItemSelectCategory(
                            category = item,
                            categoriesViewModel = categoriesViewModel,
                            filterMode = filterMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemSelectCategory(
    category: Categories,
    categoriesViewModel: CategoriesViewModel,
    filterMode: Boolean
) {
    Row(
        modifier = Modifier
            .clickable {
                when (filterMode) {
                    false -> {
                        if (categoriesViewModel.categoriesSelected != category)
                            categoriesViewModel.updateCategorySelected(category)
                        else
                            categoriesViewModel.updateCategorySelected(Categories())
                    }
                    true -> {
                        if (categoriesViewModel.categoryFilterSelected != category)
                            categoriesViewModel.categoryFilterSelected = category
                        else
                            categoriesViewModel.categoryFilterSelected = Categories()
                    }
                }
            }
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        AnimatedVisibility(
            visible = when (filterMode) {
                false -> categoriesViewModel.categoriesSelected == category
                true -> categoriesViewModel.categoryFilterSelected == category
            },
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}