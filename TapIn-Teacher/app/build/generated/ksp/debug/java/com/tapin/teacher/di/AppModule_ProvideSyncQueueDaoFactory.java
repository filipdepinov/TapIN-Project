package com.tapin.teacher.di;

import com.tapin.teacher.data.local.TapInDatabase;
import com.tapin.teacher.data.local.dao.SyncQueueDao;
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
public final class AppModule_ProvideSyncQueueDaoFactory implements Factory<SyncQueueDao> {
  private final Provider<TapInDatabase> dbProvider;

  public AppModule_ProvideSyncQueueDaoFactory(Provider<TapInDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SyncQueueDao get() {
    return provideSyncQueueDao(dbProvider.get());
  }

  public static AppModule_ProvideSyncQueueDaoFactory create(Provider<TapInDatabase> dbProvider) {
    return new AppModule_ProvideSyncQueueDaoFactory(dbProvider);
  }

  public static SyncQueueDao provideSyncQueueDao(TapInDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSyncQueueDao(db));
  }
}
