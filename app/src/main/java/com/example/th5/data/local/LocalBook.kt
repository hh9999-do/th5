package com.example.th5.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class LocalBook(
    @PrimaryKey val id: String,
    val title: String,
    val authors: String, // Stored as comma-separated string
    val description: String,
    val thumbnailUrl: String
)
