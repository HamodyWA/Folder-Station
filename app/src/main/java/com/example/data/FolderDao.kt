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

    @Query("SELECT * FROM folders WHERE appWidgetIds LIKE '%' || :widgetIdStr || '%' LIMIT 1")
    fun getFolderForWidgetIdSync(widgetIdStr: String): FolderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity)

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String)

    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}
