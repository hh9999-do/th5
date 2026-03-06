package com.example.th5.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.th5.R
import com.example.th5.data.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    bookUiState: BookUiState,
    retryAction: () -> Unit,
    viewModel: BookViewModel,
    onBookClicked: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val categories = listOf("Computer Science", "Mathematics", "Informatics")

    Column(modifier = modifier.fillMaxWidth()) {
        // Search Bar
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.getBooks()
                    focusManager.clearFocus()
                }
            ),
            label = { Text("Search Books") },
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.getBooks()
                    focusManager.clearFocus()
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Majors Menu (Categories)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = viewModel.searchQuery == category,
                    onClick = {
                        viewModel.updateSearchQuery(category)
                        viewModel.getBooks()
                        focusManager.clearFocus()
                    },
                    label = { Text(category) }
                )
            }
        }

        // UI State (Loading, Success, Error)
        when (bookUiState) {
            is BookUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
            is BookUiState.Success -> {
                if (bookUiState.books.isEmpty()) {
                    NoResultsScreen(modifier = Modifier.fillMaxSize())
                } else {
                    BooksGridScreen(
                        books = bookUiState.books,
                        onBookClicked = onBookClicked,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            is BookUiState.Error -> ErrorScreen(
                retryAction = retryAction,
                errorMessage = bookUiState.message, // Truyền thông báo lỗi cụ thể
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun NoResultsScreen(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Text("No results found. Try a different keyword.")
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        // Hiển thị nội dung lỗi cụ thể (ví dụ: Too many requests)
        Text(text = errorMessage, modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun BookCard(book: Book, onBookClicked: (Book) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onBookClicked(book) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val imageUrl = book.volumeInfo?.imageLinks?.thumbnail?.replace("http:", "https:")
            
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = book.volumeInfo?.title,
                placeholder = painterResource(R.drawable.ic_connection_error),
                error = painterResource(R.drawable.ic_connection_error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            book.volumeInfo?.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BooksGridScreen(books: List<Book>, onBookClicked: (Book) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = books, key = { book -> book.id }) {
            BookCard(
                book = it,
                onBookClicked = onBookClicked
            )
        }
    }
}
