package com.example.th5.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.th5.BookApplication
import com.example.th5.data.model.Book
import com.example.th5.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface BookUiState {
    data class Success(val books: List<Book>) : BookUiState
    data class Error(val message: String = "Failed to load") : BookUiState
    object Loading : BookUiState
}

class BookViewModel(private val bookRepository: BookRepository) : ViewModel() {
    var bookUiState: BookUiState by mutableStateOf(BookUiState.Loading)
        private set

    var searchQuery by mutableStateOf("Computer Science")
        private set

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            bookUiState = BookUiState.Loading
            bookUiState = try {
                BookUiState.Success(bookRepository.getBooks(searchQuery))
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    BookUiState.Error("Too many requests. Please wait 2-5 minutes.")
                } else {
                    BookUiState.Error("Server error: ${e.code()}")
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching books: ${e.message}", e)
                BookUiState.Error("Network error. Please check connection.")
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookApplication)
                val bookRepository = application.container.bookRepository
                BookViewModel(bookRepository = bookRepository)
            }
        }
    }
}
