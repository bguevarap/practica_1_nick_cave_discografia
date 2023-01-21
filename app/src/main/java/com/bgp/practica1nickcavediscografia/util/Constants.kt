package com.bgp.practica1nickcavediscografia.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Constants{
    const val BASE_URL = "https://private-64d59-nickcave.apiary-mock.com/"
    const val LOGTAG = "LOGS"

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



}