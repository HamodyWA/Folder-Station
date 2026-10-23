package com.example.data

import org.json.JSONArray
import org.json.JSONObject

data class FolderAppItem(
    val packageName: String,
    val activityName: String = "",
    val label: String,
    val customLabel: String = "",
    val order: Int = 0
) {
    fun getDisplayName(): String = customLabel.ifBlank { label }

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("packageName", packageName)
            put("activityName", activityName)
            put("label", label)
            put("customLabel", customLabel)
            put("order", order)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): FolderAppItem {
            return FolderAppItem(
                packageName = json.optString("packageName", ""),
                activityName = json.optString("activityName", ""),
                label = json.optString("label", "App"),
                customLabel = json.optString("customLabel", ""),
                order = json.optInt("order", 0)
            )
        }

        fun listToJson(list: List<FolderAppItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJson()) }
            return array.toString()
        }

        fun listFromJson(jsonStr: String): List<FolderAppItem> {
            if (jsonStr.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonStr)
                val list = mutableListOf<FolderAppItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(fromJson(obj))
                }
                list.sortedBy { it.order }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
