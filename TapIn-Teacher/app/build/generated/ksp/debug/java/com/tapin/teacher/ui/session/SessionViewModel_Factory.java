package com.tapin.teacher.ui.session;

import androidx.lifecycle.SavedStateHandle;
import com.tapin.teacher.data.repository.AttendanceRepository;
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
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<AttendanceRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public SessionViewModel_Factory(Provider<AttendanceRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static SessionViewModel_Factory create(Provider<AttendanceRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new SessionViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static SessionViewModel newInstance(AttendanceRepository repository,
      SavedStateHandle savedStateHandle) {
    return new SessionViewModel(repository, savedStateHandle);
  }
}
