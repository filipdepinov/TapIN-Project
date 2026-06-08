package com.tapin.teacher.ui;

import com.tapin.teacher.data.local.SessionDataStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SplashActivity_MembersInjector implements MembersInjector<SplashActivity> {
  private final Provider<SessionDataStore> sessionDataStoreProvider;

  public SplashActivity_MembersInjector(Provider<SessionDataStore> sessionDataStoreProvider) {
    this.sessionDataStoreProvider = sessionDataStoreProvider;
  }

  public static MembersInjector<SplashActivity> create(
      Provider<SessionDataStore> sessionDataStoreProvider) {
    return new SplashActivity_MembersInjector(sessionDataStoreProvider);
  }

  @Override
  public void injectMembers(SplashActivity instance) {
    injectSessionDataStore(instance, sessionDataStoreProvider.get());
  }

  @InjectedFieldSignature("com.tapin.teacher.ui.SplashActivity.sessionDataStore")
  public static void injectSessionDataStore(SplashActivity instance,
      SessionDataStore sessionDataStore) {
    instance.sessionDataStore = sessionDataStore;
  }
}
