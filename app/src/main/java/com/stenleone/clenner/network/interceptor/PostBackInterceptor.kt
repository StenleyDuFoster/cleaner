package com.stenleone.clenner.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class PostBackInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val build: Request = chain.request().newBuilder().addHeader("X-Requested-With", "XMLHttpRequest").build()
//            val nanoTime = System.nanoTime()
//            var i = 0
        println(String.format("Sending request %s on %n%s", *arrayOf<Any>(build.url(), build.headers())))
//            var proceed: Response = chain.proceed(build)
//            val nanoTime2 = System.nanoTime()
//            val printStream: PrintStream = System.out
//            val d = (nanoTime2 - nanoTime).toDouble()
//            java.lang.Double.isNaN(d)
//            printStream.println(String.format("Received response for %s in %.1fms%n%s", *arrayOf<Any>(proceed.request().url(), java.lang.Double.valueOf(d / 1000000.0), proceed.headers())))
//            while (!proceed.isSuccessful() && i < 3) {
//                Log.d("intercept", "Request is not successful - $i")
//                i++
//                proceed = chain
//            }
        return chain.proceed(build)
    }

}