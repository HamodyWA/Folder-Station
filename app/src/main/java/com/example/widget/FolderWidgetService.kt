package com.example.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.R
import com.example.data.AppDatabase
import com.example.data.FolderAppItem
import com.example.data.FolderEntity
import com.example.service.AppScanner

class FolderWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FolderRemoteViewsFactory(applicationContext, intent)
    }
}

class FolderRemoteViewsFactory(
    private val context: Context,
    intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val appWidgetId = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    )

    private val appScanner = AppScanner(context)
    private var currentFolder: FolderEntity? = null
    private var appItems: List<FolderAppItem> = emptyList()

    override fun onCreate() {
        loadData()
    }

    override fun onDataSetChanged() {
        loadData()
    }

    private fun loadData() {
        try {
            val db = AppDatabase.getInstance(context)
            val dao = db.folderDao()

            val folderId = dao.getFolderIdForWidgetSync(appWidgetId)
            val folder = if (!folderId.isNullOrBlank()) {
                dao.getFolderByIdSync(folderId)
            } else {
                dao.getAllFoldersSync().firstOrNull { it.getWidgetIdList().contains(appWidgetId) }
                    ?: dao.getAllFoldersSync().firstOrNull()
            }

            currentFolder = folder
            appItems = folder?.getApps() ?: emptyList()
        } catch (e: Exception) {
            appItems = emptyList()
        }
    }

    override fun onDestroy() {
        appItems = emptyList()
    }

    override fun getCount(): Int = appItems.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position !in appItems.indices) return null
        val item = appItems[position]
        val folder = currentFolder

        val views = RemoteViews(context.packageName, R.layout.folder_widget_item)

        // Set Icon
        val bitmap = appScanner.getAppIcon(item.packageName, 100)
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.app_icon, bitmap)
        } else {
            views.setImageViewResource(R.id.app_icon, R.drawable.ic_folder_station_logo)
        }

        // Set Label
        if (folder?.showAppLabels != false) {
            views.setViewVisibility(R.id.app_label, View.VISIBLE)
            views.setTextViewText(R.id.app_label, item.getDisplayName())
            try {
                val colorHex = folder?.appLabelColorHex ?: "#FFFFFF"
                views.setTextColor(R.id.app_label, Color.parseColor(colorHex))
            } catch (e: Exception) {
                views.setTextColor(R.id.app_label, Color.WHITE)
            }
        } else {
            views.setViewVisibility(R.id.app_label, View.GONE)
        }

        // Fill-in Intent for app launching
        val fillInIntent = Intent().apply {
            putExtra(FolderAppLaunchActivity.EXTRA_PACKAGE_NAME, item.packageName)
            putExtra(FolderAppLaunchActivity.EXTRA_ACTIVITY_NAME, item.activityName)
        }
        views.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
