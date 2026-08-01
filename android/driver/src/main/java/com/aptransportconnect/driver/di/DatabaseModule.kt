package com.aptransportconnect.driver.di

import android.content.Context
import androidx.room.Room
import com.aptransportconnect.driver.data.local.database.DriverDatabase
import com.aptransportconnect.driver.data.local.database.dao.DocumentDao
import com.aptransportconnect.driver.data.local.database.dao.NotificationDao
import com.aptransportconnect.driver.data.local.database.dao.TransactionDao
import com.aptransportconnect.driver.data.local.database.dao.TripDao
import com.aptransportconnect.driver.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DriverDatabase =
        Room.databaseBuilder(context, DriverDatabase::class.java, Constants.DRIVER_DATABASE)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideTripDao(db: DriverDatabase): TripDao = db.tripDao()
    @Provides fun provideNotificationDao(db: DriverDatabase): NotificationDao = db.notificationDao()
    @Provides fun provideTransactionDao(db: DriverDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideDocumentDao(db: DriverDatabase): DocumentDao = db.documentDao()
}
