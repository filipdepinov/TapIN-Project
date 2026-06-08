package com.tapin.teacher.data.remote.api

import com.tapin.teacher.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("courses")
    suspend fun getCourses(): Response<CoursesResponse>

    @POST("sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<SessionResponse>

    @PATCH("sessions/{id}/close")
    suspend fun closeSession(@Path("id") sessionId: String): Response<SessionResponse>

    @GET("sessions")
    suspend fun getSessions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<SessionsResponse>

    @GET("sessions/{id}")
    suspend fun getSession(@Path("id") sessionId: String): Response<SessionResponse>

    @POST("nfc/validate")
    suspend fun validateNfc(@Body request: ValidateNfcRequest): Response<ValidateNfcResponse>

    @POST("attendance")
    suspend fun bulkSync(@Body request: BulkSyncRequest): Response<BulkSyncResponse>
}
