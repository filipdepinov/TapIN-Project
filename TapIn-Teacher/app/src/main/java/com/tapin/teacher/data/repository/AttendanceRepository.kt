package com.tapin.teacher.data.repository

import com.google.gson.Gson
import com.tapin.teacher.data.local.dao.AttendanceDao
import com.tapin.teacher.data.local.dao.SyncQueueDao
import com.tapin.teacher.data.local.entity.AttendanceRecord
import com.tapin.teacher.data.local.entity.SyncQueueItem
import com.tapin.teacher.data.remote.api.ApiService
import com.tapin.teacher.data.remote.dto.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = 0) : Result<Nothing>()
}

@Singleton
class AttendanceRepository @Inject constructor(
    private val apiService: ApiService,
    private val attendanceDao: AttendanceDao,
    private val syncQueueDao: SyncQueueDao
) {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun getCourses(): Result<List<CourseDto>> {
        return try {
            val response = apiService.getCourses()
            if (response.isSuccessful) {
                Result.Success(response.body()!!.data)
            } else {
                val msg = try { Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error } catch (e: Exception) { "Error ${response.code()}" }
                Result.Error(msg, response.code())
            }
        } catch (e: Exception) { Result.Error(e.message ?: "Network error") }
    }

    suspend fun createSession(courseId: String, notes: String? = null): Result<SessionDto> {
        return try {
            val response = apiService.createSession(CreateSessionRequest(courseId, notes))
            if (response.isSuccessful) Result.Success(response.body()!!.data)
            else {
                val msg = try { Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error } catch (e: Exception) { "Error ${response.code()}" }
                Result.Error(msg, response.code())
            }
        } catch (e: Exception) { Result.Error(e.message ?: "Network error") }
    }

    suspend fun closeSession(sessionId: String): Result<SessionDto> {
        return try {
            val response = apiService.closeSession(sessionId)
            if (response.isSuccessful) Result.Success(response.body()!!.data)
            else {
                val msg = try { Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error } catch (e: Exception) { "Error ${response.code()}" }
                Result.Error(msg, response.code())
            }
        } catch (e: Exception) { Result.Error(e.message ?: "Network error") }
    }

    suspend fun getSessions(): Result<List<SessionDto>> {
        return try {
            val response = apiService.getSessions()
            if (response.isSuccessful) Result.Success(response.body()!!.data)
            else {
                val msg = try { Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error } catch (e: Exception) { "Error ${response.code()}" }
                Result.Error(msg, response.code())
            }
        } catch (e: Exception) { Result.Error(e.message ?: "Network error") }
    }

    suspend fun validateAndRecord(
        encryptedToken: String,
        sessionId: String,
        courseName: String,
        courseCode: String
    ): Result<ValidateNfcResponse> {
        return try {
            val response = apiService.validateNfc(
                ValidateNfcRequest(encryptedToken = encryptedToken, sessionId = sessionId, deviceInfo = mapOf("platform" to "android"))
            )
            if (response.isSuccessful) {
                val body = response.body()!!
                attendanceDao.insert(
                    AttendanceRecord(
                        localId = body.record.id, sessionId = sessionId,
                        studentId = body.student.id, studentName = body.student.fullName,
                        studentNumber = body.student.studentId, tappedAt = System.currentTimeMillis(),
                        status = "present", synced = true, courseName = courseName, courseCode = courseCode
                    )
                )
                Result.Success(body)
            } else {
                val msg = try { Gson().fromJson(response.errorBody()?.string(), ApiError::class.java).error } catch (e: Exception) { "Error ${response.code()}" }
                Result.Error(msg, response.code())
            }
        } catch (e: Exception) { Result.Error(e.message ?: "Network error") }
    }

    fun getSessionRecordsFlow(sessionId: String): Flow<List<AttendanceRecord>> = attendanceDao.getBySession(sessionId)
    fun getAllRecordsFlow(): Flow<List<AttendanceRecord>> = attendanceDao.getAll()
    fun getPendingSyncCount(): Flow<Int> = attendanceDao.getPendingSyncCount()

    suspend fun syncPendingRecords(onProgress: (Int) -> Unit = {}): Result<Int> {
        val unsynced = attendanceDao.getUnsynced()
        if (unsynced.isEmpty()) return Result.Success(0)
        onProgress(10)
        val dtos = unsynced.map { r -> SyncRecordDto(sessionId = r.sessionId, studentId = r.studentId, tappedAt = isoFormat.format(Date(r.tappedAt)), status = r.status) }
        onProgress(40)
        return try {
            val response = apiService.bulkSync(BulkSyncRequest(dtos))
            if (response.isSuccessful) {
                onProgress(80)
                val ids = unsynced.map { it.localId }
                attendanceDao.markSynced(ids)
                syncQueueDao.removeByIds(ids)
                onProgress(100)
                Result.Success(response.body()!!.results.synced)
            } else Result.Error("Sync failed (${response.code()})", response.code())
        } catch (e: Exception) { Result.Error(e.message ?: "Network error") }
    }
}