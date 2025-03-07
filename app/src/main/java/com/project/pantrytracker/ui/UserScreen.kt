package com.project.pantrytracker.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.pantrytracker.Firebase.LoginGoogle.UserData
import com.project.pantrytracker.items.categories.Categories
import com.project.pantrytracker.items.products.Product
import com.project.pantrytracker.viewmodels.CategoriesViewModel
import com.project.pantrytracker.viewmodels.ProductsViewModel

@Composable
fun UserScreen(
    paddingValues: PaddingValues,
    productsViewModel: ProductsViewModel,
    categoriesViewModel: CategoriesViewModel,
    userData: UserData?,
    signOut: suspend () -> Unit
) {

    val products by productsViewModel.products.collectAsState(emptyList())
    val categories by categoriesViewModel.categories.collectAsState(emptyList())

    val categoryProductCounts = products
        .groupingBy { it.category }
        .eachCount()

    val topCategories = categoryProductCounts
        .entries
        .sortedByDescending { it.value }
        .take(3)

    val topCategoryProductCounts = topCategories
        .associate { it.key to it.value }

    val topCategoryIds = topCategoryProductCounts.keys

    val topCategoryNamesWithCounts = categories.filter { category ->
        category.id in topCategoryIds
    }.map { category ->
        category to (topCategoryProductCounts[category.id] ?: 0)
    }

    val progressList = listOf(countTotalProducts(products).toFloat()) + (topCategoryNamesWithCounts.map { it.second.toFloat() })
    val listColorsChart = listOf(Color(0xFF1e4f87), Color(0xFF45217a), Color(0Xff236e55), Color(0Xff664e28))

    var signOutUser by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        key1 = signOutUser
    ) {
        if (signOutUser)
            signOut()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        TopAppBar(
            titleText = "Info",
            subTitleText = "Your profile information"
        ) {
            IconButton(
                onClick = { signOutUser = true }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout account"
                )
            }
        }

        ChartInfoProducts(
            progressList = progressList,
            listColorsChart = listColorsChart,
            topCategoryNamesWithCounts = topCategoryNamesWithCounts
        )

        NamedColumnForm(
            title = "Profile"
        ) {
            ItemForm {
                Text(
                    text = "Full name",
                    style = MaterialTheme.typography.titleMedium
                )

                if (userData != null) {
                    userData.username?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            ItemForm {
                Text(
                    text = "E-mail",
                    style = MaterialTheme.typography.titleMedium
                )

                if (userData != null) {
                    userData.email?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            ItemForm {
                Text(
                    text = "Plan",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Free",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        NamedColumnForm(
            title = "Products"
        ) {
            ItemForm {
                Text(
                    text = "Single products",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${products.size}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            ItemForm {
                Text(
                    text = "Total products",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${countTotalProducts(products)}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            ItemForm {
                Text(
                    text = "Max Products",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "200",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun ChartInfoProducts(
    progressList: List<Float>,
    listColorsChart: List<Color>,
    topCategoryNamesWithCounts: List<Pair<Categories, Int>>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ConcentricCircularProgressCharts(
            progressList = progressList,
            sizes = listOf(100.dp, 80.dp, 60.dp, 40.dp),
            colors = listColorsChart,
            backgroundColor = Color.Gray
        )

        Column(
            modifier = Modifier
                .padding(
                    top = 5.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = listColorsChart.first(),
                            shape = CircleShape
                        )
                        .size(10.dp)
                )

                Text(
                    text = "Total Products",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            topCategoryNamesWithCounts.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = listColorsChart[index + 1],
                                shape = CircleShape
                            )
                            .size(10.dp)
                    )

                    Text(
                        text = item.first.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ConcentricCircularProgressCharts(
    progressList: List<Float>,
    maxProgress: Float = 200f,
    strokeWidth: Dp = 2.dp,
    sizes: List<Dp> = listOf(100.dp, 80.dp, 60.dp, 40.dp),
    colors: List<Color> = listOf(Color.Blue, Color.Green, Color.Yellow, Color.Red),
    backgroundColor: Color = Color.Gray
) {
    val animatedProgressList = progressList.map { progress ->
        animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
            label = ""
        )
    }

    Canvas(modifier = Modifier.size(sizes.first())) {
        val canvasSize = sizes.first().toPx()
        val radius = canvasSize / 2
        val centerOffset = Offset(radius, radius)

        animatedProgressList.forEachIndexed { index, animatedProgress ->
            val size = sizes.getOrElse(index) { sizes.last() }
            val color = colors.getOrElse(index) { colors.last() }
            val currentCanvasSize = size.toPx()
            val currentStroke = strokeWidth.toPx()
            val currentRadius = currentCanvasSize / 2
            val startAngle = -90f
            val sweepAngle = 360f * (animatedProgress.value / maxProgress)

            // Draw background circle
            drawCircle(
                color = backgroundColor,
                radius = currentRadius - currentStroke / 2,
                center = centerOffset,
                style = Stroke(width = currentStroke)
            )

            // Draw progress arc
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerOffset.x - (currentRadius - currentStroke / 2), centerOffset.y - (currentRadius - currentStroke / 2)),
                size = Size(currentCanvasSize - currentStroke, currentCanvasSize - currentStroke),
                style = Stroke(width = currentStroke)
            )
        }
    }
}

private fun countTotalProducts(products: List<Product>): Int {
    var count = 0

    //products.forEach {
    //    count += it.numberOfProducts
    //}

    return count
}