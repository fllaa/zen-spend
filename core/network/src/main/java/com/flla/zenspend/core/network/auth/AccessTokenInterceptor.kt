package com.flla.zenspend.core.network.auth

import com.flla.zenspend.core.datastore.AuthTokenDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AccessTokenInterceptor
    @Inject
    constructor(
        private val tokenDataSource: AuthTokenDataSource,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = runBlocking { tokenDataSource.tokens.first()?.accessToken }
            val request =
                if (token.isNullOrBlank()) {
                    chain.request()
                } else {
                    chain.request()
                        .newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                }
            return chain.proceed(request)
        }
    }
