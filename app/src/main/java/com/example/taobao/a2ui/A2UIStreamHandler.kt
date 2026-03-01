package com.example.taobao.a2ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class A2UIStreamHandler(
    private val surfaceManager: A2UISurfaceManager,
    private val listener: A2UIStreamListener
) {
    private val parser = A2UIMessageParser()
    private val handler: Handler? = try { Handler(Looper.getMainLooper()) } catch (e: Exception) { null }
    
    companion object {
        private const val TAG = "A2UIStreamHandler"
    }

    interface A2UIStreamListener {
        fun onMessageReceived(message: A2UIMessage)
        fun onError(error: String)
        fun onComplete()
    }

    fun processStream(inputStream: InputStream) {
        Thread {
            try {
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                
                while (reader.readLine().also { line = it } != null) {
                    line?.let {
                        if (it.isNotBlank()) {
                            processMessage(it)
                        }
                    }
                }
                
                handler?.post {
                    listener.onComplete()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Stream processing error: ${e.message}")
                handler?.post {
                    listener.onError(e.message ?: "Unknown error")
                }
            }
        }.start()
    }

    fun processMessage(jsonString: String) {
        val message = parser.parseMessage(jsonString)
        message?.let {
            handleMessage(it)
            handler?.post {
                listener.onMessageReceived(it)
            }
        }
    }

    fun processMessage(message: A2UIMessage) {
        handleMessage(message)
        handler?.post {
            listener.onMessageReceived(message)
        }
    }

    fun processMessages(messages: List<A2UIMessage>) {
        messages.forEach { message ->
            handleMessage(message)
        }
        handler?.post {
            listener.onMessageReceived(messages.last())
        }
    }

    private fun handleMessage(message: A2UIMessage) {
        Log.d(TAG, "Handling message: ${if (message.createSurface != null) "createSurface" else if (message.updateComponents != null) "updateComponents" else if (message.updateDataModel != null) "updateDataModel" else "unknown"}")
        
        message.createSurface?.let { createSurface ->
            Log.d(TAG, "Creating surface: ${createSurface.surfaceId}")
        }

        message.updateDataModel?.let { dataModel ->
            surfaceManager.updateDataModel(dataModel.surfaceId, dataModel.path, dataModel.data)
            Log.d(TAG, "Updating data model at ${dataModel.path} on surface: ${dataModel.surfaceId}, data: ${dataModel.data}")
        }

        message.updateComponents?.let { update ->
            val surface = surfaceManager.getSurface(update.surfaceId)
            surface?.updateComponents(update.components)
            Log.d(TAG, "Updating ${update.components.size} components on surface: ${update.surfaceId}")
        }

        message.deleteSurface?.let { delete ->
            surfaceManager.deleteSurface(delete.surfaceId)
            Log.d(TAG, "Deleting surface: ${delete.surfaceId}")
        }
    }
}

class A2UIEventHandler(
    private val surfaceManager: A2UISurfaceManager
) {
    companion object {
        private const val TAG = "A2UIEventHandler"
    }

    data class UserEvent(
        val surfaceId: String,
        val componentId: String,
        val eventType: String,
        val eventData: Map<String, Any>? = null
    )

    fun sendEvent(event: UserEvent) {
        Log.d(TAG, "User event: ${event.eventType} on ${event.componentId} in surface ${event.surfaceId}")
    }

    fun createEvent(
        surfaceId: String,
        componentId: String,
        eventType: String,
        eventData: Map<String, Any>? = null
    ): UserEvent {
        return UserEvent(surfaceId, componentId, eventType, eventData)
    }
}
