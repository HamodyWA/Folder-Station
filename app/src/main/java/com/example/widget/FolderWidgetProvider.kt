package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import com.example.data.FolderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = AppDatabase.getInstance(context).folderDao()
                val folders = dao.getAllFoldersSync()
                for (widgetId in appWidgetIds) {
                    for (folder in folders) {
                        if (folder.getWidgetIdList().contains(widgetId)) {
                            dao.updateFolder(folder.removeWidgetId(widgetId))
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore cleanup error
            }
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = AppDatabase.getInstance(context).folderDao()
                    val folder = dao.getFolderForWidgetIdSync(appWidgetId.toString())
                        ?: dao.getAllFoldersSync().firstOrNull()
                        ?: FolderEntity(name = context.getString(R.string.app_name))

                    val views = RemoteViews(context.packageName, R.layout.folder_widget_layout)

                    // 1. Render custom background
                    val bgBitmap = WidgetBitmapRenderer.renderBackground(
                        widthPx = 450,
                        heightPx = 450,
                        folder = folder
                    )
                    views.setImageViewBitmap(R.id.widget_bg_image, bgBitmap)

                    // 2. Configure Header
                    if (folder.showTitle) {
                        views.setViewVisibility(R.id.widget_header, View.VISIBLE)
                        views.setTextViewText(R.id.widget_title, folder.name)
                        try {
                            views.setTextColor(R.id.widget_title, Color.parseColor(folder.titleColorHex))
                        } catch (e: Exception) {
                            views.setTextColor(R.id.widget_title, Color.WHITE)
                        }
                    } else {
                        views.setViewVisibility(R.id.widget_header, View.GONE)
                    }

                    // 3. Header / Edit Button Intent (opens MainActivity to edit this folder)
                    val editIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("folder_id", folder.id)
                        putExtra("from_widget", true)
                    }
                    val editPendingIntent = PendingIntent.getActivity(
                        context,
                        appWidgetId * 10 + 1,
                        editIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_edit_btn, editPendingIntent)
                    views.setOnClickPendingIntent(R.id.widget_header_icon, editPendingIntent)

                    // 4. RemoteViewsService Intent for GridView
                    val serviceIntent = Intent(context, FolderWidgetService::class.java).apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        // Make intent unique per widget
                        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
                    }
                    views.setRemoteAdapter(R.id.widget_grid, serviceIntent)

                    // 5. PendingIntent template for items in the GridView
                    val clickIntent = Intent(context, FolderAppLaunchActivity::class.java)
                    val clickPendingIntent = PendingIntent.getActivity(
                        context,
                        appWidgetId * 10 + 2,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                    views.setPendingIntentTemplate(R.id.widget_grid, clickPendingIntent)

                    // 6. Empty state handling
                    val apps = folder.getApps()
                    if (apps.isEmpty()) {
                        views.setViewVisibility(R.id.widget_empty, View.VISIBLE)
                        views.setOnClickPendingIntent(R.id.widget_empty, editPendingIntent)
                    } else {
                        views.setViewVisibility(R.id.widget_empty, View.GONE)
                    }

                    // 7. Update and notify view data changed
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_grid)
                } catch (e: Exception) {
                    // Fail gracefully
                }
            }
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = android.content.ComponentName(context, FolderWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
