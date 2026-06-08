package com.tapin.teacher.data.repository;

import com.tapin.teacher.data.local.dao.AttendanceDao;
import com.tapin.teacher.data.local.dao.SyncQueueDao;
import com.tapin.teacher.data.remote.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AttendanceRepository_Factory implements Factory<AttendanceRepository> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<AttendanceDao> attendanceDaoProvider;

  private final Provider<SyncQueueDao> syncQueueDaoProvider;

  public AttendanceRepository_Factory(Provider<ApiService> apiServiceProvider,
      Provider<AttendanceDao> attendanceDaoProvider, Provider<SyncQueueDao> syncQueueDaoProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.attendanceDaoProvider = attendanceDaoProvider;
    this.syncQueueDaoProvider = syncQueueDaoProvider;
  }

  @Override
  public AttendanceRepository get() {
    return newInstance(apiServiceProvider.get(), attendanceDaoProvider.get(), syncQueueDaoProvider.get());
  }

  public static AttendanceRepository_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<AttendanceDao> attendanceDaoProvider, Provider<SyncQueueDao> syncQueueDaoProvider) {
    return new AttendanceRepository_Factory(apiServiceProvider, attendanceDaoProvider, syncQueueDaoProvider);
  }

  public static AttendanceRepository newInstance(ApiService apiService, AttendanceDao attendanceDao,
      SyncQueueDao syncQueueDao) {
    return new AttendanceRepository(apiService, attendanceDao, syncQueueDao);
  }
}
