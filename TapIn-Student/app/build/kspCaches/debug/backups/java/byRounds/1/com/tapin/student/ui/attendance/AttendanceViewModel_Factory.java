package com.tapin.student.ui.attendance;

import com.tapin.student.data.remote.api.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AttendanceViewModel_Factory implements Factory<AttendanceViewModel> {
  private final Provider<ApiService> apiServiceProvider;

  public AttendanceViewModel_Factory(Provider<ApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public AttendanceViewModel get() {
    return newInstance(apiServiceProvider.get());
  }

  public static AttendanceViewModel_Factory create(Provider<ApiService> apiServiceProvider) {
    return new AttendanceViewModel_Factory(apiServiceProvider);
  }

  public static AttendanceViewModel newInstance(ApiService apiService) {
    return new AttendanceViewModel(apiService);
  }
}
