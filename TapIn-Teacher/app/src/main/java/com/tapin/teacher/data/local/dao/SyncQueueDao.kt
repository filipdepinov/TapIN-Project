package com.tapin.teacher.data.local.dao

import androidx.room.*
import com.tapin.teacher.data.local.entity.SyncQueueItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun enqueue(item: SyncQueueItem)

    @Query("SELECT * FROM sync_queue ORDER BY created_at ASC")
    suspend fun getAll(): List<SyncQueueItem>

    @Query("DELETE FROM sync_queue WHERE record_id IN (:ids)")
    suspend fun removeByIds(ids: List<String>)

    @Query("UPDATE sync_queue SET retry_count = retry_count + 1, last_attempt = :now WHERE record_id = :id")
    suspend fun incrementRetry(id: String, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM sync_queue")
    fun count(): Flow<Int>

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
}
