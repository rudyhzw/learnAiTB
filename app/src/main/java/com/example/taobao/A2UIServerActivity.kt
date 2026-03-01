package com.example.taobao

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taobao.a2ui.A2UIJsonDataSource
import com.example.taobao.a2ui.A2UIMessage
import com.example.taobao.a2ui.A2UIStreamHandler
import com.example.taobao.a2ui.A2UISurfaceManager
import com.example.taobao.a2ui.A2UIWebSocketClient

class A2UIServerActivity : AppCompatActivity(), A2UIStreamHandler.A2UIStreamListener {

    companion object {
        private const val TAG = "A2UIServer"
    }

    private lateinit var surfaceManager: A2UISurfaceManager
    private lateinit var streamHandler: A2UIStreamHandler
    private lateinit var jsonDataSource: A2UIJsonDataSource
    private lateinit var webSocketClient: A2UIWebSocketClient
    private var isFromNetwork = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a2ui_main)
        
        val container = findViewById<FrameLayout>(R.id.a2ui_container)
        
        surfaceManager = A2UISurfaceManager(this)
        jsonDataSource = A2UIJsonDataSource(this)
        
        surfaceManager.createSurface("taobao_main", container) { actionId, componentId, data ->
            handleUserEvent(actionId, componentId, data)
        }
        
        streamHandler = A2UIStreamHandler(surfaceManager, this)
        
        initWebSocket()
    }

    private fun initWebSocket() {
        Log.d(TAG, "开始初始化 WebSocket...")
        webSocketClient = A2UIWebSocketClient()
        
        webSocketClient.setOnConnectedListener {
            runOnUiThread {
                Log.d(TAG, "WebSocket 已连接")
                Toast.makeText(this, "已连接到服务器", Toast.LENGTH_SHORT).show()
                requestMainPage()
            }
        }
        
        webSocketClient.setOnDisconnectedListener {
            runOnUiThread {
                Log.d(TAG, "WebSocket 已断开")
            }
        }
        
        webSocketClient.setOnErrorListener { error ->
            runOnUiThread {
                Log.e(TAG, "WebSocket 错误: $error")
                Toast.makeText(this, "连接失败: $error", Toast.LENGTH_SHORT).show()
                loadLocalData()
            }
        }
        
        webSocketClient.setOnMessageListener { message ->
            isFromNetwork = true
            Log.d(TAG, "收到服务端消息: ${message.version}")
            runOnUiThread {
                streamHandler.processMessage(message)
                val surface = surfaceManager.getSurface("taobao_main")
                Log.d(TAG, "Re-rendering surface: $surface")
                surface?.render()
            }
        }
        
        Log.d(TAG, "调用 connect()")
        webSocketClient.connect()
    }

    private fun requestMainPage() {
        if (webSocketClient.isConnected()) {
            isFromNetwork = true
            webSocketClient.requestMainPage()
        } else {
            Log.w(TAG, "WebSocket 未连接，使用本地数据")
            loadLocalData()
        }
    }

    private fun loadLocalData() {
        isFromNetwork = false
        val messages = jsonDataSource.loadMessagesFromAsset("a2ui_main_page.json")
        if (messages.isNotEmpty()) {
            streamHandler.processMessages(messages)
        } else {
            Toast.makeText(this, "加载本地数据", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMessageReceived(message: A2UIMessage) {
        surfaceManager.getSurface("taobao_main")?.render()
    }

    override fun onError(error: String) {
        Log.e(TAG, "Error: $error")
    }

    override fun onComplete() {
        runOnUiThread {
            if (isFromNetwork) {
                Toast.makeText(this, "数据来自服务端", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "数据来自本地", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun handleUserEvent(actionId: String, componentId: String, data: Map<String, Any>?) {
        when (actionId) {
            "navigate_scan" -> Toast.makeText(this, "扫描功能", Toast.LENGTH_SHORT).show()
            "navigate_search" -> Toast.makeText(this, "搜索功能", Toast.LENGTH_SHORT).show()
            "show_messages" -> Toast.makeText(this, "消息功能", Toast.LENGTH_SHORT).show()
            "navigate_category" -> Toast.makeText(this, "分类导航", Toast.LENGTH_SHORT).show()
            "navigate_product" -> Toast.makeText(this, "商品详情", Toast.LENGTH_SHORT).show()
            "increment_price" -> {
                if (webSocketClient.isConnected()) {
                    webSocketClient.sendIncrementPrice()
                    Toast.makeText(this, "价格已+1 (服务端)", Toast.LENGTH_SHORT).show()
                } else {
                    surfaceManager.incrementValue("taobao_main", "/products.0.price")
                    Toast.makeText(this, "价格已+1 (本地)", Toast.LENGTH_SHORT).show()
                }
            }
            "add_product" -> {
                if (webSocketClient.isConnected()) {
                    webSocketClient.sendEvent("add_product")
                    Toast.makeText(this, "正在添加商品...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "未连接服务器", Toast.LENGTH_SHORT).show()
                }
            }
            else -> Toast.makeText(this, "未知操作: $actionId", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!webSocketClient.isConnected()) {
            requestMainPage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.disconnect()
    }
}
