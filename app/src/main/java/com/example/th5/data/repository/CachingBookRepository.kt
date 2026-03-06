package com.example.th5.data.repository

import com.example.th5.data.local.BookDao
import com.example.th5.data.local.LocalBook
import com.example.th5.data.model.Book
import com.example.th5.data.network.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CachingBookRepository(
    private val bookDao: BookDao,
    private val apiService: ApiService
) : BookRepository {
    override suspend fun getBooks(query: String): List<Book> {
        return try {
            val remoteBooks = apiService.getBooks(query).items ?: emptyList()
            
            if (remoteBooks.isNotEmpty()) {
                val localBooks = remoteBooks.map { it.toLocalBook() }
                bookDao.insertBooks(localBooks)
            }
            remoteBooks
        } catch (e: Exception) {
            val localData = bookDao.getBooksByQuery(query).map { it.toBook() }.first()
            if (localData.isEmpty()) {
                throw e
            }
            localData
        }
    }

    private fun Book.toLocalBook(): LocalBook {
        return LocalBook(
            id = this.id,
            // Xử lý an toàn khi volumeInfo hoặc title bị null
            title = this.volumeInfo?.title ?: "No Title",
            authors = this.volumeInfo?.authors?.joinToString() ?: "N/A",
            description = this.volumeInfo?.description ?: "No description available.",
            thumbnailUrl = this.volumeInfo?.imageLinks?.thumbnail?.replace("http:", "https:") ?: ""
        )
    }

    private fun List<LocalBook>.toBook(): List<Book> {
        return this.map { localBook ->
            Book(
                id = localBook.id,
                volumeInfo = com.example.th5.data.model.VolumeInfo(
                    title = localBook.title,
                    authors = localBook.authors.split(","),
                    description = localBook.description,
                    imageLinks = com.example.th5.data.model.ImageLinks("", localBook.thumbnailUrl)
                )
            )
        }
    }
}
