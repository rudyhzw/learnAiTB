package com.example.taobao.a2ui

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

class A2UIJsonDataSource(private val context: Context) {

    companion object {
        private const val TAG = "A2UIJsonDataSource"
    }

    fun loadMessagesFromAsset(fileName: String): List<A2UIMessage> {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val content = reader.readText()
            reader.close()

            val messages = mutableListOf<A2UIMessage>()
            val jsonObjects = content.split("---").filter { it.isNotBlank() }

            val parser = A2UIMessageParser()
            
            for (jsonStr in jsonObjects) {
                try {
                    val trimmed = jsonStr.trim()
                    if (trimmed.isNotBlank()) {
                        parser.parseMessage(trimmed)?.let { messages.add(it) }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse JSON object: ${e.message}")
                }
            }

            messages
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load asset: ${e.message}")
            emptyList()
        }
    }

    fun simulateStream(messages: List<A2UIMessage>, delayMs: Long = 500): List<List<A2UIMessage>> {
        val batches = mutableListOf<List<A2UIMessage>>()
        
        for (message in messages) {
            batches.add(listOf(message))
        }
        
        return batches
    }

    fun simulateStreamWithDelay(
        messages: List<A2UIMessage>,
        onMessage: (A2UIMessage) -> Unit,
        onComplete: () -> Unit,
        initialDelayMs: Long = 300,
        delayBetweenMessagesMs: Long = 200
    ) {
        Thread {
            Thread.sleep(initialDelayMs)
            
            messages.forEach { message ->
                onMessage(message)
                Thread.sleep(delayBetweenMessagesMs)
            }
            
            onComplete()
        }.start()
    }
}
