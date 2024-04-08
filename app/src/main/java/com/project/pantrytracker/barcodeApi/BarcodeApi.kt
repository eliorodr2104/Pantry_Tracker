package com.project.pantrytracker.barcodeApi


import com.project.pantrytracker.DataItems.Product
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess

class BarcodeApi {
    private val client = HttpClient(CIO)
    private val URL_API = "https://world.openfoodfacts.org/api/v2/product/"

    suspend fun searchBarcode(barcode: String): Product? {
        return try {
            val response: HttpResponse = client.request("$URL_API$barcode.json") {
                method = HttpMethod.Get
            }

            response.validateResponse()?.let { responseBody ->
                convertJsonIntoObject(responseBody)
            }

        } catch (exception: Exception) {
            null
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun convertJsonIntoObject(json: String): Product? {
        return try {
            val moshi = Moshi.Builder().build()

            val jsonData = moshi.adapter<Map<String, Any>>().fromJson(json)

            val barcodeProduct = jsonData?.get("code")

            val secondJson = jsonData?.get("product") as Map<*, *>

            val nameProduct = secondJson["product_name"]
            val quantityProduct = secondJson["quantity"]
            val brandsProduct = secondJson["brands"].toString().split(",")
            //val categoriesProduct = secondJson["categories"].toString().split(",")

            Product(
                barcode = barcodeProduct.toString(),
                name = nameProduct.toString(),
                quantity = quantityProduct.toString(),
                brands = brandsProduct,
                availability = true
            )
        } catch (exception: JsonDataException) {
            null
        }
    }

    private suspend fun HttpResponse.validateResponse(): String? {
        if (!status.isSuccess()) {
            return null
        }
        return bodyAsText()
    }
}