package com.project.pantrytracker.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.pantrytracker.DataItems.InfoProductsHome

@Preview
@Composable
fun HomeScreen(
    paddingValues: PaddingValues = PaddingValues(15.dp)
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 3.dp,
                    shape = RoundedCornerShape(
                        topStart = 35.dp,
                        topEnd = 35.dp
                    ),
                    color = Color.Black
                )
        ) {
            item(
                key = 0
            ) {
                Text(
                    text = "Welcome to your inventory!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(
                            top = 20.dp,
                            bottom = 10.dp,
                            start = 20.dp
                        )
                )

                MosaicItemsInfo()
            }
        }
    }
}

@Composable
fun MosaicItemsInfo() {
    val testInfo = listOf(
        InfoProductsHome(
            valueShow = 5,
            labelText = "Low Stock Alerts"
        ),

        InfoProductsHome(
            valueShow = 16,
            labelText = "Expired Item List"
        ),

        InfoProductsHome(
            valueShow = 1,
            labelText = "New Products Added"
        ),

        InfoProductsHome(
            valueShow = 5,
            labelText = "Many Stock Products"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for (index in 0 .. 1) {
                MosaicItem(
                    modifier = if (index %2 == 0) Modifier.size(150.dp) else Modifier
                        .fillMaxWidth()
                        .size(150.dp),
                    infoProductsHome = testInfo[index]
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for (index in 2..< testInfo.size) {
                MosaicItem(
                    modifier = if (index %2 == 0)
                        Modifier
                            .fillMaxWidth(0.58f)
                            .size(150.dp)
                    else Modifier
                        .size(150.dp)
                        .weight(1f),
                    infoProductsHome = testInfo[index]
                )
            }
        }
    }
}

@Composable
private fun MosaicItem(
    modifier: Modifier,
    infoProductsHome: InfoProductsHome
) {
    Box(
        modifier = modifier
            .border(
                width = 5.dp,
                shape = RoundedCornerShape(
                    size = 25.dp
                ),
                color = Color.Black
            )
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = infoProductsHome.valueShow.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = infoProductsHome.labelText
            )
        }
    }
}