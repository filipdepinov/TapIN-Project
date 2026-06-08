package com.tapin.teacher;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class TapInTeacherApp_MembersInjector implements MembersInjector<TapInTeacherApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public TapInTeacherApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<TapInTeacherApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new TapInTeacherApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(TapInTeacherApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.tapin.teacher.TapInTeacherApp.workerFactory")
  public static void injectWorkerFactory(TapInTeacherApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
