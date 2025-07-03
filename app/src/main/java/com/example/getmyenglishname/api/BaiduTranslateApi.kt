package com.example.getmyenglishname.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BaiduTranslateApi {
    @FormUrlEncoded
    @POST("api/trans/vip/translate")
    suspend fun translate(
        @Field("q") query: String,
        @Field("from") from: String = "zh",
        @Field("to") to: String = "en",
        @Field("appid") appId: String,
        @Field("salt") salt: String,
        @Field("sign") sign: String
    ): TranslateResponse
}

data class TranslateResponse(
    val from: String,
    val to: String,
    val trans_result: List<TransResult>
)

data class TransResult(
    val src: String,
    val dst: String
) 