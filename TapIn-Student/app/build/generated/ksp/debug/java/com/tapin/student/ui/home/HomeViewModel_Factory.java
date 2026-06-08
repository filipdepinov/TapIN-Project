package com.tapin.student.ui.home;

import com.tapin.student.data.local.SessionDataStore;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<SessionDataStore> sessionDataStoreProvider;

  public HomeViewModel_Factory(Provider<ApiService> apiServiceProvider,
      Provider<SessionDataStore> sessionDataStoreProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.sessionDataStoreProvider = sessionDataStoreProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(apiServiceProvider.get(), sessionDataStoreProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<SessionDataStore> sessionDataStoreProvider) {
    return new HomeViewModel_Factory(apiServiceProvider, sessionDataStoreProvider);
  }

  public static HomeViewModel newInstance(ApiService apiService,
      SessionDataStore sessionDataStore) {
    return new HomeViewModel(apiService, sessionDataStore);
  }
}
