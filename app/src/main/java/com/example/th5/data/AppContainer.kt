package com.example.th5.data

import android.content.Context
import com.example.th5.data.local.BookDatabase
import com.example.th5.data.network.ApiService
import com.example.th5.data.repository.BookRepository
import com.example.th5.data.repository.CachingBookRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://www.googleapis.com/books/v1/"

    private val json = Json { ignoreUnknownKeys = true }

    // Cấu hình OkHttpClient với thời gian chờ (Timeout) tối đa là 30 giây
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Thời gian tối đa để thiết lập kết nối
        .readTimeout(30, TimeUnit.SECONDS)    // Thời gian tối đa để nhận dữ liệu trả về
        .writeTimeout(30, TimeUnit.SECONDS)   // Thời gian tối đa để gửi yêu cầu đi
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .client(okHttpClient) // Áp dụng cấu hình Timeout 30s
        .build()

    private val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private val database: BookDatabase by lazy {
        BookDatabase.getDatabase(context)
    }

    override val bookRepository: BookRepository by lazy {
        CachingBookRepository(database.bookDao(), retrofitService)
    }
}
