package com.tapin.teacher.ui.home;

import android.content.Context;
import com.tapin.teacher.data.local.SessionDataStore;
import com.tapin.teacher.data.repository.AttendanceRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<AttendanceRepository> repositoryProvider;

  private final Provider<SessionDataStore> sessionDataStoreProvider;

  private final Provider<Context> contextProvider;

  public HomeViewModel_Factory(Provider<AttendanceRepository> repositoryProvider,
      Provider<SessionDataStore> sessionDataStoreProvider, Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.sessionDataStoreProvider = sessionDataStoreProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), sessionDataStoreProvider.get(), contextProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<AttendanceRepository> repositoryProvider,
      Provider<SessionDataStore> sessionDataStoreProvider, Provider<Context> contextProvider) {
    return new HomeViewModel_Factory(repositoryProvider, sessionDataStoreProvider, contextProvider);
  }

  public static HomeViewModel newInstance(AttendanceRepository repository,
      SessionDataStore sessionDataStore, Context context) {
    return new HomeViewModel(repository, sessionDataStore, context);
  }
}
