package com.example.getmyenglishname.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import java.util.UUID
import kotlin.text.Charsets.UTF_8

class BaiduTranslateClient(
    val appId: String,
    val appSecret: String
) {
    private val api: BaiduTranslateApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://fanyi-api.baidu.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(BaiduTranslateApi::class.java)
    }

    suspend fun translateToEnglish(text: String): String {
        val salt = UUID.randomUUID().toString()
        val sign = generateSign(text, salt)
        
        return try {
            val response = api.translate(
                query = text,
                appId = appId,
                salt = salt,
                sign = sign
            )
            response.trans_result.firstOrNull()?.dst ?: text
        } catch (e: Exception) {
            e.printStackTrace()
            text
        }
    }

    private fun generateSign(text: String, salt: String): String {
        val signStr = "$appId$text$salt$appSecret"
        return MessageDigest.getInstance("MD5")
            .digest(signStr.toByteArray(UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
} 