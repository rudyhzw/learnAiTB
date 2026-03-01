package com.example.taobao.a2ui

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class A2UIWebSocketClient(
    private val serverUrl: String = "http://192.168.1.19:5000"
) {
    companion object {
        private const val TAG = "A2UIWebSocket"
    }

    private var mSocket: Socket? = null
    private val objectMapper = ObjectMapper()
    
    private var onMessageReceived: ((A2UIMessage) -> Unit)? = null
    private var onConnected: (() -> Unit)? = null
    private var onDisconnected: (() -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    fun setOnMessageListener(callback: (A2UIMessage) -> Unit) {
        onMessageReceived = callback
    }

    fun setOnConnectedListener(callback: () -> Unit) {
        onConnected = callback
    }

    fun setOnDisconnectedListener(callback: () -> Unit) {
        onDisconnected = callback
    }

    fun setOnErrorListener(callback: (String) -> Unit) {
        onError = callback
    }

    fun connect() {
        try {
            Log.d(TAG, "Creating socket options...")
            val opts = IO.Options()
            opts.reconnection = true
            opts.reconnectionAttempts = 5
            opts.reconnectionDelay = 1000

            Log.d(TAG, "Creating IO.socket with url: $serverUrl")
            mSocket = IO.socket(serverUrl, opts)
            
            if (mSocket == null) {
                Log.e(TAG, "Socket is null after IO.socket()")
                onError?.invoke("Socket creation failed")
                return
            }

            Log.d(TAG, "Setting up event listeners...")
            mSocket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "WebSocket connected")
                onConnected?.invoke()
            }

            mSocket?.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "WebSocket disconnected")
                onDisconnected?.invoke()
            }

            mSocket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = args.joinToString(", ") { it.toString() }
                Log.e(TAG, "Connection error: $error")
                onError?.invoke(error)
            }

            mSocket?.on("connected") { args ->
                Log.d(TAG, "Server acknowledged")
            }

            mSocket?.on("a2ui_message") { args ->
                try {
                    val data = args[0]
                    val jsonStr = if (data is JSONObject) {
                        data.toString()
                    } else {
                        data.toString()
                    }
                    
                    val message = objectMapper.readValue(jsonStr, A2UIMessage::class.java)
                    Log.d(TAG, "Received message: ${message.version}")
                    onMessageReceived?.invoke(message)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse message: ${e.message}")
                }
            }

            Log.d(TAG, "Calling connect()...")
            mSocket?.connect()
            Log.d(TAG, "connect() called, waiting for connection...")
            Log.d(TAG, "Connecting to $serverUrl")
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Failed to connect: ${e.message}")
            onError?.invoke(e.message ?: "Connection failed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect: ${e.message}")
            onError?.invoke(e.message ?: "Connection failed")
        }
    }

    fun disconnect() {
        mSocket?.disconnect()
        mSocket?.off()
        mSocket = null
    }

    fun isConnected(): Boolean = mSocket?.connected() == true

    fun requestMainPage() {
        if (isConnected()) {
            Log.d(TAG, "Requesting main page...")
            mSocket?.emit("request_main_page")
        } else {
            Log.e(TAG, "Socket not connected")
        }
    }

    fun sendIncrementPrice() {
        if (isConnected()) {
            Log.d(TAG, "Sending increment_price...")
            mSocket?.emit("increment_price")
        } else {
            Log.e(TAG, "Socket not connected")
        }
    }

    fun sendEvent(eventName: String, data: Map<String, Any>? = null) {
        if (isConnected()) {
            Log.d(TAG, "Sending event: $eventName")
            if (data != null) {
                val jsonData = objectMapper.writeValueAsString(data)
                mSocket?.emit(eventName, jsonData)
            } else {
                mSocket?.emit(eventName)
            }
        } else {
            Log.e(TAG, "Socket not connected")
        }
    }
}
