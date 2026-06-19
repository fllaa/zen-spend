package com.flla.zenspend.core.network.fake

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import javax.inject.Inject

class FakeDemoInterceptor
    @Inject
    constructor() : Interceptor {
        @Suppress("CyclomaticComplexMethod")
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (request.url.host != DEMO_HOST) return chain.proceed(request)

            val body =
                if (request.url.encodedPath == "/me" && request.method == "PUT") {
                    val requestBuffer = Buffer()
                    request.body?.writeTo(requestBuffer)
                    val requestBodyString = requestBuffer.readUtf8()
                    val nameRegex = """"name"\s*:\s*(?:"([^"]+)"|null)""".toRegex()
                    val emailRegex = """"email"\s*:\s*(?:"([^"]+)"|null)""".toRegex()
                    val phoneRegex = """"phone"\s*:\s*(?:"([^"]+)"|null)""".toRegex()
                    val name =
                        nameRegex.find(requestBodyString)
                            ?.groupValues?.get(1)?.takeIf { it.isNotEmpty() } ?: "Demo User"
                    val email =
                        emailRegex.find(requestBodyString)
                            ?.groupValues?.get(1)?.takeIf { it.isNotEmpty() } ?: "demo@example.com"
                    val phone =
                        phoneRegex.find(requestBodyString)
                            ?.groupValues?.get(1)?.takeIf { it.isNotEmpty() } ?: ""
                    val phoneJsonVal = if (phone.isEmpty()) "null" else "\"$phone\""
                    """{"id":"demo-user","name":"$name","email":"$email","phone":$phoneJsonVal,"avatar_url":null}"""
                } else {
                    when (request.url.encodedPath) {
                        "/auth/login",
                        "/auth/register",
                        "/auth/refresh",
                        -> AUTH_BODY
                        "/me" -> USER_BODY
                        "/auth/logout" -> ""
                        else -> ERROR_BODY
                    }
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
                """{"id":"demo-user","name":"Demo User","email":"demo@example.com","phone":null,"avatar_url":null}"""
            val AUTH_BODY =
                """{"user":$USER_BODY,"tokens":""" +
                    """{"access_token":"demo-access-token","refresh_token":"demo-refresh-token"}}"""
            const val ERROR_BODY = """{"message":"Not found"}"""
        }
    }
