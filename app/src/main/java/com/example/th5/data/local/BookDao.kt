package com.example.th5.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<LocalBook>)

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%'")
    fun getBooksByQuery(query: String): Flow<List<LocalBook>>

    @Query("DELETE FROM books")
    suspend fun clearBooks()
}
