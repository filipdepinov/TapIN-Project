package com.tapin.teacher.di;

import com.tapin.teacher.data.local.SessionDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.Interceptor;

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
public final class AppModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<SessionDataStore> sessionDataStoreProvider;

  public AppModule_ProvideAuthInterceptorFactory(
      Provider<SessionDataStore> sessionDataStoreProvider) {
    this.sessionDataStoreProvider = sessionDataStoreProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(sessionDataStoreProvider.get());
  }

  public static AppModule_ProvideAuthInterceptorFactory create(
      Provider<SessionDataStore> sessionDataStoreProvider) {
    return new AppModule_ProvideAuthInterceptorFactory(sessionDataStoreProvider);
  }

  public static Interceptor provideAuthInterceptor(SessionDataStore sessionDataStore) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAuthInterceptor(sessionDataStore));
  }
}
