package com.stenleone.clenner.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    companion object {
        const val SOURCE = "click.php"
        const val POSTBACK = "postback"
    }

    @GET("")
    suspend fun postUserAppOpen(@Url url: String): retrofit2.Response<ResponseBody>

    @GET("")
    suspend fun sendSentry(@Url url: String): retrofit2.Response<ResponseBody>

    @GET("")
    suspend fun sendSource(@Url url: String): retrofit2.Response<ResponseBody>

}