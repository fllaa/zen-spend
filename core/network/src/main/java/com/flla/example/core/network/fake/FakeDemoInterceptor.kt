package com.flla.example.core.network.fake

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class FakeDemoInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (request.url.host != DEMO_HOST) return chain.proceed(request)

            val body =
                when (request.url.encodedPath) {
                    "/auth/login",
                    "/auth/register",
                    "/auth/refresh",
                    -> AUTH_BODY
                    "/me" -> USER_BODY
                    "/auth/logout" -> ""
                    else -> ERROR_BODY
                }
            val code =
                if (body == ERROR_BODY) {
                    404
                } else if (request.url.encodedPath == "/auth/logout") {
                    204
                } else {
                    200
                }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(code)
                .message(if (code in 200..299) "OK" else "Not Found")
                .body(body.toResponseBody(JSON))
                .build()
        }

        private companion object {
            const val DEMO_HOST = "demo.example.local"
            val JSON = "application/json".toMediaType()
            const val USER_BODY =
                """{"id":"demo-user","name":"Demo User","email":"demo@example.com","avatar_url":null}"""
            val AUTH_BODY =
                """{"user":$USER_BODY,"tokens":""" +
                    """{"access_token":"demo-access-token","refresh_token":"demo-refresh-token"}}"""
            const val ERROR_BODY = """{"message":"Not found"}"""
        }
    }
