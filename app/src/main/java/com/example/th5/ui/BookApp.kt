package com.example.th5.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.th5.ui.screens.BookViewModel
import com.example.th5.ui.screens.DetailScreen
import com.example.th5.ui.screens.HomeScreen

enum class BookScreen(val title: String) {
    Home("UED Research Repository"),
    Detail("Book Detail")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookApp(viewModel: BookViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = BookScreen.valueOf(
        backStackEntry?.destination?.route ?: BookScreen.Home.name
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BookScreen.Home.name,
            modifier = Modifier.padding(it)
        ) {
            composable(BookScreen.Home.name) {
                HomeScreen(
                    bookUiState = viewModel.bookUiState,
                    retryAction = viewModel::getBooks,
                    viewModel = viewModel,
                    onBookClicked = {
                        viewModel.selectBook(it)
                        navController.navigate(BookScreen.Detail.name)
                    }
                )
            }
            composable(BookScreen.Detail.name) {
                DetailScreen(viewModel = viewModel)
            }
        }
    }
}
