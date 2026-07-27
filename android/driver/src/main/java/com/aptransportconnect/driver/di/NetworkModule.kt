package com.aptransportconnect.driver.di

import com.aptransportconnect.driver.BuildConfig
import com.aptransportconnect.driver.data.remote.api.DriverApiService
import com.aptransportconnect.driver.data.remote.interceptor.AuthInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @Named("authlessOkHttp")
    fun provideAuthlessOkHttp(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

    @Provides
    @Singleton
    @Named("authlessRetrofit")
    fun provideAuthlessRetrofit(gson: Gson, @Named("authlessOkHttp") client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()

    @Provides
    @Singleton
    @Named("authlessApi")
    fun provideAuthlessApi(@Named("authlessRetrofit") retrofit: Retrofit): DriverApiService = retrofit.create(DriverApiService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(authInterceptor).addInterceptor(loggingInterceptor).build()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson)).build()

    @Provides
    @Singleton
    fun provideDriverApi(retrofit: Retrofit): DriverApiService = retrofit.create(DriverApiService::class.java)
}
