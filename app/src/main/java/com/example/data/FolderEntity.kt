package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My Folder",
    val columns: Int = 3,
    val rows: Int = 3,
    val iconSizeDp: Int = 44,
    val spacingDp: Int = 6,
    val cornerRadiusDp: Int = 24,
    val backgroundColorHex: String = "#11162E",
    val backgroundOpacity: Float = 0.90f,
    val folderShape: String = ShapeType.ROUNDED_RECT.name,
    val borderColorHex: String = "#388BFD",
    val borderThicknessDp: Int = 1,
    val showTitle: Boolean = true,
    val titleColorHex: String = "#FFFFFF",
    val titleSizeSp: Int = 13,
    val showAppLabels: Boolean = true,
    val appLabelColorHex: String = "#FFFFFF",
    val appLabelSizeSp: Int = 10,
    val isGradient: Boolean = true,
    val gradientEndColorHex: String = "#21153B",
    val frameImagePath: String? = null,
    val frameOpacity: Float = 0.85f,
    val frameScaleType: String = "FILL",
    val appsJson: String = "[]",
    val appWidgetIds: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    enum class ShapeType(val displayName: String) {
        ROUNDED_RECT("Rounded Rect"),
        SQUARE("Square"),
        CIRCLE("Circle"),
        LARGE_ROUNDED("Large Rounded"),
        MATERIAL("Material"),
        MINIMAL("Minimal"),
        LIST("List"),
        LARGE_ICON("Large Icon"),
        BORDER("Border")
    }

    fun getApps(): List<FolderAppItem> {
        return FolderAppItem.listFromJson(appsJson)
    }

    fun withApps(apps: List<FolderAppItem>): FolderEntity {
        return copy(
            appsJson = FolderAppItem.listToJson(apps),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun getWidgetIdList(): List<Int> {
        if (appWidgetIds.isBlank()) return emptyList()
        return appWidgetIds.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    fun addWidgetId(widgetId: Int): FolderEntity {
        val current = getWidgetIdList().toMutableSet()
        current.add(widgetId)
        return copy(
            appWidgetIds = current.joinToString(","),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun removeWidgetId(widgetId: Int): FolderEntity {
        val current = getWidgetIdList().toMutableSet()
        current.remove(widgetId)
        return copy(
            appWidgetIds = current.joinToString(","),
            updatedAt = System.currentTimeMillis()
        )
    }
}
