package com.stenleone.clenner.network

import okhttp3.ResponseBody
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST("")
    suspend fun postUserAppOpen(@Url url: String): retrofit2.Response<ResponseBody>

}