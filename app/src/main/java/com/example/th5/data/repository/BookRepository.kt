package com.example.th5.data.repository

import com.example.th5.data.model.Book
import com.example.th5.data.network.ApiService

interface BookRepository {
    suspend fun getBooks(query: String): List<Book>
}

class NetworkBookRepository(private val apiService: ApiService) : BookRepository {
    override suspend fun getBooks(query: String): List<Book> {
        return apiService.getBooks(query).items ?: emptyList()
    }
}
