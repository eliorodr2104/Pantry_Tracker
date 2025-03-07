package com.project.pantrytracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.companion.StorageColor
import com.project.pantrytracker.items.companion.StorageIcons
import com.project.pantrytracker.items.products.Storage
import com.project.pantrytracker.items.uiItems.ColorItem
import com.project.pantrytracker.items.uiItems.PercentItem
import com.project.pantrytracker.ui.theme.blueIcon
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductsViewModel
import com.project.pantrytracker.viewmodels.StorageViewModel
import kotlinx.coroutines.launch

@Composable
fun PanoramicInfo(
    viewModelStorage     : StorageViewModel,
    viewModelProducts    : ProductsViewModel,
    viewModelCategories  : CategoriesViewModel,
    navController        : NavController,
    userData             : UserData?,

    changeStorageSelected: (Storage) -> Unit
) {
    var showBottomSheet by remember {
        mutableStateOf(false)

    }

    val products        by viewModelProducts.products.collectAsState(initial = emptyList())
    val productsUsed    by viewModelProducts.productsUsed.collectAsState(initial = emptyList())
    val productsExpired by viewModelProducts.productsExpired.collectAsState(initial = emptyList())

    val categories      by viewModelCategories.categories.collectAsState()

    LaunchedEffect(products.isEmpty()) {
        viewModelProducts.getAllProducts(
            userData
        )
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(
                all = 15.dp
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        DashboardProducts(
            productsSize = products.size,
            productsUsedSize = productsUsed.size,
            productsExpiredSize = productsExpired.size
        )

        Spacer(
            modifier = Modifier
                .height(15.dp)
        )

        DashboardStorage(
            storageViewModel = viewModelStorage,
            productsViewModel = viewModelProducts,
            changeValueBottomSheet = {
                showBottomSheet = it
            },

            navController = navController,
            changeStorageSelected = changeStorageSelected
        )

    }

    if (showBottomSheet) {
        BottomSheetAddStorage(
            userData = userData,
            changeValueBottomSheet = {
                showBottomSheet = it
            },

            storageViewModel = viewModelStorage
        )
    }
}

@Composable
private fun DashboardStorage(
    storageViewModel      : StorageViewModel,
    productsViewModel     : ProductsViewModel,
    changeValueBottomSheet: (Boolean) -> Unit,
    changeStorageSelected : (Storage) -> Unit,
    navController         : NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start

    ) {

        Text(
            text = "STORAGES",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.outline,

            modifier = Modifier
                .padding(
                    start = 10.dp
                )

        )

        Spacer(
            modifier = Modifier
                .height(
                    height = 5.dp
                )
        )

        StorageList(
            storageViewModel       = storageViewModel,
            productsViewModel      = productsViewModel,
            changeValueBottomSheet = changeValueBottomSheet,
            navController          = navController,
            changeStorageSelected  = changeStorageSelected
        )

    }
}

@Composable
private fun StorageList(
    storageViewModel      : StorageViewModel,
    productsViewModel     : ProductsViewModel,
    changeValueBottomSheet: (Boolean) -> Unit,
    changeStorageSelected : (Storage) -> Unit,
    navController         : NavController
) {
    val storage by storageViewModel.storage.collectAsState(emptyList())
    val storageProducts by productsViewModel.productsStorage.collectAsState(mutableMapOf())

    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = ShapeDefaults.Medium
            )

            .background(
                color = MaterialTheme.colorScheme.secondaryContainer
            ),

        //contentPadding = PaddingValues(
        //    all = 15.dp
        //),

        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        item {
            val isExpanded = expandedStates["miao"] ?: false
            val rotationAngle by animateFloatAsState(
                targetValue = if (isExpanded) 90f else 0f,
                animationSpec = tween(durationMillis = 200),
                label = "Rotation Animation"
            )

            val storageAll = Storage(
                name = "All",
                icon = StorageIcons.BoxRound,
                color = ColorItem(
                    red   = StorageColor.LavenderPurple.red,
                    green = StorageColor.LavenderPurple.green,
                    blue  = StorageColor.LavenderPurple.blue,
                    alpha = StorageColor.LavenderPurple.alpha
                )
            )

            StorageItem(
                storage = storageAll,
                modifierIcon = Modifier.rotate(rotationAngle),
                clickable = {
                    expandedStates["miao"] = !(expandedStates["miao"] ?: false)
                }
            )

            if (storage.isNotEmpty()) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(
                        alpha = 0.3f
                    ),
                    modifier = Modifier
                        .padding(
                            start = 10.dp,
                            end = 10.dp
                        )
                )
            }

            AnimatedVisibility (
                visible = expandedStates["miao"] == true
            ) {
                val productsItems = storageProducts[storageAll]?.products ?: emptyList()

                LazyColumn {
                    items(
                        items = productsItems

                    ) { item ->
                        Text(text = "$item - Dettaglio 1")

                    }
                }
            }
        }

        itemsIndexed(
            items = storage,
            key = { _, item ->
                item.name
            }

        ) { index, item ->

            StorageItem(
                storage = item,
                modifierIcon = Modifier,
                clickable = {
                    expandedStates[item.name] = !(expandedStates[item.name] ?: false)
                    //changeStorageSelected(item)
                    //navController.navigate("items_menu")
                }
            )

            if (index != storage.lastIndex) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary.copy(
                        alpha = 0.3f
                    ),
                    modifier = Modifier
                        .padding(
                            start = 10.dp,
                            end = 10.dp
                        )
                )
            }

        }

        item {

            AddStorageButton(
                changeValueBottomSheet = changeValueBottomSheet
            )

        }

    }

}

@Composable
private fun AddStorageButton(
    changeValueBottomSheet: (Boolean) -> Unit
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.tertiary.copy(
            alpha = 0.3f
        ),
        modifier = Modifier
            .padding(
                start = 10.dp,
                end = 10.dp
            )
    )

    Spacer(
        modifier = Modifier
            .height(5.dp)
    )

    Row(
        modifier = Modifier
            .clickable {
                changeValueBottomSheet(true)
            }
            .fillMaxWidth()
            .padding(
                all = 10.dp
            ),

        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {

        Spacer(
            modifier = Modifier
                .width(7.dp)
        )

        Icon(
            imageVector = Icons.Filled.AddCircleOutline,
            contentDescription = "All",

            tint = blueIcon
        )

        Text(
            text = "Add Storage",
            color = MaterialTheme.colorScheme.tertiary
        )

    }

    Spacer(
        modifier = Modifier
            .height(5.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetAddStorage(
    userData: UserData?,
    changeValueBottomSheet: (Boolean) -> Unit,
    storageViewModel: StorageViewModel
) {

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var colorSelected by remember {
        mutableStateOf(StorageColor.EmeraldGreen)
    }

    var iconSelected by remember {
        mutableIntStateOf(StorageIcons.BathtubRound)
    }

    var nameStorage by remember {
        mutableStateOf("")
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(),

        onDismissRequest = {
            changeValueBottomSheet(false)
        },
        sheetState = sheetState,

        containerColor = MaterialTheme.colorScheme.secondaryContainer

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = 15.dp
                ),

            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,

                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Text(
                    text = "Cancel",
                    color = blueIcon,

                    modifier = Modifier
                        .clickable {
                            changeValueBottomSheet(false)

                        }
                )

                Text(
                    text = "New Storage",
                    color = MaterialTheme.colorScheme.outline,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Save",
                    color = if (nameStorage != "") blueIcon else MaterialTheme.colorScheme.outline,

                    modifier = Modifier.clickable {
                        if (nameStorage != "") {

                            storageViewModel.addStorage(
                                storage = Storage(
                                    name = nameStorage,
                                    icon = iconSelected,
                                    color = ColorItem(
                                        red   = colorSelected.red,
                                        green = colorSelected.green,
                                        blue  = colorSelected.blue,
                                        alpha = colorSelected.alpha
                                    )
                                ),
                                user = userData
                            )

                            scope.launch {
                                sheetState.hide()

                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    changeValueBottomSheet(false)
                                }
                            }
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .clip(
                        shape = ShapeDefaults.Medium
                    )
                    .background(
                        color = colorSelected
                    )
                    .fillMaxWidth(0.2f)
                    .aspectRatio(1f)

            ) {

                Icon(
                    painter = painterResource(iconSelected),
                    contentDescription = "Icon New Storage",

                    tint = MaterialTheme.colorScheme.onSurface,

                    modifier = Modifier
                        .align(
                            alignment = Alignment.Center
                        )
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f)
                    //.size(
                    //    size = 35.dp
                    //)
                )

            }

            TextField(
                value = nameStorage,
                onValueChange = {
                    nameStorage = it
                },

                shape = ShapeDefaults.Medium,

                modifier = Modifier
                    .fillMaxWidth(),

                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    focusedContainerColor   = MaterialTheme.colorScheme.tertiaryContainer,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor   = Color.Transparent


                ),

                singleLine = true,

                label = {
                    Text(
                        text = "Name Storage",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(
                    alpha = 0.3f
                ),
            )

            StorageColorList(
                colorSelected = colorSelected,
                changeColorValue = {
                    colorSelected = it
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary.copy(
                    alpha = 0.3f
                ),
            )


            StorageIconList(
                iconSelected = iconSelected,
                changeIconValue = {
                    iconSelected = it
                }
            )
        }
    }
}

@Composable
private fun StorageIconList(
    iconSelected: Int,
    changeIconValue: (Int) -> Unit
) {

    Column(

        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.Start

    ) {
        Text(
            text = "Icon",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 27.sp
            )
        )

        LazyVerticalGrid (
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),

            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = ShapeDefaults.Medium
                )
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                .padding(10.dp)
        ) {

            items(
                items = StorageIcons.listStorageIcons

            ) { item ->

                StorageIconItem(
                    icon = item,
                    isSelected = iconSelected == item,
                    changeIconValue = changeIconValue
                )

            }

        }
    }

}


@Composable
private fun StorageIconItem(
    icon            : Int,
    isSelected      : Boolean,
    changeIconValue: (Int) -> Unit
) {

    Box(
        modifier = Modifier
            .clip(
                shape = CircleShape
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                if (!isSelected) changeIconValue(icon)
            }

            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                ) else Color.Transparent,
                shape = CircleShape
            )
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = "Icon Selected",

            tint = MaterialTheme.colorScheme.onSurface,

            modifier = Modifier
                .align(
                    alignment = Alignment.Center
                )
        )
    }

}

@Composable
private fun StorageColorList(
    colorSelected   : Color,
    changeColorValue: (Color) -> Unit
) {

    Column(

        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.Start

    ) {
        Text(
            text = "Color",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 27.sp
            )
        )

        LazyVerticalGrid (
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),

            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = ShapeDefaults.Medium
                )
                .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                .padding(10.dp)
        ) {

            items(
                items = StorageColor.ColorList

            ) { item ->

                StorageColorItem(
                    color = item,
                    isSelected = colorSelected == item,
                    changeColorValue = changeColorValue

                )

            }

        }
    }
}

@Composable
private fun StorageColorItem(
    color           : Color,
    isSelected      : Boolean,
    changeColorValue: (Color) -> Unit
) {

    Box(
        modifier = Modifier
            .clip(
                shape = CircleShape
            )
            .background(
                color = color
            )
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                if (!isSelected) changeColorValue(color)
            }
    ) {

        if (isSelected) {

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.DarkGray.copy(alpha = 0.6f))
            )

            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Color Selected",

                tint = MaterialTheme.colorScheme.onSurface,

                modifier = Modifier
                    .align(
                        alignment = Alignment.Center
                    )
            )

        }

    }

}

@Composable
private fun StorageItem(
    storage              : Storage,
    clickable            : () -> Unit,
    modifierIcon          : Modifier
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                clickable()
            }
            .padding(
                all = 10.dp
            ),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        shape = ShapeDefaults.Medium
                    )

                    .background(
                        color = Color(
                            red = storage.color.red,
                            green = storage.color.green,
                            blue = storage.color.blue,
                            alpha = storage.color.alpha
                        )
                    )

                    .fillMaxWidth(0.17f)
                    .aspectRatio(1f)

            ) {

                Icon(
                    painter = painterResource(storage.icon),
                    contentDescription = storage.name,
                    modifier            = Modifier
                        .align(
                            alignment = Alignment.Center
                        )
                        .fillMaxWidth(0.6f)
                        .aspectRatio(1f)
                )

            }

            Text(
                text = storage.name,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            modifier = modifierIcon,
            contentDescription = "Open Storage"
        )

    }

}


@Composable
private fun DashboardProducts(
    productsSize: Int,
    productsUsedSize: Int,
    productsExpiredSize: Int
) {
    var isOpen by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isOpen) 90f else 0f,
        animationSpec = tween(
            durationMillis = 200
        ),
        label = "Rotation Animation"
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isOpen = !isOpen }
                .clip(
                    shape = ShapeDefaults.Medium
                )
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                .animateContentSize(
                    animationSpec = tween(durationMillis = 100)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = 15.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Product",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    AnimatedVisibility(
                        visible = !isOpen,
                        enter = scaleIn(
                            animationSpec = tween(500)
                        ),

                        exit = scaleOut(
                            animationSpec = tween(500)
                        )
                    ) {
                        Text(
                            text = productsSize.toString(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Toggle",
                        modifier = Modifier.rotate(rotationAngle),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            AnimatedVisibility(
                visible = isOpen,
                enter = fadeIn(
                    animationSpec = tween(500)
                ) + expandVertically(),

                exit = fadeOut(
                    animationSpec = tween(500)
                ) + shrinkVertically()
            ) {
                PercentValues(
                    totalProducts = productsSize,
                    usedProducts = productsUsedSize,
                    expiredProducts = productsExpiredSize
                )
            }
        }
    }
}

@Composable
private fun PercentValues(
    totalProducts  : Int,
    usedProducts   : Int,
    expiredProducts: Int
) {
    val listPercent = listOf(
        PercentItem("Total", totalProducts),
        PercentItem("Used", usedProducts),
        PercentItem("Expired", expiredProducts),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 15.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listPercent.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            ) {
                Text(
                    text = item.name,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                ConcentricCircularProgressCharts(
                    progress = item.value.toFloat(),
                    maxProgress = totalProducts.toFloat(),
                    backgroundColor = MaterialTheme.colorScheme.outline,
                    color = blueIcon
                )

                Text(
                    text = "${item.value} pz",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ConcentricCircularProgressCharts(
    progress       : Float,
    maxProgress    : Float,
    color          : Color,
    backgroundColor: Color
) {
    val strokeWidth: Dp = 3.dp

    val animatedProgress by remember { mutableFloatStateOf(progress) }
    val animatedValue by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = ""
    )

    Canvas(
        modifier = Modifier
            //.size(20.dp)
            .fillMaxWidth(0.6f)
            .aspectRatio(1f)
    ) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2
        val centerOffset = Offset(size.width / 2, size.height / 2)

        val currentStroke = strokeWidth.toPx()
        val startAngle = -90f
        val sweepAngle = 360f * (animatedValue / maxProgress)

        drawCircle(
            color = backgroundColor,
            radius = radius - currentStroke / 2,
            center = centerOffset,
            style = Stroke(width = currentStroke)
        )

        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(centerOffset.x - radius + currentStroke / 2, centerOffset.y - radius + currentStroke / 2),
            size = Size(canvasSize - currentStroke, canvasSize - currentStroke),
            style = Stroke(width = currentStroke)
        )
    }
}

@Composable
private fun PercentItemBox(
    item: PercentItem
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = 5.dp
        ),
        horizontalAlignment = Alignment.Start

    ) {
        Text(
            text = item.name,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (item.name == "Total") "${item.value}" else "${item.value}%",
            color = MaterialTheme.colorScheme.outline
        )
    }

}

private fun calcPercent(a: Int, b: Int): Int {
    return if (a == 0 || b == 0) {
        0

    } else {
        (a / b) * 100

    }
}