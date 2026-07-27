package com.aptransportconnect.utils

import android.content.Context
import androidx.room.Room
import com.aptransportconnect.data.local.AppDatabase
import com.aptransportconnect.data.remote.service.ApiService
import com.aptransportconnect.data.repository.SessionStore
import com.aptransportconnect.data.repository.TransportRepository
import com.aptransportconnect.data.repository.TransportRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    @Volatile
    private var repository: TransportRepository? = null

    fun repository(context: Context): TransportRepository {
        return repository ?: synchronized(this) {
            repository ?: createRepository(context.applicationContext).also { repository = it }
        }
    }

    private fun createRepository(context: Context): TransportRepository {
        val sessionStore = SessionStore(context)
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { sessionStore.authToken.first() }
            val request = chain.request().newBuilder().apply {
                if (token.isNotBlank()) {
                    addHeader("Authorization", "******")
                }
            }.build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .build()

        val api = Retrofit.Builder()
            .baseUrl("https://example.com/api/") // TODO: Replace with backend base URL.
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)

        val db = Room.databaseBuilder(context, AppDatabase::class.java, "transport-connect.db").build()

        return TransportRepositoryImpl(
            api = api,
            bookingDao = db.bookingDao(),
            notificationDao = db.notificationDao(),
            chatDao = db.chatDao(),
            sessionStore = sessionStore,
        )
    }
}
