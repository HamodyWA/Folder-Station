package com.example.service

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import com.example.model.InstalledAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppScanner(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val iconCache = LruCache<String, Bitmap>(150)

    suspend fun getInstalledApps(): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(launcherIntent, 0)
        val appList = mutableListOf<InstalledAppInfo>()

        for (resolveInfo in resolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            // Don't list our own app inside folder shortcut options if preferred, or include it
            val activityName = resolveInfo.activityInfo.name
            val appName = resolveInfo.loadLabel(packageManager).toString()

            val appInfo = resolveInfo.activityInfo.applicationInfo
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            // Lazy or cached bitmap
            var bitmap = iconCache.get(pkg)
            if (bitmap == null) {
                try {
                    val drawable = resolveInfo.loadIcon(packageManager)
                    bitmap = drawableToBitmap(drawable, 96, 96)
                    if (bitmap != null) {
                        iconCache.put(pkg, bitmap)
                    }
                } catch (e: Exception) {
                    // Ignore decoding failure
                }
            }

            appList.add(
                InstalledAppInfo(
                    packageName = pkg,
                    activityName = activityName,
                    appName = appName,
                    isGame = isGame,
                    isSystemApp = isSystem,
                    iconBitmap = bitmap
                )
            )
        }

        appList.sortedBy { it.appName.lowercase() }
    }

    fun getAppIcon(packageName: String, sizePx: Int = 96): Bitmap? {
        iconCache.get(packageName)?.let { return it }
        return try {
            val drawable = packageManager.getApplicationIcon(packageName)
            val bmp = drawableToBitmap(drawable, sizePx, sizePx)
            if (bmp != null) {
                iconCache.put(packageName, bmp)
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    private fun drawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap? {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return Bitmap.createScaledBitmap(drawable.bitmap, width, height, true)
        }

        return try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}
