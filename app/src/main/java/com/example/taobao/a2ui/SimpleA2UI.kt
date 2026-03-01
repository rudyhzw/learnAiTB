package com.example.taobao.a2ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class SimpleA2UIRenderer(
    private val context: Context,
    private val surface: SimpleA2UISurface,
    private val surfaceManager: SimpleA2UISurfaceManager
) {
    companion object {
        private const val TAG = "SimpleA2UIRenderer"
    }
    
    fun render() {
        val data = surfaceManager.getDataModel(surface.surfaceId) ?: run {
            Log.e(TAG, "No data model for surface: ${surface.surfaceId}")
            return
        }
        
        val products = data["products"] as? List<*>
        
        Log.d(TAG, "Rendering ${products?.size ?: 0} products")
        
        val container = surface.getContainer()
        container.removeAllViews()
        
        products?.forEachIndexed { index, product ->
            if (product is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val productMap = product as Map<String, Any>
                
                val card = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.WHITE)
                    val padding = 16.dpToPx()
                    setPadding(padding, padding, padding, padding)
                    val margin = 8.dpToPx()
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(margin, margin, margin, margin)
                    layoutParams = params
                }
                
                val title = productMap["title"]?.toString() ?: "商品$index"
                val price = productMap["price"]?.toString() ?: "0.00"
                
                card.addView(TextView(context).apply {
                    text = title
                    textSize = 14f
                    setTextColor(Color.BLACK)
                    maxLines = 2
                })
                
                card.addView(TextView(context).apply {
                    text = "¥$price"
                    textSize = 18f
                    setTextColor(Color.parseColor("#FF6600"))
                })
                
                container.addView(card)
                
                Log.d(TAG, "Added product card: $title - ¥$price")
            }
        }
    }
    
    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).toInt()
}

class SimpleA2UISurface(
    val surfaceId: String,
    private val context: Context,
    private val container: ViewGroup,
    private val manager: SimpleA2UISurfaceManager
) {
    fun render() {
        SimpleA2UIRenderer(context, this, manager).render()
    }
    
    fun getContainer(): ViewGroup = container
}

class SimpleA2UISurfaceManager(private val context: Context) {
    private val surfaces = mutableMapOf<String, SimpleA2UISurface>()
    private val dataModels = mutableMapOf<String, MutableMap<String, Any>>()
    
    fun createSurface(surfaceId: String, container: ViewGroup): SimpleA2UISurface {
        val surface = SimpleA2UISurface(surfaceId, context, container, this)
        surfaces[surfaceId] = surface
        dataModels[surfaceId] = mutableMapOf()
        return surface
    }
    
    fun getSurface(surfaceId: String): SimpleA2UISurface? = surfaces[surfaceId]
    
    fun getDataModel(surfaceId: String): Map<String, Any>? = dataModels[surfaceId]
    
    fun updateDataModel(surfaceId: String, data: Map<String, Any>) {
        dataModels[surfaceId]?.clear()
        dataModels[surfaceId]?.putAll(data)
        surfaces[surfaceId]?.render()
    }
}
