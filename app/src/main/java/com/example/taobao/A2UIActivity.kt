package com.example.taobao

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taobao.a2ui.A2UIDataSource
import com.example.taobao.a2ui.A2UIJsonDataSource
import com.example.taobao.a2ui.A2UIStreamHandler
import com.example.taobao.a2ui.A2UISurfaceManager

class A2UIActivity : AppCompatActivity(), A2UIStreamHandler.A2UIStreamListener {

    private lateinit var surfaceManager: A2UISurfaceManager
    private lateinit var streamHandler: A2UIStreamHandler
    private lateinit var jsonDataSource: A2UIJsonDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a2ui_main)
        
        val container = findViewById<FrameLayout>(R.id.a2ui_container)
        
        surfaceManager = A2UISurfaceManager(this)
        jsonDataSource = A2UIJsonDataSource(this)
        
        // 创建带有事件回调的surface
        surfaceManager.createSurface("taobao_main", container) { actionId, componentId, data ->
            handleUserEvent(actionId, componentId, data)
        }
        
        streamHandler = A2UIStreamHandler(surfaceManager, this)
        
        // 使用JSON文件数据源
        val messages = jsonDataSource.loadMessagesFromAsset("a2ui_main_page.json")
        if (messages.isNotEmpty()) {
            streamHandler.processMessages(messages)
        } else {
            // 如果JSON加载失败，回退到原来的动态数据源
            val fallbackDataSource = A2UIDataSource()
            val fallbackMessages = fallbackDataSource.getMainPageMessages()
            streamHandler.processMessages(fallbackMessages)
        }
    }

    override fun onMessageReceived(message: com.example.taobao.a2ui.A2UIMessage) {
        surfaceManager.getSurface("taobao_main")?.render()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onComplete() {
        Toast.makeText(this, "UI渲染完成", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleUserEvent(actionId: String, componentId: String, data: Map<String, Any>?) {
        when (actionId) {
            "navigate_scan" -> {
                Toast.makeText(this, "扫描功能", Toast.LENGTH_SHORT).show()
            }
            "navigate_search" -> {
                Toast.makeText(this, "搜索功能", Toast.LENGTH_SHORT).show()
            }
            "show_messages" -> {
                Toast.makeText(this, "消息功能", Toast.LENGTH_SHORT).show()
            }
            "navigate_category" -> {
                Toast.makeText(this, "分类导航", Toast.LENGTH_SHORT).show()
            }
            "navigate_product" -> {
                Toast.makeText(this, "商品详情", Toast.LENGTH_SHORT).show()
            }
            "increment_price" -> {
                // 增加价格示例
                surfaceManager.incrementValue("taobao_main", "/products.0.price")
            }
            else -> {
                Toast.makeText(this, "未知操作: $actionId", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
