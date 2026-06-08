package com.tapin.teacher.data.local.dao

import androidx.room.*
import com.tapin.teacher.data.local.entity.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE session_id = :sessionId ORDER BY tapped_at ASC")
    fun getBySession(sessionId: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE session_id = :sessionId ORDER BY tapped_at ASC")
    suspend fun getBySessionOnce(sessionId: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records ORDER BY tapped_at DESC")
    fun getAll(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE synced = 0")
    suspend fun getUnsynced(): List<AttendanceRecord>

    @Query("UPDATE attendance_records SET synced = 1 WHERE local_id IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Query("SELECT COUNT(*) FROM attendance_records WHERE synced = 0")
    fun getPendingSyncCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE session_id = :sessionId")
    suspend fun countForSession(sessionId: String): Int

    @Query("SELECT * FROM attendance_records WHERE student_id = :studentId AND session_id = :sessionId LIMIT 1")
    suspend fun findDuplicate(studentId: String, sessionId: String): AttendanceRecord?

    @Query("SELECT DISTINCT session_id FROM attendance_records ORDER BY tapped_at DESC")
    suspend fun getDistinctSessionIds(): List<String>
}
