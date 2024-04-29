package com.project.pantrytracker.barcodeApi


import com.project.pantrytracker.DataItems.ProductApi
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess

class BarcodeApi {
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 6000
        }
    }
    private val URL_API = "https://world.openfoodfacts.org/api/v2/product/"

    suspend fun searchBarcode(barcode: String): ProductApi {
        return try {
            val response: HttpResponse = client.request("$URL_API$barcode.json") {
                method = HttpMethod.Get
            }

            println("Forse crasha:")
            println(response.status)

            if (response.status.value != 404) {
                convertJsonIntoObject(
                    json = response.validateResponse(),
                    barcode = barcode
                )

            } else {
                ProductApi(
                    barcode = barcode
                )
            }

        } catch (exception: HttpRequestTimeoutException) {
            ProductApi(
                exception = exception
            )
        } catch (exception: ConnectTimeoutException) {
            ProductApi(
                exception = exception
            )
        } catch (exception: SocketTimeoutException) {
            ProductApi(
                exception = exception
            )
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun convertJsonIntoObject(
        json: String?,
        barcode: String
    ): ProductApi {
        return try {
            val moshi = Moshi.Builder().build()

            val jsonData = json?.let { moshi.adapter<Map<String, Any>>().fromJson(it) }

            val secondJson = jsonData?.get("product") as Map<*, *>

            val nameProduct = secondJson["product_name"]
            val quantityProduct = secondJson["quantity"]
            val brandsProduct = secondJson["brands"].toString().split(",")
            //val categoriesProduct = secondJson["categories"].toString().split(",")

            ProductApi(
                barcode = barcode,
                name = nameProduct.toString(),
                quantity = quantityProduct.toString(),
                brands = brandsProduct
            )
        } catch (exception: JsonDataException) {
            ProductApi(
                exception = exception
            )
        }
    }

    private suspend fun HttpResponse.validateResponse(): String? {
        if (!status.isSuccess()) {
            return null
        }
        return bodyAsText()
    }
}