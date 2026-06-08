package com.tapin.student.hce;

import com.tapin.student.data.local.SessionDataStore;
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
public final class TapInHceService_MembersInjector implements MembersInjector<TapInHceService> {
  private final Provider<SessionDataStore> sessionDataStoreProvider;

  public TapInHceService_MembersInjector(Provider<SessionDataStore> sessionDataStoreProvider) {
    this.sessionDataStoreProvider = sessionDataStoreProvider;
  }

  public static MembersInjector<TapInHceService> create(
      Provider<SessionDataStore> sessionDataStoreProvider) {
    return new TapInHceService_MembersInjector(sessionDataStoreProvider);
  }

  @Override
  public void injectMembers(TapInHceService instance) {
    injectSessionDataStore(instance, sessionDataStoreProvider.get());
  }

  @InjectedFieldSignature("com.tapin.student.hce.TapInHceService.sessionDataStore")
  public static void injectSessionDataStore(TapInHceService instance,
      SessionDataStore sessionDataStore) {
    instance.sessionDataStore = sessionDataStore;
  }
}
