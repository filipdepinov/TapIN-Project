package com.tapin.teacher.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Room entity for an attendance tap recorded by the teacher's NFC reader.
 * Records are stored locally first, then synced to the backend.
 */
@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    val localId: String,               // UUID generated locally

    @ColumnInfo(name = "session_id")
    val sessionId: String,             // Backend session UUID

    @ColumnInfo(name = "student_id")
    val studentId: String,             // Backend student UUID

    @ColumnInfo(name = "student_name")
    val studentName: String,           // Cached for display without network

    @ColumnInfo(name = "student_number")
    val studentNumber: String?,        // e.g. "STU001"

    @ColumnInfo(name = "tapped_at")
    val tappedAt: Long,                // Epoch ms — exact tap timestamp

    @ColumnInfo(name = "status")
    val status: String = "present",

    @ColumnInfo(name = "synced")
    val synced: Boolean = false,       // true once backend confirms receipt

    @ColumnInfo(name = "course_name")
    val courseName: String = "",

    @ColumnInfo(name = "course_code")
    val courseCode: String = ""
)
