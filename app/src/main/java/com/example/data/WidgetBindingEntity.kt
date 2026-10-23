package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widget_bindings")
data class WidgetBindingEntity(
    @PrimaryKey val widgetId: Int,
    val folderId: String,
    val updatedAt: Long = System.currentTimeMillis()
)
