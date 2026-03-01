package com.example.taobao

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.taobao.a2ui.A2UIDataSource
import com.example.taobao.a2ui.A2UIJsonDataSource
import com.example.taobao.a2ui.A2UIStreamHandler
import com.example.taobao.a2ui.A2UISurfaceManager
import kotlin.random.Random

class MainActivity : AppCompatActivity(), A2UIStreamHandler.A2UIStreamListener {

    private lateinit var bannerPager: ViewPager2
    private lateinit var bannerIndicator: LinearLayout
    private lateinit var productsRecycler: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var searchEditText: EditText
    private lateinit var scanImage: ImageView
    private lateinit var messageImage: ImageView

    private val bannerList = mutableListOf<Int>()
    private val productList = mutableListOf<Product>()

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

    private fun setupTraditionalView() {
        setContentView(R.layout.activity_main)
        initViews()
        initData()
        setupBanner()
        setupProducts()
        setupClickListeners()
    }

    private fun setupA2UIWithJsonView() {
        setContentView(R.layout.activity_a2ui_main)
        
        val container = findViewById<android.widget.FrameLayout>(R.id.a2ui_container)
        
        surfaceManager = A2UISurfaceManager(this)
        jsonDataSource = A2UIJsonDataSource(this)
        
        surfaceManager.createSurface("taobao_main", container) { actionId, componentId, data ->
            handleA2UIAction(actionId, componentId, data)
        }
        
        streamHandler = A2UIStreamHandler(surfaceManager, this)
        
        val messages = jsonDataSource.loadMessagesFromAsset("a2ui_main_page.json")
        
        android.util.Log.d("MainActivity", "Loaded ${messages.size} messages")
        
        Toast.makeText(this, "正在加载A2UI数据... 共${messages.size}条消息", Toast.LENGTH_SHORT).show()
        
        // Process all messages at once
        messages.forEach { message ->
            streamHandler.processMessage(com.example.taobao.a2ui.A2UIMessageParser().toJson(message))
        }
        
        // Render after all messages processed
        surfaceManager.getSurface("taobao_main")?.render()
        
        Toast.makeText(this, "A2UI渲染完成 (JSON数据)", Toast.LENGTH_SHORT).show()
    }

    private fun setupA2UIView() {
        setContentView(R.layout.activity_a2ui_main)
        
        val container = findViewById<android.widget.FrameLayout>(R.id.a2ui_container)
        
        surfaceManager = A2UISurfaceManager(this)
        dataSource = A2UIDataSource()
        jsonDataSource = A2UIJsonDataSource(this)
        
        surfaceManager.createSurface("taobao_main", container) { actionId, componentId, data ->
            handleA2UIAction(actionId, componentId, data)
        }
        
        streamHandler = A2UIStreamHandler(surfaceManager, this)
        
        val messages = dataSource.getMainPageMessages()
        
        Toast.makeText(this, "正在加载A2UI数据...", Toast.LENGTH_SHORT).show()
        
        jsonDataSource.simulateStreamWithDelay(
            messages = messages,
            onMessage = { message ->
                runOnUiThread {
                    streamHandler.processMessage(com.example.taobao.a2ui.A2UIMessageParser().toJson(message))
                    surfaceManager.getSurface("taobao_main")?.render()
                }
            },
            onComplete = {
                runOnUiThread {
                    Toast.makeText(this, "A2UI渲染完成", Toast.LENGTH_SHORT).show()
                }
            },
            initialDelayMs = 500,
            delayBetweenMessagesMs = 300
        )
    }

    override fun onMessageReceived(message: com.example.taobao.a2ui.A2UIMessage) {
        surfaceManager.getSurface("taobao_main")?.render()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
    }

    override fun onComplete() {
        Toast.makeText(this, "A2UI渲染完成", Toast.LENGTH_SHORT).show()
    }

    private fun handleA2UIAction(actionId: String, componentId: String, data: Map<String, Any>?) {
        when (actionId) {
            "navigate_product" -> {
                val productId = extractProductId(componentId)
                navigateToProduct(productId)
            }
            "increment_price" -> {
                val index = data?.get("index") as? Int ?: 0
                incrementProductPrice(index)
            }
            else -> {
                Toast.makeText(this, "Event: $actionId on $componentId", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractProductId(componentId: String): Int {
        return try {
            if (componentId.contains("product_item")) {
                val index = componentId.replace("product_item", "").toIntOrNull() ?: 0
                index + 1
            } else {
                1
            }
        } catch (e: Exception) {
            1
        }
    }

    private fun incrementProductPrice(index: Int) {
        val path = "/products/$index/price"
        val success = surfaceManager.incrementValue("taobao_main", path)
        if (success) {
            surfaceManager.getSurface("taobao_main")?.reRender()
            Toast.makeText(this, "价格已+1", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "价格更新失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToProduct(productId: Int = 1) {
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra("product_id", productId)
        startActivity(intent)
    }

    private fun initViews() {
        bannerPager = findViewById(R.id.banner_pager)
        bannerIndicator = findViewById(R.id.banner_indicator)
        productsRecycler = findViewById(R.id.products_recycler)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        searchEditText = findViewById(R.id.et_search)
        scanImage = findViewById(R.id.iv_scan)
        messageImage = findViewById(R.id.iv_message)

        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.taobao_orange))
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }
    }

    private fun initData() {
        bannerList.addAll(listOf(
            R.color.taobao_orange,
            R.color.taobao_red,
            R.color.gray_dark,
            R.color.gray
        ))

        for (i in 1..20) {
            productList.add(Product(
                title = "商品标题${i}这是一个很长的商品标题显示测试",
                price = Random.nextDouble(10.0, 500.0),
                sales = Random.nextInt(100, 10000)
            ))
        }
    }

    private fun setupBanner() {
        bannerPager.adapter = BannerAdapter(bannerList)

        for (i in bannerList.indices) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    marginStart = 4.dpToPx()
                    marginEnd = 4.dpToPx()
                }
                setBackgroundResource(if (i == 0) R.drawable.dot_selected else R.drawable.dot_normal)
            }
            bannerIndicator.addView(dot)
        }

        bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
    }

    private fun updateIndicator(position: Int) {
        for (i in 0 until bannerIndicator.childCount) {
            bannerIndicator.getChildAt(i).setBackgroundResource(
                if (i == position) R.drawable.dot_selected else R.drawable.dot_normal
            )
        }
    }

    private fun setupProducts() {
        productsRecycler.layoutManager = GridLayoutManager(this, 2)
        productsRecycler.adapter = ProductAdapter(productList)
    }

    private fun setupClickListeners() {
        scanImage.setOnClickListener { v ->
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                navigateToProduct()
            }.start()
        }

        messageImage.setOnClickListener { v ->
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                Toast.makeText(this, "消息", Toast.LENGTH_SHORT).show()
            }.start()
        }

        searchEditText.setOnClickListener {
            navigateToProduct()
        }

        val categoryContainer = findViewById<LinearLayout>(R.id.category_container)
        for (i in 0 until categoryContainer.childCount) {
            val textView = categoryContainer.getChildAt(i) as TextView
            textView.setOnClickListener { v ->
                v.animate().alpha(0.7f).setDuration(100).withEndAction {
                    v.animate().alpha(1f).setDuration(100).start()
                    navigateToProduct()
                }.start()
            }
        }

        bannerPager.setOnClickListener {
            navigateToProduct()
        }
    }

    private fun navigateToProduct() {
        val intent = Intent(this, ProductActivity::class.java)
        startActivity(intent)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    inner class BannerAdapter(private val banners: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
            return BannerViewHolder(view)
        }

        override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
            holder.bind(banners[position])
        }

        override fun getItemCount(): Int = banners.size

        inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.iv_banner)
            private val textView: TextView = itemView.findViewById(R.id.tv_banner)

            fun bind(color: Int) {
                imageView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, color))
                textView.text = "Banner ${bindingAdapterPosition + 1}"
                itemView.setOnClickListener { v ->
                    v.animate().alpha(0.8f).setDuration(100).withEndAction {
                        v.animate().alpha(1f).setDuration(100).start()
                        navigateToProduct()
                    }.start()
                }
            }
        }
    }

    inner class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int = products.size

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.iv_product)
            private val titleView: TextView = itemView.findViewById(R.id.tv_title)
            private val priceView: TextView = itemView.findViewById(R.id.tv_price)
            private val salesView: TextView = itemView.findViewById(R.id.tv_sales)

            fun bind(product: Product) {
                imageView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, 
                    if (bindingAdapterPosition % 3 == 0) R.color.gray_light 
                    else if (bindingAdapterPosition % 3 == 1) R.color.divider 
                    else R.color.gray))
                titleView.text = product.title
                priceView.text = "¥${String.format("%.2f", product.price)}"
                salesView.text = "${product.sales}+人付款"

                itemView.setOnClickListener { v ->
                    v.animate().alpha(0.8f).scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                        v.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(100).start()
                        navigateToProduct()
                    }.start()
                }
            }
        }
    }

    data class Product(
        val title: String,
        val price: Double,
        val sales: Int
    )
}
