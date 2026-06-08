package com.tapin.teacher.di;

import android.content.Context;
import com.tapin.teacher.data.local.TapInDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideDatabaseFactory implements Factory<TapInDatabase> {
  private final Provider<Context> ctxProvider;

  public AppModule_ProvideDatabaseFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public TapInDatabase get() {
    return provideDatabase(ctxProvider.get());
  }

  public static AppModule_ProvideDatabaseFactory create(Provider<Context> ctxProvider) {
    return new AppModule_ProvideDatabaseFactory(ctxProvider);
  }

  public static TapInDatabase provideDatabase(Context ctx) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDatabase(ctx));
  }
}
