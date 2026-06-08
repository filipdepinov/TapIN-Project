package com.tapin.teacher.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.tapin.teacher.data.repository.AttendanceRepository;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<AttendanceRepository> repositoryProvider;

  public SyncWorker_Factory(Provider<AttendanceRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public SyncWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, repositoryProvider.get());
  }

  public static SyncWorker_Factory create(Provider<AttendanceRepository> repositoryProvider) {
    return new SyncWorker_Factory(repositoryProvider);
  }

  public static SyncWorker newInstance(Context context, WorkerParameters workerParams,
      AttendanceRepository repository) {
    return new SyncWorker(context, workerParams, repository);
  }
}
