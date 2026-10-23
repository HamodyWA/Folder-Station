package com.example.data

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import com.example.service.AppScanner
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

    suspend fun getFolderByIdSync(id: String): FolderEntity? = withContext(Dispatchers.IO) {
        folderDao.getFolderByIdSync(id)
    }

    suspend fun saveFolder(folder: FolderEntity): Boolean = withContext(Dispatchers.IO) {
        try {
            val toSave = folder.copy(updatedAt = System.currentTimeMillis())
            folderDao.insertFolder(toSave)

            withContext(Dispatchers.Main) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val boundWidgets = withContext(Dispatchers.IO) {
                    folderDao.getWidgetIdsForFolderSync(toSave.id)
                }

                if (boundWidgets.isNotEmpty()) {
                    for (wId in boundWidgets) {
                        FolderWidgetProvider.updateAppWidget(context, appWidgetManager, wId)
                    }
                } else {
                    FolderWidgetProvider.updateAllWidgets(context)
                }
            }
            true
        } catch (e: Exception) {
            false
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
        folderDao.unbindAllForFolder(id)
        folderDao.deleteFolderById(id)
        withContext(Dispatchers.Main) {
            FolderWidgetProvider.updateAllWidgets(context)
        }
    }

    suspend fun initializeDefaultFoldersIfEmpty() = withContext(Dispatchers.IO) {
        if (folderDao.getFolderCount() == 0) {
            val appScanner = AppScanner(context)
            val installedApps = appScanner.getInstalledApps()

            fun getAppList(count: Int, offset: Int = 0): List<FolderAppItem> {
                val sublist = installedApps.drop(offset).take(count)
                return sublist.mapIndexed { idx, app ->
                    FolderAppItem(
                        packageName = app.packageName,
                        activityName = app.activityName,
                        label = app.appName,
                        order = idx
                    )
                }
            }

            // Folder 1: Games
            val gamesApps = installedApps.filter { it.isGame }.take(8).ifEmpty { getAppList(8, 0) }
            val gamesFolder = FolderEntity(
                name = "Games",
                columns = 2,
                rows = 2,
                folderShape = FolderEntity.ShapeType.ROUNDED_RECT.name,
                backgroundColorHex = "#0C112A",
                isGradient = true,
                gradientEndColorHex = "#231145",
                backgroundOpacity = 0.90f,
                borderColorHex = "#EC4899",
                borderThicknessDp = 1,
                showTitle = true,
                titleColorHex = "#FFFFFF"
            ).withApps(gamesApps.mapIndexed { i, a ->
                if (a is FolderAppItem) a else {
                    val info = a as com.example.model.InstalledAppInfo
                    FolderAppItem(packageName = info.packageName, activityName = info.activityName, label = info.appName, order = i)
                }
            })

            // Folder 2: Social Media
            val socialFolder = FolderEntity(
                name = "Social Media",
                columns = 2,
                rows = 2,
                folderShape = FolderEntity.ShapeType.ROUNDED_RECT.name,
                backgroundColorHex = "#081B26",
                isGradient = true,
                gradientEndColorHex = "#123E3A",
                backgroundOpacity = 0.90f,
                borderColorHex = "#10B981",
                borderThicknessDp = 1,
                showTitle = true,
                titleColorHex = "#FFFFFF"
            ).withApps(getAppList(6, 4))

            // Folder 3: Tools
            val toolsFolder = FolderEntity(
                name = "Tools",
                columns = 2,
                rows = 2,
                folderShape = FolderEntity.ShapeType.ROUNDED_RECT.name,
                backgroundColorHex = "#161D3B",
                isGradient = true,
                gradientEndColorHex = "#332214",
                backgroundOpacity = 0.90f,
                borderColorHex = "#F59E0B",
                borderThicknessDp = 1,
                showTitle = true,
                titleColorHex = "#FFFFFF"
            ).withApps(getAppList(7, 2))

            // Folder 4: Media
            val mediaFolder = FolderEntity(
                name = "Media",
                columns = 2,
                rows = 2,
                folderShape = FolderEntity.ShapeType.ROUNDED_RECT.name,
                backgroundColorHex = "#190F2E",
                isGradient = true,
                gradientEndColorHex = "#3B112D",
                backgroundOpacity = 0.90f,
                borderColorHex = "#8B5CF6",
                borderThicknessDp = 1,
                showTitle = true,
                titleColorHex = "#FFFFFF"
            ).withApps(getAppList(5, 6))

            // Folder 5: Productivity
            val productivityFolder = FolderEntity(
                name = "Productivity",
                columns = 2,
                rows = 2,
                folderShape = FolderEntity.ShapeType.ROUNDED_RECT.name,
                backgroundColorHex = "#0D1E30",
                isGradient = true,
                gradientEndColorHex = "#143734",
                backgroundOpacity = 0.90f,
                borderColorHex = "#06B6D4",
                borderThicknessDp = 1,
                showTitle = true,
                titleColorHex = "#FFFFFF"
            ).withApps(getAppList(6, 1))

            folderDao.insertFolder(gamesFolder)
            folderDao.insertFolder(socialFolder)
            folderDao.insertFolder(toolsFolder)
            folderDao.insertFolder(mediaFolder)
            folderDao.insertFolder(productivityFolder)
        }
    }

    fun requestPinWidget(folder: FolderEntity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val myProvider = ComponentName(context, FolderWidgetProvider::class.java)

            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val pinnedWidgetCallbackIntent = Intent(context, FolderWidgetProvider::class.java).apply {
                    action = FolderWidgetProvider.ACTION_WIDGET_PINNED
                    putExtra(FolderWidgetProvider.EXTRA_PINNED_FOLDER_ID, folder.id)
                }

                val successCallback = PendingIntent.getBroadcast(
                    context,
                    folder.id.hashCode(),
                    pinnedWidgetCallbackIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )

                val extras = Bundle().apply {
                    putString(FolderWidgetProvider.EXTRA_PINNED_FOLDER_ID, folder.id)
                }

                return appWidgetManager.requestPinAppWidget(myProvider, extras, successCallback)
            }
        }
        return false
    }
}
