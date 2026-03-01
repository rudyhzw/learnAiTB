package com.example.taobao.a2ui

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class A2UINetworkDataSource(private val context: Context) {

    companion object {
        private const val TAG = "A2UINetworkDataSource"
        private const val BASE_URL = "http://192.168.1.19:5000"
    }

    private val objectMapper = ObjectMapper()

    suspend fun fetchMainPageMessages(): List<A2UIMessage> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/main_page")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val messages = objectMapper.readValue(response, Array<A2UIMessage>::class.java)
                messages.toList()
            } else {
                Log.e(TAG, "HTTP Error: $responseCode")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchProducts(): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/products")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val node = objectMapper.readTree(response)
                val products = node.get("products")
                if (products != null && products.isArray) {
                    products.map { objectMapper.convertValue(it, Map::class.java) as Map<String, Any> }
                } else {
                    emptyList()
                }
            } else {
                Log.e(TAG, "HTTP Error: $responseCode")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchProductDetail(productId: Int): Map<String, Any>? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/api/products/$productId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val node = objectMapper.readTree(response)
                val product = node.get("product")
                if (product != null) {
                    objectMapper.convertValue(product, Map::class.java) as? Map<String, Any>
                } else {
                    null
                }
            } else {
                Log.e(TAG, "HTTP Error: $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error: ${e.message}")
            null
        }
    }
}
