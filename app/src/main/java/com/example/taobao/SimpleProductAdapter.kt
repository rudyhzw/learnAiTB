package com.example.taobao

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

class SimpleProductAdapter(
    private val products: MutableList<Map<String, Any>>,
    private val onItemClick: (Int, Int, Map<String, Any>, TextView) -> Unit,
    private var scrollView: NestedScrollView? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyclerView: RecyclerView? = null

    fun setRecyclerView(rv: RecyclerView) {
        recyclerView = rv
    }

    fun setScrollView(scroll: NestedScrollView?) {
        scrollView = scroll
    }

    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_LARGE = 1
        const val TYPE_HORIZONTAL = 2
        const val TYPE_TEXT = 3
        const val TYPE_CONTENT = 4
    }

    private val imageColors = listOf(
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
        "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
    )

    private val tagTexts = listOf("热卖", "新品", "特惠", "爆款", "推荐")

    override fun getItemViewType(position: Int): Int {
        val product = products[position]
        val itemType = product["itemType"]?.toString()?.toIntOrNull() ?: 0
        if (itemType == 3) return TYPE_CONTENT
        
        val hasImage = product["hasImage"]?.toString()?.toBooleanStrictOrNull() ?: true
        if (!hasImage) return TYPE_TEXT
        
        return when (itemType) {
            1 -> TYPE_LARGE
            2 -> TYPE_HORIZONTAL
            else -> TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CONTENT -> {
                val view = inflater.inflate(R.layout.item_product_content, parent, false)
                ContentViewHolder(view)
            }
            TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.item_product_text, parent, false)
                TextViewHolder(view)
            }
            TYPE_LARGE -> {
                val view = inflater.inflate(R.layout.item_product_type2, parent, false)
                LargeViewHolder(view)
            }
            TYPE_HORIZONTAL -> {
                val view = inflater.inflate(R.layout.item_product_type3, parent, false)
                HorizontalViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_product_type1, parent, false)
                NormalViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = products[position]
        when (holder) {
            is NormalViewHolder -> holder.bind(product, position)
            is LargeViewHolder -> holder.bind(product, position)
            is HorizontalViewHolder -> holder.bind(product, position)
            is TextViewHolder -> holder.bind(product, position)
            is ContentViewHolder -> holder.bind(product, position)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updatePrice(position: Int, newPrice: String) {
        @Suppress("UNCHECKED_CAST")
        (products[position] as MutableMap<String, Any>)["price"] = newPrice
        notifyItemChanged(position)
    }

    fun stopCurrentTypewriter() {
        if (products.isNotEmpty()) {
            val lastPosition = products.size - 1
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(lastPosition)
            if (viewHolder is ContentViewHolder) {
                viewHolder.stopTypewriterAndAddEllipsis()
            }
        }
    }

    inner class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewImage: View = itemView.findViewById(R.id.view_image)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvCoupon: TextView = itemView.findViewById(R.id.tv_coupon)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvOriginalPrice: TextView = itemView.findViewById(R.id.tv_original_price)
        private val tvSales: TextView = itemView.findViewById(R.id.tv_sales)
        private val tvLocation: TextView = itemView.findViewById(R.id.tv_location)

        fun bind(product: Map<String, Any>, position: Int) {
            viewImage.setBackgroundColor(Color.parseColor(imageColors[position % imageColors.size]))
            bindCommon(product)
            itemView.setOnClickListener {
                val productId = product["id"]?.toString()?.toIntOrNull() ?: (position + 1)
                onItemClick(position, productId, product, tvPrice)
            }
        }

        protected fun bindCommon(product: Map<String, Any>) {
            tvTitle.text = product["title"]?.toString() ?: "商品标题"

            val coupon = product["coupon"]?.toString() ?: ""
            if (coupon.isNotEmpty()) {
                tvCoupon.text = coupon
                tvCoupon.visibility = View.VISIBLE
            } else {
                tvCoupon.visibility = View.GONE
            }

            val price = product["price"]?.toString() ?: "0.00"
            tvPrice.text = "¥$price"

            val originalPrice = product["originalPrice"]?.toString() ?: "0.00"
            tvOriginalPrice.text = " ¥$originalPrice"

            val sales = product["sales"]?.toString() ?: "0"
            tvSales.text = "$sales+人付款"

            val location = product["location"]?.toString() ?: ""
            if (location.isNotEmpty()) {
                tvLocation.text = location
                tvLocation.visibility = View.VISIBLE
            } else {
                tvLocation.visibility = View.GONE
            }
        }
    }

    inner class LargeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewImage: View = itemView.findViewById(R.id.view_image)
        private val tvTag: TextView = itemView.findViewById(R.id.tv_tag)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvCoupon: TextView = itemView.findViewById(R.id.tv_coupon)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvOriginalPrice: TextView = itemView.findViewById(R.id.tv_original_price)
        private val tvSales: TextView = itemView.findViewById(R.id.tv_sales)
        private val tvLocation: TextView = itemView.findViewById(R.id.tv_location)

        fun bind(product: Map<String, Any>, position: Int) {
            val colorIndex = (position + 2) % imageColors.size
            viewImage.setBackgroundColor(Color.parseColor(imageColors[colorIndex]))

            val tagText = product["tag"]?.toString() ?: tagTexts[position % tagTexts.size]
            tvTag.text = tagText
            tvTag.visibility = View.VISIBLE

            tvTitle.text = product["title"]?.toString() ?: "商品标题"

            val coupon = product["coupon"]?.toString() ?: ""
            if (coupon.isNotEmpty()) {
                tvCoupon.text = coupon
                tvCoupon.visibility = View.VISIBLE
            } else {
                tvCoupon.visibility = View.GONE
            }

            val price = product["price"]?.toString() ?: "0.00"
            tvPrice.text = "¥$price"

            val originalPrice = product["originalPrice"]?.toString() ?: "0.00"
            tvOriginalPrice.text = " ¥$originalPrice"

            val sales = product["sales"]?.toString() ?: "0"
            tvSales.text = "$sales+人付款"

            val location = product["location"]?.toString() ?: ""
            if (location.isNotEmpty()) {
                tvLocation.text = location
                tvLocation.visibility = View.VISIBLE
            } else {
                tvLocation.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val productId = product["id"]?.toString()?.toIntOrNull() ?: (position + 1)
                onItemClick(position, productId, product, tvPrice)
            }
        }
    }

    inner class HorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewImage: View = itemView.findViewById(R.id.view_image)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvCoupon: TextView = itemView.findViewById(R.id.tv_coupon)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvOriginalPrice: TextView = itemView.findViewById(R.id.tv_original_price)
        private val tvSales: TextView = itemView.findViewById(R.id.tv_sales)
        private val tvLocation: TextView = itemView.findViewById(R.id.tv_location)

        fun bind(product: Map<String, Any>, position: Int) {
            val colorIndex = (position + 5) % imageColors.size
            viewImage.setBackgroundColor(Color.parseColor(imageColors[colorIndex]))

            tvTitle.text = product["title"]?.toString() ?: "商品标题"

            val coupon = product["coupon"]?.toString() ?: ""
            if (coupon.isNotEmpty()) {
                tvCoupon.text = coupon
                tvCoupon.visibility = View.VISIBLE
            } else {
                tvCoupon.visibility = View.GONE
            }

            val price = product["price"]?.toString() ?: "0.00"
            tvPrice.text = "¥$price"

            val originalPrice = product["originalPrice"]?.toString() ?: "0.00"
            tvOriginalPrice.text = " ¥$originalPrice"

            val sales = product["sales"]?.toString() ?: "0"
            tvSales.text = "$sales+人付款"

            val location = product["location"]?.toString() ?: ""
            if (location.isNotEmpty()) {
                tvLocation.text = location
                tvLocation.visibility = View.VISIBLE
            } else {
                tvLocation.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val productId = product["id"]?.toString()?.toIntOrNull() ?: (position + 1)
                onItemClick(position, productId, product, tvPrice)
            }
        }
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvCoupon: TextView = itemView.findViewById(R.id.tv_coupon)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvOriginalPrice: TextView = itemView.findViewById(R.id.tv_original_price)
        private val tvSales: TextView = itemView.findViewById(R.id.tv_sales)
        private val tvLocation: TextView = itemView.findViewById(R.id.tv_location)

        fun bind(product: Map<String, Any>, position: Int) {
            itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))

            tvTitle.text = product["title"]?.toString() ?: "商品标题"

            val coupon = product["coupon"]?.toString() ?: ""
            if (coupon.isNotEmpty()) {
                tvCoupon.text = coupon
                tvCoupon.visibility = View.VISIBLE
            } else {
                tvCoupon.visibility = View.GONE
            }

            val price = product["price"]?.toString() ?: "0.00"
            tvPrice.text = "¥$price"

            val originalPrice = product["originalPrice"]?.toString() ?: "0.00"
            tvOriginalPrice.text = " ¥$originalPrice"

            val sales = product["sales"]?.toString() ?: "0"
            tvSales.text = "$sales+人付款"

            val location = product["location"]?.toString() ?: ""
            if (location.isNotEmpty()) {
                tvLocation.text = location
                tvLocation.visibility = View.VISIBLE
            } else {
                tvLocation.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val productId = product["id"]?.toString()?.toIntOrNull() ?: (position + 1)
                onItemClick(position, productId, product, tvPrice)
            }
        }
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvContent: TextView = itemView.findViewById(R.id.tv_content)
        private var typewriterHandler: android.os.Handler? = null
        private var typewriterRunnable: Runnable? = null
        private var isTypewriterRunning = false
        private var currentPosition: Int = -1

        fun bind(product: Map<String, Any>, position: Int) {
            val isLastItem = position == products.size - 1
            val content = product["content"]?.toString() ?: "这是一段很长的商品描述内容..."
            @Suppress("UNCHECKED_CAST")
            val typedText = (product as? MutableMap<String, Any>)?.get("typedText")?.toString() ?: ""
            val isComplete = product["typewriterComplete"]?.toString()?.toBooleanStrictOrNull() ?: false
            
            if (isLastItem) {
                if (currentPosition != position || !isTypewriterRunning) {
                    stopTypewriter()
                    
                    tvContent.text = typedText
                    
                    if (!isComplete) {
                        val startIndex = typedText.length
                        currentPosition = position
                        isTypewriterRunning = true
                        
                        typewriterHandler = android.os.Handler(android.os.Looper.getMainLooper())
                        val charArray = content.toCharArray()
                        var index = startIndex

                        typewriterRunnable = object : Runnable {
                            override fun run() {
                                if (isTypewriterRunning && index < charArray.size) {
                                    tvContent.text = tvContent.text.toString() + charArray[index]
                                    @Suppress("UNCHECKED_CAST")
                                    (products[position] as? MutableMap<String, Any>)?.put("typedText", tvContent.text.toString())
                                    index++
                                    scrollView?.post {
                                        scrollView?.fullScroll(View.FOCUS_DOWN)
                                    }
                                    if (index >= charArray.size) {
                                        @Suppress("UNCHECKED_CAST")
                                        (products[position] as? MutableMap<String, Any>)?.put("typewriterComplete", true)
                                        isTypewriterRunning = false
                                    } else {
                                        typewriterHandler?.postDelayed(this, 30)
                                    }
                                }
                            }
                        }
                        typewriterHandler?.post(typewriterRunnable!!)
                    }
                }
            } else {
                stopTypewriter()
            }

            itemView.setOnClickListener {
                val productId = product["id"]?.toString()?.toIntOrNull() ?: (position + 1)
                onItemClick(position, productId, product, tvContent)
            }
        }

        fun stopTypewriter() {
            isTypewriterRunning = false
            typewriterHandler?.removeCallbacksAndMessages(null)
        }

        fun stopTypewriterAndAddEllipsis() {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return
            
            val product = products[position]
            val isComplete = product["typewriterComplete"]?.toString()?.toBooleanStrictOrNull() ?: false
            
            if (!isComplete) {
                val currentText = tvContent.text.toString()
                if (currentText.isNotEmpty() && !currentText.endsWith("...")) {
                    tvContent.text = currentText + "..."
                    @Suppress("UNCHECKED_CAST")
                    (products[position] as? MutableMap<String, Any>)?.put("typedText", currentText + "...")
                }
            }
            
            isTypewriterRunning = false
            typewriterHandler?.removeCallbacksAndMessages(null)
        }
    }
}
