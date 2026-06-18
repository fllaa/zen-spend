package com.flla.example.core.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl
