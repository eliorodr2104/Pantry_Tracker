package com.project.pantrytracker.ui

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.pantrytracker.DataItems.BottomBarItemData
import com.project.pantrytracker.DataItems.Product
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.Firebase.addProductDb
import com.project.pantrytracker.enumsData.Screens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MenuScreen(
    userData: UserData? = UserData("", "", "", null),
    signOut: suspend () -> Unit = {  },
    activity: Activity
) {

    var signOutUser by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        key1 = signOutUser
    ) {
        if (signOutUser)
            signOut()
    }

    var screenOption by remember {
        mutableStateOf(Screens.HOME)
    }

    val itemsBottomBar = listOf(
        BottomBarItemData(
            selected = screenOption == Screens.HOME,
            onClick = { screenOption = Screens.HOME },
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") },
            label = { Text(text = "Home") },
            alwaysShowLabel = screenOption == Screens.HOME
        ),

        BottomBarItemData(
            selected = screenOption == Screens.LIST_PRODUCTS,
            onClick = { screenOption = Screens.LIST_PRODUCTS },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "List products") },
            label = { Text(text = "List") },
            alwaysShowLabel = screenOption == Screens.LIST_PRODUCTS
        )
    )

    val windowsClass = calculateWindowSizeClass(activity)
    val showNavigationRail = windowsClass.widthSizeClass != WindowWidthSizeClass.Compact

    var paddingValues: PaddingValues

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when(screenOption) {
                                Screens.HOME -> "Pantry manager"
                                Screens.LIST_PRODUCTS -> "Inventory"
                                Screens.ADD_PRODUCT -> "Add product"
                            },
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(
                                    start = if (showNavigationRail) 80.dp else 0.dp
                                )
                        )
                    },

                    navigationIcon = {

                    },

                    actions = {
                        IconButton(
                            onClick = {
                                signOutUser = true
                            }
                        ) {
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
                if (screenOption != Screens.ADD_PRODUCT && !showNavigationRail) {
                    if (screenOption == Screens.LIST_PRODUCTS) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                addProductDb(
                                    product = Product("test", "test", "test", emptyList(), "", 1),
                                    user = userData
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddShoppingCart,
                                contentDescription = "Add test"
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "Add Test"
                            )
                        }

                    } else {
                        ExtendedFloatingActionButton(
                            onClick = { screenOption = Screens.ADD_PRODUCT }
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


                }
            },

            bottomBar = {
                if (!showNavigationRail) {
                    if (screenOption != Screens.ADD_PRODUCT) {
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
                }
            }
        ) { paddingValuesStandard ->
            // Nell'Activity o componente superiore

            paddingValues = PaddingValues(
                top = paddingValuesStandard.calculateTopPadding(),
                start = if (showNavigationRail) 80.dp else 0.dp,
                bottom = paddingValuesStandard.calculateBottomPadding()
            )

            AnimatedVisibility(
                visible = screenOption == Screens.HOME,
                enter = fadeIn(
                    // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                    initialAlpha = 0.4f
                ),
                exit = fadeOut(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 250)
                )
            ) {
                HomeScreen(
                    paddingValues = paddingValues
                )
            }

            AnimatedVisibility(
                visible = screenOption == Screens.ADD_PRODUCT,
                enter = fadeIn(
                    // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                    initialAlpha = 0.4f
                ),
                exit = fadeOut(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 250)
                )
            ) {
                UseScanScreen(
                    userData = userData,
                    paddingValues = paddingValues,
                    changeMenu = { screenOption = Screens.HOME }
                )
            }

            AnimatedVisibility(
                visible = screenOption == Screens.LIST_PRODUCTS,
                enter = fadeIn(
                    // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                    initialAlpha = 0.4f
                ),
                exit = fadeOut(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 250)
                )
            ) {
                ListScreen(
                    //viewModel = viewModel,
                    userData = userData,
                    paddingValues = paddingValues
                )
            }
        }
    }

    if (showNavigationRail) {
        NavigationSideBar(
            items = itemsBottomBar,
            userData = userData,
            screenOption = screenOption,
            changeScreen = { screenOption = it }
        )
    }
}

@Composable
private fun NavigationSideBar(
    items: List<BottomBarItemData>,
    userData: UserData?,
    screenOption: Screens,
    changeScreen: (Screens) -> Unit
) {
    NavigationRail (
        header = {
            /*
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
             */

            if (screenOption == Screens.LIST_PRODUCTS) {
                FloatingActionButton(
                    onClick = {
                        addProductDb(
                            product = Product("test", "test", "test", emptyList(), "", 1),
                            user = userData
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = "Add test"
                    )
                }

            } else {
                FloatingActionButton(
                    onClick = { changeScreen(Screens.ADD_PRODUCT) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "Scan product"
                    )
                }
            }
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .offset(x = (-1).dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom)
        ) {
            items.forEach { item ->
                NavigationRailItem(
                    selected = item.selected,
                    onClick = item.onClick,
                    icon = item.icon,
                    label = item.label,
                )
            }
        }
    }
}