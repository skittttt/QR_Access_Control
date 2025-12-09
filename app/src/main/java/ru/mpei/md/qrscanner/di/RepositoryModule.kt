package ru.mpei.md.qrscanner.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import ru.mpei.md.qrscanner.data.repository.AccessRepository
import ru.mpei.md.qrscanner.data.repository.MockAccessRepository
import ru.mpei.md.qrscanner.domain.usecases.*

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    fun provideAccessRepository(): AccessRepository {
        return MockAccessRepository()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    
    @Provides
    fun provideGetUserEventsUseCase(repository: AccessRepository): GetUserEventsUseCase {
        return GetUserEventsUseCase(repository)
    }
    
    @Provides
    fun provideGetEventDetailsUseCase(repository: AccessRepository): GetEventDetailsUseCase {
        return GetEventDetailsUseCase(repository)
    }
    
    @Provides
    fun provideGenerateQrCodeUseCase(): GenerateQrCodeUseCase {
        return GenerateQrCodeUseCase()
    }
    
    @Provides
    fun provideValidateQrCodeUseCase(repository: AccessRepository): ValidateQrCodeUseCase {
        return ValidateQrCodeUseCase(repository)
    }
    
    @Provides
    fun provideProcessAccessDecisionUseCase(repository: AccessRepository): ProcessAccessDecisionUseCase {
        return ProcessAccessDecisionUseCase(repository)
    }

    @Provides
    fun provideValidateAccessTicketUseCase(repository: AccessRepository): ValidateAccessTicketUseCase {
        return ValidateAccessTicketUseCase(repository)
    }
}