package com.example.th5.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val items: List<Book>? = null
)

@Serializable
data class Book(
    val id: String,
    // Cho phép volumeInfo null nếu Google gặp lỗi dữ liệu
    val volumeInfo: VolumeInfo? = null
)

@Serializable
data class VolumeInfo(
    val title: String? = "No Title",
    val authors: List<String>? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null
)

@Serializable
data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
)
