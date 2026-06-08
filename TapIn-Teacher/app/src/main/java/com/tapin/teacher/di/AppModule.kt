package com.tapin.teacher.di

import android.content.Context
import androidx.room.Room
import com.tapin.teacher.BuildConfig
import com.tapin.teacher.data.local.SessionDataStore
import com.tapin.teacher.data.local.TapInDatabase
import com.tapin.teacher.data.local.dao.AttendanceDao
import com.tapin.teacher.data.local.dao.SyncQueueDao
import com.tapin.teacher.data.remote.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Room ──────────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): TapInDatabase =
        Room.databaseBuilder(ctx, TapInDatabase::class.java, "tapin_teacher.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideAttendanceDao(db: TapInDatabase): AttendanceDao = db.attendanceDao()
    @Provides fun provideSyncQueueDao(db: TapInDatabase): SyncQueueDao  = db.syncQueueDao()

    // ── Network ───────────────────────────────────────────────

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionDataStore: SessionDataStore): Interceptor =
        Interceptor { chain ->
            val token = runBlocking { sessionDataStore.jwtToken.firstOrNull() }
            val req = if (token != null)
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            else chain.request()
            chain.proceed(req)
        }

    @Provides
    @Singleton
    fun provideOkHttp(authInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
