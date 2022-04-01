package com.example.paging3_unspalsh.screens

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.paging3_unspalsh.navigation.Screen
import com.example.paging3_unspalsh.ui.theme.topAppBarBackgroundColor
import com.example.paging3_unspalsh.ui.theme.topAppBarContentColor
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalPagingApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
){
    val getAllImages = homeViewModel.getAllImages.collectAsLazyPagingItems()
    
    Scaffold(
        topBar = {
            HomeTopBar(
                onSearchClicked = {
                    navController.navigate(Screen.Search.route)
                }
            )
        },
        content = {
            ListCommon(items = getAllImages)
        }
    )
}




@Composable
fun HomeTopBar(
    onSearchClicked: () -> Unit
){
    TopAppBar(
        title = {
            Text(
                text = "Home",
                color = MaterialTheme.colors.topAppBarContentColor
                )
        },
        backgroundColor = MaterialTheme.colors.topAppBarBackgroundColor,
        actions = {
            IconButton(onClick = onSearchClicked) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            }
        }
    )
}