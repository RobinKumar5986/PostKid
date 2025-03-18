package com.kgJr.posKid.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class ApiHandler {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30,TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun requestBuilder(url: String,methodType: ApiMethodType,body: String?): Request{
        val request = Request.Builder()
            .url(url)

        val requestBody = body?.toRequestBody("application/json; charset=utf-8".toMediaType())
        when (methodType) {
            ApiMethodType.GET -> {
                request.get()
            }
            ApiMethodType.POST -> {
                if (requestBody != null) {
                    request.post(requestBody)
                } else {
                    throw IllegalArgumentException("POST request requires a body")
                }
            }
            ApiMethodType.PUT -> {
                if (requestBody != null) {
                    request.put(requestBody)
                } else {
                    throw IllegalArgumentException("PUT request requires a body")
                }
            }
            ApiMethodType.DELETE -> {
                request.delete(requestBody)
            }
        }

        return request.build()
    }

    fun callRequest(url: String, methodType: ApiMethodType, body: String?): String?{
        try {
            val request = this.requestBuilder(url = url, methodType = methodType,body = body)
            client.newCall(request).execute().use { response ->
                return if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    "Error: HTTP ${response.code} - ${response.message}"
                }
            }
            return ""
        }catch (e: Exception){
            return "Error: ${e.message}"
        }
    }
}

enum class ApiMethodType{
    GET,
    POST,
    PUT,
    DELETE
}