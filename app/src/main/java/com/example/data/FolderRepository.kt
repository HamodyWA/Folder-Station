package com.example.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.example.widget.FolderWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class FolderRepository(
    private val context: Context,
    private val folderDao: FolderDao
) {

    val allFolders: Flow<List<FolderEntity>> = folderDao.getAllFoldersFlow()

    fun getFolderById(id: String): Flow<FolderEntity?> = folderDao.getFolderByIdFlow(id)

    suspend fun saveFolder(folder: FolderEntity) = withContext(Dispatchers.IO) {
        folderDao.insertFolder(folder)
        // Immediately notify widget manager to update all widgets
        withContext(Dispatchers.Main) {
            FolderWidgetProvider.updateAllWidgets(context)
        }
    }

    suspend fun duplicateFolder(folder: FolderEntity) = withContext(Dispatchers.IO) {
        val copy = folder.copy(
            id = UUID.randomUUID().toString(),
            name = "${folder.name} (Copy)",
            appWidgetIds = "",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        folderDao.insertFolder(copy)
    }

    suspend fun deleteFolder(id: String) = withContext(Dispatchers.IO) {
        folderDao.deleteFolderById(id)
        withContext(Dispatchers.Main) {
            FolderWidgetProvider.updateAllWidgets(context)
        }
    }

    suspend fun initializeDefaultFoldersIfEmpty() = withContext(Dispatchers.IO) {
        if (folderDao.getFolderCount() == 0) {
            val starter1 = PresetCatalog.presets[0].toFolderEntity("Main Station")
            val starter2 = PresetCatalog.presets[1].toFolderEntity("Quick Games")
            folderDao.insertFolder(starter1)
            folderDao.insertFolder(starter2)
        }
    }

    fun requestPinWidget(folder: FolderEntity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val myProvider = ComponentName(context, FolderWidgetProvider::class.java)

            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                // Pass folder ID in extras bundle
                val extras = Bundle().apply {
                    putString("pinned_folder_id", folder.id)
                }

                val pinnedWidgetCallbackIntent = Intent(context, FolderWidgetProvider::class.java).apply {
                    action = "com.example.folderstation.ACTION_WIDGET_PINNED"
                    putExtra("pinned_folder_id", folder.id)
                }

                val successCallback = PendingIntent.getBroadcast(
                    context,
                    0,
                    pinnedWidgetCallbackIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

                return appWidgetManager.requestPinAppWidget(myProvider, extras, successCallback)
            }
        }
        return false
    }
}
