package com.project.pantrytracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.pantrytracker.DataItems.BottomBarItemData
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.Firebase.ProductsViewModel
import com.project.pantrytracker.enumsData.Screens.ADD_PRODUCT
import com.project.pantrytracker.enumsData.Screens.HOME
import com.project.pantrytracker.enumsData.Screens.LIST_PRODUCTS

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    userData: UserData? = UserData("", "", null)
) {
    var screenOption by remember {
        mutableStateOf(HOME)
    }

    val itemsBottomBar = listOf(
        BottomBarItemData(
            selected = screenOption == HOME,
            onClick = { screenOption = HOME },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") },
            label = { Text(text = "Home") },
            alwaysShowLabel = screenOption == HOME
        ),

        BottomBarItemData(
            selected = screenOption == LIST_PRODUCTS,
            onClick = { screenOption = LIST_PRODUCTS },
            icon = { Icon(imageVector = Icons.Filled.List, contentDescription = "List products") },
            label = { Text(text = "List") },
            alwaysShowLabel = screenOption == LIST_PRODUCTS
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(screenOption) {
                                HOME -> "Pantry manager"
                                LIST_PRODUCTS -> "Inventory"
                                ADD_PRODUCT -> "Add product"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }, enabled = false) {
                    }
                },

                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        if (userData?.profilePictureUrl != null) {
                            AsyncImage(
                                model = userData.profilePictureUrl,
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            )
        },

        floatingActionButton = {
             if (screenOption != ADD_PRODUCT) {
                 ExtendedFloatingActionButton(
                     onClick = { screenOption = ADD_PRODUCT }
                 ) {
                     Icon(
                         imageVector = Icons.Filled.PhotoCamera,
                         contentDescription = "Scan product"
                     )

                     Spacer(modifier = Modifier.width(5.dp))

                     Text(
                         text = "Scan"
                     )
                 }
             }
        },

        bottomBar = {
            BottomAppBar {
                itemsBottomBar.forEach { item ->
                    NavigationBarItem(
                        selected = item.selected,
                        onClick = item.onClick,
                        icon = item.icon,
                        enabled = item.enable,
                        label = item.label,
                        alwaysShowLabel = item.alwaysShowLabel
                    )
                }
            }
        }
    ) { paddingValues ->
        // Nell'Activity o componente superiore

        when(screenOption) {
            HOME -> HomeScreen(
                paddingValues = paddingValues
            )
            ADD_PRODUCT -> UseScanScreen(
                userData = userData,
                paddingValues = paddingValues,
                changeMenu = { screenOption = HOME }
            )
            LIST_PRODUCTS -> ListScreen(
                //viewModel = viewModel,
                userData = userData,
                paddingValues = paddingValues
            )
        }
    }
}