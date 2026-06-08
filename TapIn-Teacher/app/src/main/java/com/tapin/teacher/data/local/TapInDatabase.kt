package com.tapin.teacher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tapin.teacher.data.local.dao.AttendanceDao
import com.tapin.teacher.data.local.dao.SyncQueueDao
import com.tapin.teacher.data.local.entity.AttendanceRecord
import com.tapin.teacher.data.local.entity.SyncQueueItem

@Database(
    entities = [AttendanceRecord::class, SyncQueueItem::class],
    version = 1,
    exportSchema = false
)
abstract class TapInDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun syncQueueDao(): SyncQueueDao
}
