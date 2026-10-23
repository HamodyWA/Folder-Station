package com.example.model

import android.graphics.Bitmap

data class InstalledAppInfo(
    val packageName: String,
    val activityName: String,
    val appName: String,
    val isGame: Boolean = false,
    val isSystemApp: Boolean = false,
    val iconBitmap: Bitmap? = null
)
