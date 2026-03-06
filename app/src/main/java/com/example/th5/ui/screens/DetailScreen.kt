package com.example.th5.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.th5.R

@Composable
fun DetailScreen(viewModel: BookViewModel) {
    val selectedBook by viewModel.selectedBook.collectAsState()

    selectedBook?.let { book ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Sử dụng ?. để truy cập an toàn vào volumeInfo
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
                    .height(300.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = book.volumeInfo?.title ?: "No Title",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Authors: ${book.volumeInfo?.authors?.joinToString() ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = book.volumeInfo?.description ?: "No description available.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
