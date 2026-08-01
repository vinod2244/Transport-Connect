package com.aptransportconnect.driver.di

import com.aptransportconnect.driver.data.repository.AuthRepositoryImpl
import com.aptransportconnect.driver.data.repository.DocumentRepositoryImpl
import com.aptransportconnect.driver.data.repository.EarningsRepositoryImpl
import com.aptransportconnect.driver.data.repository.LocationRepositoryImpl
import com.aptransportconnect.driver.data.repository.NotificationRepositoryImpl
import com.aptransportconnect.driver.data.repository.ProfileRepositoryImpl
import com.aptransportconnect.driver.data.repository.TripRepositoryImpl
import com.aptransportconnect.driver.data.repository.WalletRepositoryImpl
import com.aptransportconnect.driver.domain.repository.AuthRepository
import com.aptransportconnect.driver.domain.repository.DocumentRepository
import com.aptransportconnect.driver.domain.repository.EarningsRepository
import com.aptransportconnect.driver.domain.repository.LocationRepository
import com.aptransportconnect.driver.domain.repository.NotificationRepository
import com.aptransportconnect.driver.domain.repository.ProfileRepository
import com.aptransportconnect.driver.domain.repository.TripRepository
import com.aptransportconnect.driver.domain.repository.WalletRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository
    @Binds @Singleton abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository
    @Binds @Singleton abstract fun bindEarningsRepository(impl: EarningsRepositoryImpl): EarningsRepository
    @Binds @Singleton abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
    @Binds @Singleton abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository
    @Binds @Singleton abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
    @Binds @Singleton abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository
}
