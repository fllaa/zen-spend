@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.flla.example.core.network.di

import com.flla.example.core.common.AppConstants
import com.flla.example.core.network.api.AuthApi
import com.flla.example.core.network.api.UserApi
import com.flla.example.core.network.auth.AccessTokenInterceptor
import com.flla.example.core.network.auth.TokenRefreshAuthenticator
import com.flla.example.core.network.fake.FakeDemoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = AppConstants.DEMO_BASE_URL

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(
        fakeDemoInterceptor: FakeDemoInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(fakeDemoInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedOkHttpClient(
        accessTokenInterceptor: AccessTokenInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator,
        fakeDemoInterceptor: FakeDemoInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(accessTokenInterceptor)
            .authenticator(tokenRefreshAuthenticator)
            .addInterceptor(fakeDemoInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        json: Json,
        @AuthenticatedClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @RefreshApi
    fun provideRefreshAuthApi(
        @BaseUrl baseUrl: String,
        json: Json,
        @RefreshClient okHttpClient: OkHttpClient,
    ): AuthApi =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
}
