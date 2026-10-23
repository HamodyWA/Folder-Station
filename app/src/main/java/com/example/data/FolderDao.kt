package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Query("SELECT * FROM folders ORDER BY updatedAt DESC")
    fun getAllFoldersFlow(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders ORDER BY updatedAt DESC")
    fun getAllFoldersSync(): List<FolderEntity>

    @Query("SELECT * FROM folders WHERE id = :id LIMIT 1")
    fun getFolderByIdFlow(id: String): Flow<FolderEntity?>

    @Query("SELECT * FROM folders WHERE id = :id LIMIT 1")
    fun getFolderByIdSync(id: String): FolderEntity?

    @Query("SELECT folderId FROM widget_bindings WHERE widgetId = :widgetId LIMIT 1")
    fun getFolderIdForWidgetSync(widgetId: Int): String?

    @Query("SELECT widgetId FROM widget_bindings WHERE folderId = :folderId")
    fun getWidgetIdsForFolderSync(folderId: String): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String)

    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bindWidget(binding: WidgetBindingEntity)

    @Query("DELETE FROM widget_bindings WHERE widgetId = :widgetId")
    suspend fun unbindWidget(widgetId: Int)

    @Query("DELETE FROM widget_bindings WHERE folderId = :folderId")
    suspend fun unbindAllForFolder(folderId: String)
}
