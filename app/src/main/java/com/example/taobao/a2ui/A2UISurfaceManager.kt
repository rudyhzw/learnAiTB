package com.example.taobao.a2ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import java.util.concurrent.ConcurrentHashMap

class A2UISurfaceManager(private val context: Context) {

    private val surfaces = ConcurrentHashMap<String, A2UISurface>()
    private val dataModels = ConcurrentHashMap<String, MutableMap<String, Any>>()

    fun createSurface(surfaceId: String, rootView: ViewGroup, eventCallback: ((actionId: String, componentId: String, data: Map<String, Any>?) -> Unit)? = null): A2UISurface {
        val surface = A2UISurface(surfaceId, context, rootView, this, eventCallback)
        surfaces[surfaceId] = surface
        if (!dataModels.containsKey(surfaceId)) {
            dataModels[surfaceId] = mutableMapOf()
        }
        return surface
    }

    fun getSurface(surfaceId: String): A2UISurface? = surfaces[surfaceId]

    fun deleteSurface(surfaceId: String) {
        surfaces[surfaceId]?.destroy()
        surfaces.remove(surfaceId)
        dataModels.remove(surfaceId)
    }

    fun updateDataModel(surfaceId: String, path: String, data: Any?) {
        val model = dataModels.getOrPut(surfaceId) { mutableMapOf() }
        if (path.isEmpty() || path == "/") {
            if (data is Map<*, *>) {
                model.clear()
                @Suppress("UNCHECKED_CAST")
                (data as Map<String, Any>).forEach { (k, v) ->
                    model[k] = v
                }
            }
        } else {
            val keys = path.removePrefix("/").split("/")
            if (keys.size == 1) {
                if (data != null) {
                    model[keys[0]] = data
                }
            } else {
                var current: MutableMap<String, Any> = model
                for (i in 0 until keys.size - 1) {
                    val key = keys[i]
                    if (!current.containsKey(key) || current[key] !is MutableMap<*, *>) {
                        current[key] = mutableMapOf<String, Any>()
                    }
                    @Suppress("UNCHECKED_CAST")
                    current = current[key] as MutableMap<String, Any>
                }
                @Suppress("UNCHECKED_CAST")
                val lastKey = keys.last()
                if (data != null) {
                    current[lastKey] = data
                }
            }
        }
        surfaces[surfaceId]?.onDataModelUpdated(path, model)
    }

    fun incrementValue(surfaceId: String, path: String): Boolean {
        val model = dataModels[surfaceId] ?: return false
        val keys = path.removePrefix("/").split("/")
        
        if (keys.isEmpty()) return false
        
        var current: Any = model
        for (i in 0 until keys.size - 1) {
            if (current is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                current = (current as Map<String, Any>)[keys[i]] ?: return false
            } else {
                return false
            }
        }
        
        val lastKey = keys.last()
        if (current is MutableMap<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val currentMap = current as MutableMap<String, Any>
            val currentValue = currentMap[lastKey]
            
            val newValue = when (currentValue) {
                is Int -> currentValue + 1
                is Double -> currentValue + 1.0
                is Float -> currentValue + 1f
                is Long -> currentValue + 1L
                is String -> {
                    val num = currentValue.toDoubleOrNull()
                    if (num != null) {
                        String.format("%.2f", num + 1)
                    } else {
                        return false
                    }
                }
                else -> return false
            }
            
            currentMap[lastKey] = newValue
            surfaces[surfaceId]?.reRender()
            return true
        }
        return false
    }

    fun getDataModel(surfaceId: String): Map<String, Any>? = dataModels[surfaceId]

    fun getDataAtPath(surfaceId: String, path: String): Any? {
        val model = dataModels[surfaceId] ?: return null
        if (path.isEmpty() || path == "/") return model
        
        val cleanPath = path.removePrefix("/")
        if (cleanPath.isEmpty()) return model
        
        val keys = cleanPath.split("/")
        var current: Any = model
        for (key in keys) {
            if (current is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                current = (current as Map<String, Any>)[key] ?: return null
            } else {
                return null
            }
        }
        return current
    }
}

class A2UISurface(
    val surfaceId: String,
    private val context: Context,
    private val rootView: ViewGroup,
    private val manager: A2UISurfaceManager,
    private val eventCallback: ((actionId: String, componentId: String, data: Map<String, Any>?) -> Unit)? = null
) {
    private val components = ConcurrentHashMap<String, Component>()
    var rootComponentId: String? = null
    private var lastRenderDataModel: Map<String, Any>? = null

    companion object {
        private const val TAG = "A2UISurface"
    }

    fun updateComponents(components: List<Component>) {
        components.forEach { component ->
            this.components[component.id] = component
            android.util.Log.d(TAG, "Added component: ${component.id}")
        }
        render()
    }

    fun onDataModelUpdated(path: String, dataModel: Map<String, Any>) {
        android.util.Log.d(TAG, "Data model updated, re-rendering")
        reRender()
    }

    fun render() {
        android.util.Log.d(TAG, "Rendering surface: $surfaceId, components count: ${components.size}")
        lastRenderDataModel = manager.getDataModel(surfaceId)
        doRender()
    }

    private fun doRender() {
        android.util.Log.d(TAG, "doRender called, components: ${components.keys}")
        
        val renderer = A2UIRenderer(context, this, manager, eventCallback)
        rootView.removeAllViews()
        
        if (components.isEmpty()) {
            android.util.Log.w(TAG, "No components to render!")
            return
        }
        
        rootComponentId?.let { rootId ->
            val rootComponent = components[rootId]
            rootComponent?.let {
                try {
                    android.util.Log.d(TAG, "Rendering root component: $rootId")
                    val view = renderer.render(it)
                    rootView.addView(view)
                } catch (e: Exception) {
                    android.util.Log.e("A2UISurface", "Render error: ${e.message}")
                }
            }
        } ?: run {
            val rootComponent = components["root"]
            rootComponent?.let {
                try {
                    android.util.Log.d(TAG, "Rendering default root component")
                    val view = renderer.render(it)
                    rootView.addView(view)
                } catch (e: Exception) {
                    android.util.Log.e("A2UISurface", "Render error: ${e.message}")
                }
            }
        }
    }

    fun reRender() {
        doRender()
    }

    fun getComponent(componentId: String): Component? = components[componentId]

    fun destroy() {
        components.clear()
        rootView.removeAllViews()
    }
}
