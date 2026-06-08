package com.tapin.teacher.di;

import com.tapin.teacher.data.local.TapInDatabase;
import com.tapin.teacher.data.local.dao.AttendanceDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideAttendanceDaoFactory implements Factory<AttendanceDao> {
  private final Provider<TapInDatabase> dbProvider;

  public AppModule_ProvideAttendanceDaoFactory(Provider<TapInDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AttendanceDao get() {
    return provideAttendanceDao(dbProvider.get());
  }

  public static AppModule_ProvideAttendanceDaoFactory create(Provider<TapInDatabase> dbProvider) {
    return new AppModule_ProvideAttendanceDaoFactory(dbProvider);
  }

  public static AttendanceDao provideAttendanceDao(TapInDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAttendanceDao(db));
  }
}
