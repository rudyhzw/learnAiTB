package com.example.taobao

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taobao.a2ui.A2UIDataSource
import com.example.taobao.a2ui.A2UIJsonDataSource
import com.example.taobao.a2ui.A2UIMessageParser
import com.example.taobao.a2ui.A2UIStreamHandler
import com.example.taobao.a2ui.A2UISurfaceManager

class ProductActivity : AppCompatActivity(), A2UIStreamHandler.A2UIStreamListener {

    private lateinit var surfaceManager: A2UISurfaceManager
    private lateinit var streamHandler: A2UIStreamHandler
    private lateinit var dataSource: A2UIDataSource
    private lateinit var jsonDataSource: A2UIJsonDataSource
    private var useA2UI = false
    private var useJsonData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        useA2UI = intent.getBooleanExtra("use_a2ui", false)
        useJsonData = intent.getBooleanExtra("use_json", false)
        
        if (useA2UI) {
            if (useJsonData) {
                setupA2UIWithJsonView()
            } else {
                setupA2UIView()
            }
        } else {
            setupTraditionalView()
        }
    }

    private fun setupA2UIWithJsonView() {
        setContentView(R.layout.activity_a2ui_product)
        
        val container = findViewById<android.widget.FrameLayout>(R.id.a2ui_product_container)
        
        surfaceManager = A2UISurfaceManager(this)
        jsonDataSource = A2UIJsonDataSource(this)
        
        val productId = intent.getIntExtra("product_id", 1)
        val surfaceId = "product_detail_$productId"
        
        surfaceManager.createSurface(surfaceId, container)
        
        streamHandler = A2UIStreamHandler(surfaceManager, this)
        
        val messages = jsonDataSource.loadMessagesFromAsset("a2ui_product_detail.json")
        
        Toast.makeText(this, "正在加载商品详情...", Toast.LENGTH_SHORT).show()
        
        jsonDataSource.simulateStreamWithDelay(
            messages = messages,
            onMessage = { message ->
                runOnUiThread {
                    streamHandler.processMessage(A2UIMessageParser().toJson(message))
                    surfaceManager.getSurface(surfaceId)?.render()
                }
            },
            onComplete = {
                runOnUiThread {
                    Toast.makeText(this, "商品详情加载完成 (JSON数据)", Toast.LENGTH_SHORT).show()
                }
            },
            initialDelayMs = 300,
            delayBetweenMessagesMs = 200
        )
    }

    private fun setupA2UIView() {
        setContentView(R.layout.activity_a2ui_product)
        
        val container = findViewById<android.widget.FrameLayout>(R.id.a2ui_product_container)
        
        surfaceManager = A2UISurfaceManager(this)
        dataSource = A2UIDataSource()
        jsonDataSource = A2UIJsonDataSource(this)
        
        val productId = intent.getIntExtra("product_id", 1)
        val surfaceId = "product_detail_$productId"
        
        surfaceManager.createSurface(surfaceId, container)
        
        streamHandler = A2UIStreamHandler(surfaceManager, this)
        
        val messages = dataSource.getProductPageMessages(productId)
        
        Toast.makeText(this, "正在加载商品详情...", Toast.LENGTH_SHORT).show()
        
        jsonDataSource.simulateStreamWithDelay(
            messages = messages,
            onMessage = { message ->
                runOnUiThread {
                    streamHandler.processMessage(A2UIMessageParser().toJson(message))
                    surfaceManager.getSurface(surfaceId)?.render()
                }
            },
            onComplete = {
                runOnUiThread {
                    Toast.makeText(this, "商品详情加载完成", Toast.LENGTH_SHORT).show()
                }
            },
            initialDelayMs = 300,
            delayBetweenMessagesMs = 200
        )
    }

    override fun onMessageReceived(message: com.example.taobao.a2ui.A2UIMessage) {
        val productId = intent.getIntExtra("product_id", 1)
        surfaceManager.getSurface("product_detail_$productId")?.render()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onComplete() {
        Toast.makeText(this, "商品详情A2UI渲染完成", Toast.LENGTH_SHORT).show()
    }

    private fun setupTraditionalView() {
        setContentView(R.layout.activity_product)

        val backBtn = findViewById<ImageView>(R.id.iv_back)
        backBtn.setOnClickListener {
            finish()
        }

        val addCartBtn = findViewById<Button>(R.id.btn_add_cart)
        addCartBtn.setOnClickListener {
            Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show()
        }

        val buyBtn = findViewById<Button>(R.id.btn_buy)
        buyBtn.setOnClickListener {
            Toast.makeText(this, "即将跳转到订单页面", Toast.LENGTH_SHORT).show()
        }
    }
}
