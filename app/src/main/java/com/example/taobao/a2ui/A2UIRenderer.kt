package com.example.taobao.a2ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.math.roundToInt

class A2UIRenderer(
    private val context: Context,
    private val surface: A2UISurface,
    private val surfaceManager: A2UISurfaceManager,
    private val eventCallback: ((actionId: String, componentId: String, data: Map<String, Any>?) -> Unit)? = null
) {
    private val objectMapper = ObjectMapper()
    private val componentViews = mutableMapOf<String, View>()
    private val handler: Handler? = try { Handler(Looper.getMainLooper()) } catch (e: Exception) { null }

    companion object {
        private const val TAG = "A2UIRenderer"
    }

    fun render(component: Component): View {
        val componentType = component.component.keys.firstOrNull() ?: return View(context)
        val props = component.component[componentType] as? Map<String, Any> ?: emptyMap()

        val view = createComponentView(componentType, props, component)
        componentViews[component.id] = view
        
        renderChildren(component, view)
        
        setupEvents(component, view)
        
        return view
    }

    private fun createComponentView(type: String, props: Map<String, Any>, component: Component): View {
        return when (type) {
            A2UIComponentTypes.TEXT -> createTextView(props)
            A2UIComponentTypes.BUTTON -> createButtonView(props)
            A2UIComponentTypes.ROW -> createRowView(props)
            A2UIComponentTypes.COLUMN -> createColumnView(props)
            A2UIComponentTypes.IMAGE -> createImageView(props)
            A2UIComponentTypes.CARD -> createCardView(props)
            A2UIComponentTypes.LIST -> createListView(props)
            A2UIComponentTypes.INPUT, A2UIComponentTypes.TEXT_INPUT -> createInputView(props)
            A2UIComponentTypes.SCROLL_VIEW -> createScrollView(props)
            A2UIComponentTypes.FLAT_LIST -> createFlatListView(props)
            A2UIComponentTypes.GRID -> createGridView(props)
            A2UIComponentTypes.DIVIDER -> createDividerView(props)
            A2UIComponentTypes.SPACER -> createSpacerView(props)
            A2UIComponentTypes.BADGE -> createBadgeView(props)
            A2UIComponentTypes.LOADING -> createLoadingView(props)
            A2UIComponentTypes.AVATAR -> createAvatarView(props)
            A2UIComponentTypes.STACK -> createStackView(props)
            A2UIComponentTypes.WRAP -> createWrapView(props)
            A2UIComponentTypes.PROGRESS, A2UIComponentTypes.PROGRESS_BAR -> createProgressBarView(props)
            A2UIComponentTypes.TOAST -> createToastView(props)
            A2UIComponentTypes.SWITCH -> createSwitchView(props)
            A2UIComponentTypes.CHECK_BOX -> createCheckBoxView(props)
            A2UIComponentTypes.TABS -> createTabsView(props)
            A2UIComponentTypes.CAROUSEL -> createCarouselView(props)
            A2UIComponentTypes.MODAL -> createModalView(props)
            A2UIComponentTypes.BOTTOM_SHEET -> createBottomSheetView(props)
            else -> createDefaultView(type, props)
        }
    }

    private fun createTextView(props: Map<String, Any>): TextView {
        val textView = TextView(context)
        
        resolveValue(props["text"])?.let { textView.text = it.toString() }
        
        props["fontSize"]?.let {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (it as? Number)?.toFloat() ?: 14f)
        }
        
        props["fontWeight"]?.let {
            if (it == true || it == "bold") {
                textView.typeface = Typeface.DEFAULT_BOLD
            }
        }
        
        props["color"]?.let {
            val color = resolveColorValue(it)
            if (color != null) {
                textView.setTextColor(color)
            }
        }
        
        props["textAlign"]?.let {
            val align = it as? String
            when (align) {
                "center" -> textView.gravity = Gravity.CENTER
                "right" -> textView.gravity = Gravity.END
                else -> textView.gravity = Gravity.START
            }
        }
        
        props["maxLines"]?.let {
            textView.maxLines = (it as? Number)?.toInt() ?: Int.MAX_VALUE
        }
        
        props["height"]?.let {
            val height = (it as? Number)?.toInt()
            if (height != null && height > 0) {
                textView.layoutParams = ViewGroup.LayoutParams(
                    textView.layoutParams?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT,
                    height.dpToPx()
                )
            }
        }
        
        return textView
    }

    private fun createButtonView(props: Map<String, Any>): Button {
        val button = Button(context)
        
        resolveValue(props["text"])?.let { button.text = it.toString() }
        
        props["backgroundColor"]?.let {
            val color = resolveColorValue(it)
            if (color != null) {
                button.setBackgroundColor(color)
            }
        }
        
        props["color"]?.let {
            val color = resolveColorValue(it)
            if (color != null) {
                button.setTextColor(color)
            }
        }
        
        props["fontSize"]?.let {
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, (it as? Number)?.toFloat() ?: 14f)
        }
        
        props["padding"]?.let {
            val padding = (it as? Number)?.toInt() ?: 16
            button.setPadding(padding.dpToPx(), padding.dpToPx(), padding.dpToPx(), padding.dpToPx())
        }
        
        props["cornerRadius"]?.let {
            button.background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = (it as? Number)?.toFloat() ?: 8f
                props["backgroundColor"]?.let { c ->
                    val color = resolveColorValue(c)
                    if (color != null) {
                        setColor(color)
                    }
                }
            }
        }
        
        button.isAllCaps = false
        
        val width = props["width"] as? String
        val height = props["height"] as? String
        val widthDp = (props["width"] as? Number)?.toInt()
        val heightDp = (props["height"] as? Number)?.toInt()
        
        button.layoutParams = when {
            widthDp != null && heightDp != null -> ViewGroup.LayoutParams(widthDp.dpToPx(), heightDp.dpToPx())
            width == "match" && heightDp != null -> ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightDp.dpToPx())
            width == "wrap" -> ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            else -> ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        
        return button
    }

    private fun createRowView(props: Map<String, Any>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = resolveGravity(props["align"] as? String)
            
            props["spacing"]?.let {
                val spacing = (it as? Number)?.toInt() ?: 0
                setPadding(spacing.dpToPx(), 0, spacing.dpToPx(), 0)
            }
        }
    }

    private fun createColumnView(props: Map<String, Any>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = resolveGravity(props["align"] as? String)
            
            props["spacing"]?.let {
                val spacing = (it as? Number)?.toInt() ?: 0
                setPadding(0, spacing.dpToPx(), 0, 0)
            }
            
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
            
            props["padding"]?.let {
                val padding = (it as? Number)?.toInt() ?: 0
                setPadding(padding.dpToPx(), padding.dpToPx(), padding.dpToPx(), padding.dpToPx())
            }
            
            props["marginTop"]?.let {
                val margin = (it as? Number)?.toInt() ?: 0
                (layoutParams as? LinearLayout.LayoutParams)?.topMargin = margin.dpToPx()
            }
        }
    }

    private fun createImageView(props: Map<String, Any>): ImageView {
        return ImageView(context).apply {
            props["width"]?.let {
                val width = (it as? Number)?.toInt() ?: ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams = if (width > 0) {
                    ViewGroup.LayoutParams(width.dpToPx(), heightDp(props["height"]))
                } else {
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightDp(props["height"]))
                }
            } ?: run {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    heightDp(props["height"])
                )
            }
            
            val srcValue: Any? = props["src"] ?: props["iconUrl"]
            val imageMap = props["image"]
            if (srcValue == null && imageMap is Map<*, *>) {
                imageMap["url"]
            } else srcValue
            
            val resolvedValue = resolveValue(srcValue)
            Log.d(TAG, "createImageView: props keys=${props.keys}, srcValue=$srcValue, resolvedValue=$resolvedValue")
            
            when (resolvedValue) {
                is String -> {
                    if (resolvedValue.startsWith("http://") || resolvedValue.startsWith("https://")) {
                        loadImageFromUrl(resolvedValue)
                    } else if (resolvedValue.startsWith("#")) {
                        try {
                            setBackgroundColor(Color.parseColor(resolvedValue))
                        } catch (e: Exception) {
                            setBackgroundColor(Color.LTGRAY)
                        }
                    } else {
                        try {
                            setBackgroundColor(Color.parseColor(resolvedValue))
                        } catch (e: Exception) {
                            setBackgroundColor(Color.LTGRAY)
                        }
                    }
                }
                is Number -> {
                    setBackgroundColor(resolvedValue.toInt())
                }
                else -> {
                    setBackgroundColor(Color.LTGRAY)
                }
            }
            
            props["objectFit"]?.let {
                scaleType = when (it as? String) {
                    "cover" -> ImageView.ScaleType.CENTER_CROP
                    "contain" -> ImageView.ScaleType.FIT_CENTER
                    else -> ImageView.ScaleType.FIT_XY
                }
            }
            
            props["cornerRadius"]?.let {
                val radius = (it as? Number)?.toFloat() ?: 0f
                if (radius > 0) {
                    background = android.graphics.drawable.GradientDrawable().apply {
                        cornerRadius = radius
                        setColor(Color.TRANSPARENT)
                    }
                }
            }
        }
    }

    private fun ImageView.loadImageFromUrl(url: String) {
        setBackgroundColor(Color.LTGRAY)
        
        fun updateImage(bitmap: android.graphics.Bitmap?) {
            if (bitmap != null) {
                post {
                    if (bitmap != null) {
                        setImageBitmap(bitmap)
                    }
                }
            }
        }
        
        Thread {
            try {
                val connection = java.net.URL(url).openConnection()
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val inputStream = connection.getInputStream()
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                
                if (bitmap != null) {
                    updateImage(bitmap)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load image: $url, ${e.message}")
            }
        }.start()
    }

    private fun createCardView(props: Map<String, Any>): RelativeLayout {
        return RelativeLayout(context).apply {
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
            
            val padding = props["padding"]?.let { (it as? Number)?.toInt() } ?: 16
            setPadding(padding.dpToPx(), padding.dpToPx(), padding.dpToPx(), padding.dpToPx())
            
            props["cornerRadius"]?.let {
                background = android.graphics.drawable.GradientDrawable().apply {
                    cornerRadius = (it as? Number)?.toFloat() ?: 8f
                    props["backgroundColor"]?.let { c ->
                        val color = resolveColorValue(c)
                        if (color != null) {
                            setColor(color)
                        } else {
                            setColor(Color.WHITE)
                        }
                    } ?: setColor(Color.WHITE)
                }
            }
        }
    }

    private fun createListView(props: Map<String, Any>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
    }

    private fun createInputView(props: Map<String, Any>): EditText {
        return EditText(context).apply {
            resolveValue(props["placeholder"])?.let { hint = it.toString() }
            
            props["value"]?.let {
                val value = resolveValue(it)
                if (value != null) {
                    setText(value.toString())
                }
            }
            
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
            
            val padding = props["padding"]?.let { (it as? Number)?.toInt() } ?: 16
            setPadding(padding.dpToPx(), padding.dpToPx(), padding.dpToPx(), padding.dpToPx())
        }
    }

    private fun createScrollView(props: Map<String, Any>): ScrollView {
        return ScrollView(context).apply {
            props["showsVerticalScrollIndicator"]?.let {
                isVerticalScrollBarEnabled = it as? Boolean ?: true
            }
            
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
        }
    }

    private fun createFlatListView(props: Map<String, Any>): androidx.recyclerview.widget.RecyclerView {
        return androidx.recyclerview.widget.RecyclerView(context).apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            
            props["columns"]?.let {
                val columns = (it as? Number)?.toInt() ?: 1
                if (columns > 1) {
                    layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, columns)
                }
            }
        }
    }

    private fun createGridView(props: Map<String, Any>): GridLayout {
        return GridLayout(context).apply {
            props["columns"]?.let {
                columnCount = (it as? Number)?.toInt() ?: 2
            }
            
            props["spacing"]?.let {
                val spacing = (it as? Number)?.toInt() ?: 8
                setPadding(spacing.dpToPx(), spacing.dpToPx(), spacing.dpToPx(), spacing.dpToPx())
            }
            
            props["padding"]?.let {
                val padding = (it as? Number)?.toInt() ?: 8
                setPadding(padding.dpToPx(), padding.dpToPx(), padding.dpToPx(), padding.dpToPx())
            }
        }
    }

    private fun createDividerView(props: Map<String, Any>): View {
        return View(context).apply {
            setBackgroundColor(resolveColorValue(props["color"]) ?: Color.LTGRAY)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (props["height"]?.let { (it as? Number)?.toInt() } ?: 1).dpToPx()
            )
        }
    }

    private fun createSpacerView(props: Map<String, Any>): View {
        return View(context).apply {
            val width = props["width"]?.let { (it as? Number)?.toInt() } ?: 0
            val height = props["height"]?.let { (it as? Number)?.toInt() } ?: 0
            layoutParams = ViewGroup.LayoutParams(width.dpToPx(), height.dpToPx())
        }
    }

    private fun createBadgeView(props: Map<String, Any>): TextView {
        return TextView(context).apply {
            resolveValue(props["text"])?.let { text = it.toString() }
            setTextColor(resolveColorValue(props["color"]) ?: Color.WHITE)
            textSize = 10f
            setBackgroundColor(resolveColorValue(props["backgroundColor"]) ?: Color.RED)
            gravity = Gravity.CENTER
            
            val padding = 4.dpToPx()
            setPadding(padding, padding / 2, padding, padding / 2)
            
            props["cornerRadius"]?.let {
                val radius = (it as? Number)?.toFloat() ?: 4f
                background = android.graphics.drawable.GradientDrawable().apply {
                    cornerRadius = radius
                    setColor(resolveColorValue(props["backgroundColor"]) ?: Color.RED)
                }
            }
        }
    }

    private fun createLoadingView(props: Map<String, Any>): android.widget.ProgressBar {
        return android.widget.ProgressBar(context).apply {
            props["color"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    indeterminateTintList = android.content.res.ColorStateList.valueOf(color)
                }
            }
        }
    }

    private fun createAvatarView(props: Map<String, Any>): ImageView {
        return ImageView(context).apply {
            val size = props["size"]?.let { (it as? Number)?.toInt() } ?: 48
            layoutParams = ViewGroup.LayoutParams(size.dpToPx(), size.dpToPx())
            
            props["src"]?.let { src ->
                val srcValue = resolveValue(src)
                when (srcValue) {
                    is String -> {
                        if (srcValue.startsWith("http")) {
                            loadImageFromUrl(srcValue)
                        } else {
                            setBackgroundColor(resolveColorValue(srcValue) ?: Color.GRAY)
                        }
                    }
                    else -> setBackgroundColor(resolveColorValue(props["backgroundColor"]) ?: Color.GRAY)
                }
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun createStackView(props: Map<String, Any>): FrameLayout {
        return FrameLayout(context).apply {
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
        }
    }

    private fun createWrapView(props: Map<String, Any>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = resolveGravity(props["align"] as? String)
            
            props["spacing"]?.let {
                val spacing = (it as? Number)?.toInt() ?: 0
                setPadding(spacing.dpToPx(), 0, spacing.dpToPx(), 0)
            }
        }
    }

    private fun createProgressBarView(props: Map<String, Any>): android.widget.ProgressBar {
        return android.widget.ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
            props["progress"]?.let {
                progress = (it as? Number)?.toInt() ?: 0
            }
            props["max"]?.let {
                max = (it as? Number)?.toInt() ?: 100
            }
            props["color"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    indeterminateTintList = android.content.res.ColorStateList.valueOf(color)
                }
            }
        }
    }

    private fun createToastView(props: Map<String, Any>): TextView {
        return TextView(context).apply {
            resolveValue(props["message"])?.let { text = it.toString() }
            setTextColor(resolveColorValue(props["color"]) ?: Color.WHITE)
            textSize = (props["fontSize"] as? Number)?.toFloat() ?: 14f
            setBackgroundColor(resolveColorValue(props["backgroundColor"]) ?: Color.BLACK)
            gravity = Gravity.CENTER
            val padding = 8.dpToPx()
            setPadding(padding * 2, padding, padding * 2, padding)
        }
    }

    private fun createSwitchView(props: Map<String, Any>): android.widget.Switch {
        return android.widget.Switch(context).apply {
            props["checked"]?.let { isChecked = it as? Boolean ?: false }
            props["color"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    thumbTintList = android.content.res.ColorStateList.valueOf(color)
                }
            }
        }
    }

    private fun createCheckBoxView(props: Map<String, Any>): android.widget.CheckBox {
        return android.widget.CheckBox(context).apply {
            resolveValue(props["text"])?.let { text = it.toString() }
            props["checked"]?.let { isChecked = it as? Boolean ?: false }
        }
    }

    private fun createTabsView(props: Map<String, Any>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = resolveGravity(props["align"] as? String)
            
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
        }
    }

    private fun createCarouselView(props: Map<String, Any>): FrameLayout {
        return FrameLayout(context).apply {
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
        }
    }

    private fun createModalView(props: Map<String, Any>): FrameLayout {
        return FrameLayout(context).apply {
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
        }
    }

    private fun createBottomSheetView(props: Map<String, Any>): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
            
            props["backgroundColor"]?.let {
                val color = resolveColorValue(it)
                if (color != null) {
                    setBackgroundColor(color)
                }
            }
        }
    }

    private fun createDefaultView(type: String, props: Map<String, Any>): View {
        return View(context).apply {
            setBackgroundColor(Color.LTGRAY)
        }
    }

    private fun renderChildren(component: Component, parentView: View) {
        val childrenRef = component.children ?: return
        
        when (parentView) {
            is LinearLayout, is RelativeLayout, is FrameLayout, is GridLayout, is ScrollView -> {
                val viewGroup = if (parentView is ScrollView) {
                    val contentView = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                    }
                    parentView.addView(contentView)
                    contentView
                } else {
                    parentView as ViewGroup
                }
                
                childrenRef.explicitList?.forEach { childId ->
                    surface.getComponent(childId)?.let { childComponent ->
                        val childView = render(childComponent)
                        addChildToParent(viewGroup, childView, childComponent)
                    }
                }
                
                childrenRef.dataBinding?.let { binding ->
                    val dataList = surfaceManager.getDataAtPath(surface.surfaceId, binding) as? List<*>
                    val templateId = childrenRef.templateComponentId
                    
                    dataList?.forEachIndexed { index, data ->
                        templateId?.let { tid ->
                            surface.getComponent(tid)?.let { template ->
                                val itemView = renderWithData(template, data, index)
                                itemView.tag = index
                                viewGroup.addView(itemView)
                            }
                        }
                    }
                }
            }
            is androidx.recyclerview.widget.RecyclerView -> {
                val recyclerView = parentView
                childrenRef.dataBinding?.let { binding ->
                    val dataList = surfaceManager.getDataAtPath(surface.surfaceId, binding) as? List<*>
                    val templateId = childrenRef.templateComponentId
                    
                    if (dataList != null && templateId != null) {
                        recyclerView.adapter = A2UIListAdapter(
                            context,
                            surface,
                            surfaceManager,
                            dataList,
                            templateId
                        )
                    }
                }
            }
        }
    }

    internal fun renderWithData(component: Component, data: Any?, position: Int): View {
        val componentType = component.component.keys.firstOrNull() ?: return View(context)
        val originalProps = component.component[componentType] as? Map<String, Any> ?: emptyMap()
        val props = resolveDataBinding(originalProps, data, position)
        
        val view = createComponentView(componentType, props, component)
        componentViews[component.id] = view
        
        if (component.children != null) {
            renderChildrenWithData(component, view, data, position)
        }
        
        setupEvents(component, view)
        
        return view
    }

    private fun renderChildrenWithData(component: Component, parentView: View, data: Any?, position: Int) {
        val childrenRef = component.children ?: return
        
        when (parentView) {
            is LinearLayout, is RelativeLayout, is FrameLayout, is GridLayout -> {
                val viewGroup = parentView as ViewGroup
                
                childrenRef.explicitList?.forEach { childId ->
                    surface.getComponent(childId)?.let { childComponent ->
                        val childView = renderWithData(childComponent, data, position)
                        addChildToParent(viewGroup, childView, childComponent)
                    }
                }
                
                childrenRef.dataBinding?.let { binding ->
                    val subDataList = resolveBindingValue(binding, data)
                    val templateId = childrenRef.templateComponentId
                    
                    if (subDataList is List<*>) {
                        subDataList.forEachIndexed { index, item ->
                            templateId?.let { tid ->
                                surface.getComponent(tid)?.let { template ->
                                    val itemView = renderWithData(template, item, index)
                                    viewGroup.addView(itemView)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resolveDataBinding(props: Map<String, Any>, data: Any?, position: Int): Map<String, Any> {
        if (data == null) return props
        
        val resolved = mutableMapOf<String, Any>()
        props.forEach { (key, value) ->
            resolved[key] = when (value) {
                is Map<*, *> -> {
                    val map = value as Map<String, Any>
                    val binding = map["binding"] as? String
                    if (binding != null) {
                        resolveBindingValue(binding, data) ?: map["literalString"] ?: ""
                    } else {
                        value
                    }
                }
                else -> value
            }
        }
        return resolved
    }

    private fun resolveBindingValue(binding: String, data: Any?): Any? {
        if (data == null) return null
        if (binding.isEmpty()) return data
        
        val keys = binding.split(".")
        var current: Any = data
        
        for (key in keys) {
            when (current) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    current = (current as Map<String, Any>)[key] ?: return null
                }
                else -> return null
            }
        }
        return current
    }

    private fun addChildToParent(parent: ViewGroup, child: View, component: Component) {
        val props = component.component.values.firstOrNull() as? Map<String, Any> ?: emptyMap()
        
        when (parent) {
            is LinearLayout -> {
                val layoutParams = LinearLayout.LayoutParams(
                    widthDp(props["width"]),
                    heightDp(props["height"])
                )
                
                props["margin"]?.let {
                    val margin = (it as? Number)?.toInt() ?: 0
                    layoutParams.setMargins(margin.dpToPx(), margin.dpToPx(), margin.dpToPx(), margin.dpToPx())
                }
                
                props["marginStart"]?.let {
                    layoutParams.marginStart = (it as? Number)?.toInt()?.dpToPx() ?: 0
                }
                
                props["marginEnd"]?.let {
                    layoutParams.marginEnd = (it as? Number)?.toInt()?.dpToPx() ?: 0
                }
                
                props["flexWeight"]?.let {
                    layoutParams.weight = (it as? Number)?.toFloat() ?: 1f
                    layoutParams.width = 0
                }
                
                parent.addView(child, layoutParams)
            }
            is RelativeLayout -> {
                val layoutParams = RelativeLayout.LayoutParams(
                    widthDp(props["width"]),
                    heightDp(props["height"])
                )
                
                props["alignParentTop"]?.let {
                    if (it as? Boolean == true) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                }
                props["alignParentBottom"]?.let {
                    if (it as? Boolean == true) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                props["centerInParent"]?.let {
                    if (it as? Boolean == true) layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                }
                
                parent.addView(child, layoutParams)
            }
            is GridLayout -> {
                val layoutParams = GridLayout.LayoutParams().apply {
                    width = widthDp(props["width"])
                    height = heightDp(props["height"])
                    
                    props["column"]?.let {
                        columnSpec = GridLayout.spec((it as? Number)?.toInt() ?: 0)
                    }
                    props["row"]?.let {
                        rowSpec = GridLayout.spec((it as? Number)?.toInt() ?: 0)
                    }
                    props["columnWeight"]?.let {
                        columnSpec = GridLayout.spec(0, (it as? Number)?.toFloat() ?: 1f)
                    }
                }
                parent.addView(child, layoutParams)
            }
            else -> parent.addView(child)
        }
    }

    private fun setupEvents(component: Component, view: View) {
        component.events?.forEach { (eventType, actionId) ->
            when (eventType) {
                A2UIEventTypes.ON_CLICK -> {
                    view.setOnClickListener {
                        handleEvent(actionId, component.id, view)
                    }
                }
                A2UIEventTypes.ON_CHANGE -> {
                    if (view is EditText) {
                        view.setOnEditorActionListener { _, _, _ ->
                            handleEvent(actionId, component.id, view)
                            true
                        }
                    }
                }
            }
        }
    }

    private fun handleEvent(actionId: String, componentId: String, view: View? = null) {
        val data = mutableMapOf<String, Any>()
        view?.let {
            val index = it.getTag()
            if (index != null) {
                data["index"] = index
            }
        }
        
        if (eventCallback != null) {
            eventCallback.invoke(actionId, componentId, data)
        } else {
            Toast.makeText(context, "Event: $actionId on $componentId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resolveValue(value: Any?): Any? {
        return when (value) {
            is String -> value
            is Number -> value
            is Boolean -> value
            is Map<*, *> -> {
                val map = value as Map<String, Any>
                val literalString = map["literalString"] as? String
                val binding = map["binding"] as? String
                
                when {
                    binding != null -> {
                        val resolved = resolveBinding(binding)
                        if (resolved != null) {
                            if (literalString != null) {
                                "$literalString$resolved"
                            } else {
                                resolved
                            }
                        } else {
                            literalString ?: ""
                        }
                    }
                    literalString != null -> literalString
                    map["prefix"] != null -> {
                        map["prefix"].toString() + (map["binding"]?.toString() ?: "")
                    }
                    map["suffix"] != null -> {
                        map["binding"]?.toString()?.plus(map["suffix"].toString()) ?: ""
                    }
                    else -> map
                }
            }
            null -> null
            else -> value.toString()
        }
    }

    private fun resolveBinding(path: String): Any? {
        if (path.isEmpty()) return null
        return surfaceManager.getDataAtPath(surface.surfaceId, "/$path")
    }

    private fun resolveColorValue(value: Any?): Int? {
        return when (value) {
            is String -> {
                try {
                    Color.parseColor(value)
                } catch (e: Exception) {
                    null
                }
            }
            is Number -> value.toInt()
            is Map<*, *> -> {
                val map = value as Map<String, Any>
                val binding = map["binding"] as? String
                if (binding != null) {
                    val resolved = resolveBinding(binding)
                    when (resolved) {
                        is String -> {
                            try {
                                Color.parseColor(resolved)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        is Number -> resolved.toInt()
                        else -> null
                    }
                } else null
            }
            else -> null
        }
    }

    private fun resolveGravity(align: String?): Int {
        return when (align) {
            "center" -> Gravity.CENTER
            "start" -> Gravity.START
            "end" -> Gravity.END
            "top" -> Gravity.TOP
            "bottom" -> Gravity.BOTTOM
            "spaceBetween" -> Gravity.CENTER_VERTICAL or Gravity.HORIZONTAL_GRAVITY_MASK
            "spaceAround" -> Gravity.CENTER_VERTICAL
            else -> Gravity.START
        }
    }

    private fun Int.dpToPx(): Int = (this * context.resources.displayMetrics.density).roundToInt()

    private fun widthDp(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt().dpToPx()
            "match" -> ViewGroup.LayoutParams.MATCH_PARENT
            "wrap" -> ViewGroup.LayoutParams.WRAP_CONTENT
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun heightDp(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt().dpToPx()
            "match" -> ViewGroup.LayoutParams.MATCH_PARENT
            "wrap" -> ViewGroup.LayoutParams.WRAP_CONTENT
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}
