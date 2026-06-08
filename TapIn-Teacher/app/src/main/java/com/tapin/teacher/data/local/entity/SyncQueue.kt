package com.tapin.teacher.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks which attendance records still need to be uploaded.
 * Decoupled from AttendanceRecord so we can retry without re-reading all records.
 */
@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey
    @ColumnInfo(name = "record_id")
    val recordId: String,              // FK → AttendanceRecord.localId

    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0,

    @ColumnInfo(name = "last_attempt")
    val lastAttempt: Long? = null,     // Epoch ms of last sync attempt

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
