package com.example.taobao

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.taobao.a2ui.A2UIDataSource

class SimpleA2UIActivity : AppCompatActivity() {

    private companion object {
        private const val TAG = "TaobaoUI"
    }

    private var products = mutableListOf<Map<String, Any>>()
    private var banners = listOf<Map<String, Any>>()
    private var categories = listOf<Map<String, Any>>()
    private lateinit var dataSource: A2UIDataSource

    private lateinit var bannerPager: ViewPager2
    private lateinit var bannerIndicator: LinearLayout
    private lateinit var categoryContainer: LinearLayout
    private lateinit var productsRecycler: androidx.recyclerview.widget.RecyclerView
    private lateinit var productAdapter: SimpleProductAdapter
    private lateinit var btnAddProducts: Button
    private lateinit var btnClearProducts: Button
    private lateinit var nestedScrollView: androidx.core.widget.NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_a2ui)
        
        initViews()
        loadHeaderData()
    }

    private fun initViews() {
        bannerPager = findViewById(R.id.banner_pager)
        bannerIndicator = findViewById(R.id.banner_indicator)
        categoryContainer = findViewById(R.id.category_container)
        productsRecycler = findViewById(R.id.products_recycler)
        btnAddProducts = findViewById(R.id.btn_add_products)
        btnClearProducts = findViewById(R.id.btn_clear_products)
        nestedScrollView = findViewById(R.id.scroll_view)

        productsRecycler.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        productAdapter = SimpleProductAdapter(products, { position, productId, product, priceTextView ->
            onProductClick(position, productId, product, priceTextView)
        }, nestedScrollView)
        productAdapter.setRecyclerView(productsRecycler)
        productsRecycler.adapter = productAdapter

        btnAddProducts.setOnClickListener {
            addMoreProducts()
        }

        btnClearProducts.setOnClickListener {
            clearProducts()
        }
    }

    private fun loadHeaderData() {
        try {
            dataSource = A2UIDataSource()
            val messages = dataSource.getMainPageMessages()
            
            val dataModelMsg = messages.find { it.updateDataModel != null }
            val data = dataModelMsg?.updateDataModel?.data
            
            if (data is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val dataMap = data as Map<String, Any>
                banners = dataMap["banners"] as? List<Map<String, Any>> ?: emptyList()
                categories = dataMap["categories"] as? List<Map<String, Any>> ?: emptyList()
            }
            
            renderBanners()
            renderCategories()
            renderProducts()
            
        } catch (e: Exception) {
            Toast.makeText(this, "错误: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProductsData() {
        try {
            val dataSource = A2UIDataSource()
            val messages = dataSource.getMainPageMessages()
            
            val dataModelMsg = messages.find { it.updateDataModel != null }
            val data = dataModelMsg?.updateDataModel?.data
            
            if (data is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val dataMap = data as Map<String, Any>
                val loadedProducts = (dataMap["products"] as? List<*>)?.filterIsInstance<Map<*, *>>()?.map { it as Map<String, Any> }?.toMutableList() ?: mutableListOf()
                
                products.clear()
                products.addAll(loadedProducts)
            }
            
            renderProducts()
            Toast.makeText(this, "加载了${products.size}个商品", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Toast.makeText(this, "错误: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addMoreProducts() {
        productAdapter.stopCurrentTypewriter()
        
        val textCount = products.count { it["itemType"]?.toString()?.toIntOrNull() == 3 }
        val otherCount = products.size - textCount
        
        val productTitles = listOf(
            "【好评如潮】纯棉T恤女短袖 宽松百搭",
            "【限时特惠】无线蓝牙耳机 降噪运动",
            "【爆款】北欧风简约客厅吊灯 创意",
            "【新品上市】男士休闲西裤 商务直筒",
            "【旗舰店】SK-II神仙水精华液 护肤",
            "【618预售】iPhone15手机壳 透明防摔",
            "【出口品质】乳胶枕头 护颈枕 泰国进口",
            "【热销10万】纯棉四件套 床上用品",
            "【断码清仓】运动鞋男跑步鞋 透气",
            "【品牌特卖】戴森吹风机 家用静音"
        )
        val shopNames = listOf(
            "优品汇旗舰店", "潮牌服饰专营", "数码精品屋", "家居生活馆",
            "美妆护肤坊", "运动户外店", "食品超市", "图书音像店"
        )
        
        val random = kotlin.random.Random
        val newProduct: MutableMap<String, Any> = if (textCount >= otherCount * 2) {
            val randomType = listOf(0, 1, 2).random()
            mutableMapOf(
                "id" to products.size + 1,
                "itemType" to randomType,
                "hasImage" to true,
                "title" to productTitles[products.size % productTitles.size],
                "price" to String.format("%.2f", 29.9 + random.nextDouble() * 570),
                "originalPrice" to String.format("%.2f", 50.0 + random.nextDouble() * 850),
                "sales" to (100 + random.nextInt(49900)),
                "shop" to shopNames[products.size % shopNames.size],
                "location" to listOf("杭州", "上海", "广州", "深圳", "北京", "成都")[products.size % 6],
                "coupon" to if (products.size % 4 == 0) "满100减10" else "",
                "tag" to listOf("热卖", "新品", "特惠", "爆款", "推荐")[products.size % 5]
            )
        } else {
            mutableMapOf(
                "id" to products.size + 1,
                "itemType" to 3,
                "hasImage" to false,
                "content" to "这是一段非常详细的商品描述内容，包含了产品的所有信息和使用说明。用户可以在这里了解到商品的详细信息，包括规格、材质，功能特点以及使用方法等。这段文字需要足够长来展示纯文本内容样式的效果，总共约200字左右。商品质量保证正品，支持7天无理由退换货，全国联保，品质值得信赖。购买后可享受专属客服服务，有任何问题随时联系咨询。"
            )
        }
        
        products.add(newProduct)
        productAdapter.notifyItemInserted(products.size - 1)
        productsRecycler.scrollToPosition(products.size - 1)

        Toast.makeText(this, "已添加1条商品，当前共${products.size}条", Toast.LENGTH_SHORT).show()
    }

    private fun clearProducts() {
        productAdapter.stopCurrentTypewriter()
        products.clear()
        productAdapter.notifyDataSetChanged()
        Toast.makeText(this, "已清空所有商品", Toast.LENGTH_SHORT).show()
    }

    private fun renderBanners() {
        if (banners.isEmpty()) return

        bannerPager.adapter = BannerAdapter(banners)
        
        bannerIndicator.removeAllViews()
        banners.forEachIndexed { index, _ ->
            val indicator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    marginStart = 4.dpToPx()
                    marginEnd = 4.dpToPx()
                }
                setBackgroundColor(
                    if (index == 0) android.graphics.Color.parseColor("#FF6600")
                    else android.graphics.Color.parseColor("#CCCCCC")
                )
            }
            bannerIndicator.addView(indicator)
        }

        bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until bannerIndicator.childCount) {
                    bannerIndicator.getChildAt(i).setBackgroundColor(
                        if (i == position) android.graphics.Color.parseColor("#FF6600")
                        else android.graphics.Color.parseColor("#CCCCCC")
                    )
                }
            }
        })
    }

    private fun renderCategories() {
        categories.forEach { category ->
            val catItem = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    0, 
                    LinearLayout.LayoutParams.WRAP_CONTENT, 
                    1f
                )
            }

            catItem.addView(TextView(this).apply {
                text = category["icon"]?.toString() ?: "📦"
                textSize = 28f
            })

            catItem.addView(TextView(this).apply {
                text = category["name"]?.toString() ?: ""
                setTextColor(android.graphics.Color.parseColor("#333333"))
                textSize = 12f
                setPadding(0, 8.dpToPx(), 0, 0)
            })

            categoryContainer.addView(catItem)
        }
    }

    private fun renderProducts() {
        productAdapter.notifyDataSetChanged()
    }

    private fun onProductClick(position: Int, productId: Int, product: Map<String, Any>, priceTextView: TextView) {
//        val intent = Intent(this, ProductActivity::class.java).apply {
//            putExtra("product_id", productId)
//            putExtra("use_a2ui", true)
//        }
//        startActivity(intent)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
