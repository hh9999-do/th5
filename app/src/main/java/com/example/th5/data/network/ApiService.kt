package com.example.th5.data.network

import com.example.th5.data.model.BookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("volumes")
    suspend fun getBooks(@Query("q") query: String): BookResponse
}
