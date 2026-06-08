package com.tapin.student.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Auth ─────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto,
    @SerializedName("nfcToken") val nfcToken: String?
)

data class UserDto(
    val id: String,
    val email: String,
    @SerializedName("fullName") val fullName: String,
    val role: String,
    @SerializedName("studentId") val studentId: String?
)

data class RefreshNfcTokenResponse(
    @SerializedName("nfcToken") val nfcToken: String,
    @SerializedName("ttlMinutes") val ttlMinutes: Int
)

// ── Attendance ────────────────────────────────────────────────

data class MyAttendanceResponse(
    val data: List<CourseAttendanceDto>
)

data class CourseAttendanceDto(
    @SerializedName("courseId")       val courseId: String,
    @SerializedName("courseName")     val courseName: String,
    @SerializedName("courseCode")     val courseCode: String,
    @SerializedName("totalSessions")  val totalSessions: Int,
    val attended: Int,
    @SerializedName("attendanceRate") val attendanceRate: Double,
    val sessions: List<SessionDto>
)

data class SessionDto(
    @SerializedName("sessionId")  val sessionId: String,
    @SerializedName("startedAt")  val startedAt: String,
    val present: Boolean,
    @SerializedName("tappedAt")   val tappedAt: String?,
    val status: String?
)

// ── Generic error ─────────────────────────────────────────────

data class ApiError(
    val error: String
)
