package com.stenleone.clenner.util.extencial

import retrofit2.Response

fun <T> Response<T>.isSuccessOr404(): Boolean {
    return this.isSuccessful || this.code() == 404
}