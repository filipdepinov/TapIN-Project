package com.tapin.student.ui.login;

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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<ApiService> apiServiceProvider;

  private final Provider<SessionDataStore> sessionDataStoreProvider;

  public LoginViewModel_Factory(Provider<ApiService> apiServiceProvider,
      Provider<SessionDataStore> sessionDataStoreProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.sessionDataStoreProvider = sessionDataStoreProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(apiServiceProvider.get(), sessionDataStoreProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<ApiService> apiServiceProvider,
      Provider<SessionDataStore> sessionDataStoreProvider) {
    return new LoginViewModel_Factory(apiServiceProvider, sessionDataStoreProvider);
  }

  public static LoginViewModel newInstance(ApiService apiService,
      SessionDataStore sessionDataStore) {
    return new LoginViewModel(apiService, sessionDataStore);
  }
}
