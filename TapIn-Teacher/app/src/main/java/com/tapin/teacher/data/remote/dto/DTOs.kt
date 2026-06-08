package com.tapin.teacher.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Auth ─────────────────────────────────────────────────────

data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val email: String,
    @SerializedName("fullName") val fullName: String,
    val role: String,
    @SerializedName("studentId") val studentId: String?
)

// ── Courses ───────────────────────────────────────────────────

data class CoursesResponse(val data: List<CourseDto>)

data class CourseDto(
    val id: String,
    val name: String,
    val code: String,
    @SerializedName("teacherId") val teacherId: String,
    val description: String?,
    @SerializedName("_count") val count: CountDto?
)

data class CountDto(
    val enrollments: Int = 0,
    val sessions: Int = 0
)

// ── Sessions ──────────────────────────────────────────────────

data class CreateSessionRequest(
    @SerializedName("courseId") val courseId: String,
    val notes: String? = null
)

data class SessionResponse(val data: SessionDto)

data class SessionDto(
    val id: String,
    @SerializedName("courseId") val courseId: String,
    @SerializedName("teacherId") val teacherId: String,
    @SerializedName("startedAt") val startedAt: String,
    @SerializedName("endedAt") val endedAt: String?,
    val status: String,
    val notes: String?,
    val course: CourseSummaryDto?,
    val teacher: TeacherSummaryDto?,
    @SerializedName("_count") val count: RecordCountDto?
)

data class CourseSummaryDto(val id: String, val name: String, val code: String)
data class TeacherSummaryDto(val id: String, @SerializedName("fullName") val fullName: String)
data class RecordCountDto(val records: Int = 0)

data class SessionsResponse(
    val data: List<SessionDto>,
    val pagination: PaginationDto
)

data class PaginationDto(val page: Int, val limit: Int, val total: Int, val totalPages: Int)

// ── NFC Validate ──────────────────────────────────────────────

data class ValidateNfcRequest(
    @SerializedName("encryptedToken") val encryptedToken: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("deviceInfo") val deviceInfo: Map<String, String>? = null
)

data class ValidateNfcResponse(
    val message: String,
    val student: StudentSummaryDto,
    val record: RecordSummaryDto,
    val course: CourseSummaryDto?
)

data class StudentSummaryDto(
    val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("studentId") val studentId: String?
)

data class RecordSummaryDto(
    val id: String,
    @SerializedName("tappedAt") val tappedAt: String,
    val status: String
)

// ── Bulk sync ─────────────────────────────────────────────────

data class BulkSyncRequest(val records: List<SyncRecordDto>)

data class SyncRecordDto(
    @SerializedName("sessionId")  val sessionId: String,
    @SerializedName("studentId")  val studentId: String,
    @SerializedName("tappedAt")   val tappedAt: String,
    val status: String = "present"
)

data class BulkSyncResponse(val message: String, val results: SyncResultDto)

data class SyncResultDto(val synced: Int, val skipped: Int, val errors: List<Any>)

// ── Error ─────────────────────────────────────────────────────

data class ApiError(val error: String)
