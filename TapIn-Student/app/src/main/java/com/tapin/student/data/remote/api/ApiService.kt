package com.tapin.student.data.remote.api

import com.tapin.student.data.remote.dto.LoginRequest
import com.tapin.student.data.remote.dto.LoginResponse
import com.tapin.student.data.remote.dto.MyAttendanceResponse
import com.tapin.student.data.remote.dto.RefreshNfcTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/refresh-nfc-token")
    suspend fun refreshNfcToken(): Response<RefreshNfcTokenResponse>

    @GET("statistics/my-attendance")
    suspend fun getMyAttendance(): Response<MyAttendanceResponse>
}
