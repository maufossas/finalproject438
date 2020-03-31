package com.example.finalproject.Network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// api client for accessing themoviedb
object ApiClient {
    const val BASE_URL = "https://api.themoviedb.org/3/"

    fun makeRetrofitService(): MovieInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(MovieInterface::class.java)
    }
}