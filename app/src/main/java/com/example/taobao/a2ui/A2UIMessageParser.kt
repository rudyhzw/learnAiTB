package com.example.taobao.a2ui

import android.util.Log
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class A2UIMessageParser {
    private val objectMapper = ObjectMapper()
    
    companion object {
        private const val TAG = "A2UIMessageParser"
    }

    fun parseMessage(jsonString: String): A2UIMessage? {
        return try {
            val node = objectMapper.readTree(jsonString)
            objectMapper.convertValue(node, A2UIMessage::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse A2UI message: ${e.message}")
            null
        }
    }

    fun parseMessageList(jsonString: String): List<A2UIMessage> {
        return try {
            val node = objectMapper.readTree(jsonString)
            if (node.isArray()) {
                node.mapNotNull { item ->
                    try {
                        objectMapper.convertValue(item, A2UIMessage::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse message item: ${e.message}")
                        null
                    }
                }
            } else {
                listOfNotNull(parseMessage(jsonString))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse message list: ${e.message}")
            emptyList()
        }
    }

    fun parseComponent(componentNode: JsonNode): Component? {
        return try {
            val id = componentNode.get("id")?.asText() ?: return null
            val componentMap = mutableMapOf<String, Any>()
            
            componentNode.get("component")?.let { compNode ->
                compNode.fields().forEach { (key, value) ->
                    componentMap[key] = parseValue(value)
                }
            }
            
            val children = componentNode.get("children")?.let { childrenNode ->
                val explicitList = childrenNode.get("explicitList")?.map { it.asText() }
                val dataBinding = childrenNode.get("dataBinding")?.asText()
                val templateComponentId = childrenNode.get("templateComponentId")?.asText()
                
                if (explicitList != null || dataBinding != null) {
                    ChildrenReference(
                        explicitList = explicitList,
                        dataBinding = dataBinding,
                        templateComponentId = templateComponentId
                    )
                } else null
            }
            
            val events = componentNode.get("events")?.let { eventsNode ->
                val eventsMap = mutableMapOf<String, String>()
                eventsNode.fields().forEach { (key, value) ->
                    eventsMap[key] = value.asText()
                }
                eventsMap
            }
            
            Component(
                id = id,
                component = componentMap,
                children = children,
                events = events
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse component: ${e.message}")
            null
        }
    }

    private fun parseValue(node: JsonNode): Any {
        return when {
            node.isTextual -> node.asText()
            node.isNumber -> {
                if (node.isDouble || node.isFloat)
                    node.asDouble()
                else
                    node.asInt()
            }
            node.isBoolean -> node.asBoolean()
            node.isArray -> node.map { parseValue(it) }
            node.isObject -> {
                val map = mutableMapOf<String, Any>()
                node.fields().forEach { (key, value) ->
                    map[key] = parseValue(value)
                }
                map
            }
            else -> node.toString()
        }
    }

    fun toJson(message: A2UIMessage): String {
        return try {
            objectMapper.writeValueAsString(message)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to serialize message: ${e.message}")
            "{}"
        }
    }
}
