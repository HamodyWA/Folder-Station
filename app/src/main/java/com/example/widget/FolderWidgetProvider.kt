package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
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
import com.example.data.WidgetBindingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_WIDGET_PINNED) {
            val pinnedFolderId = intent.getStringExtra(EXTRA_PINNED_FOLDER_ID)
            val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

            if (!pinnedFolderId.isNullOrBlank() && widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(context)
                    val dao = db.folderDao()
                    dao.bindWidget(WidgetBindingEntity(widgetId = widgetId, folderId = pinnedFolderId))

                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, widgetId)
                }
            }
        }
    }

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
                for (widgetId in appWidgetIds) {
                    dao.unbindWidget(widgetId)
                }
            } catch (e: Exception) {
                // Ignore cleanup error
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_PINNED = "com.example.folderstation.ACTION_WIDGET_PINNED"
        const val EXTRA_PINNED_FOLDER_ID = "pinned_folder_id"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dao = AppDatabase.getInstance(context).folderDao()

                    // Resolve bound folder for this specific widget ID
                    val folderId = dao.getFolderIdForWidgetSync(appWidgetId)
                    val folder = if (!folderId.isNullOrBlank()) {
                        dao.getFolderByIdSync(folderId)
                    } else {
                        // Fallback: search in legacy or first folder
                        dao.getAllFoldersSync().firstOrNull { it.getWidgetIdList().contains(appWidgetId) }
                            ?: dao.getAllFoldersSync().firstOrNull()
                    } ?: FolderEntity(name = context.getString(R.string.app_name))

                    // Select layout resource matching shape and grid columns
                    val layoutId = when {
                        folder.folderShape == FolderEntity.ShapeType.CIRCLE.name -> {
                            R.layout.folder_widget_layout_circular
                        }
                        folder.columns == 1 -> {
                            R.layout.folder_widget_layout_1col
                        }
                        folder.columns == 2 -> {
                            R.layout.folder_widget_layout_2col
                        }
                        folder.columns == 4 -> {
                            R.layout.folder_widget_layout_4col
                        }
                        else -> {
                            R.layout.folder_widget_layout
                        }
                    }

                    val views = RemoteViews(context.packageName, layoutId)

                    // 1. Render custom background with exact shape and styling
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

                    // 3. Header / Edit Button Intent (opens MainActivity targeting this folder and widget ID)
                    val editIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("folder_id", folder.id)
                        putExtra("app_widget_id", appWidgetId)
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
                        data = Uri.parse("widget://$appWidgetId/${folder.id}/${folder.updatedAt}")
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
            val componentName = ComponentName(context, FolderWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
