package com.pavel.tecchnonextkotlinassesment.ui.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    pager: Flow<PagingData<PostEntity>>,
    searchQueryFlow: StateFlow<String>,
    searchResultsFlow: Flow<List<PostEntity>>,
    onSearch: (String) -> Unit,
    onToggleFav: (Int) -> Unit,
    onOpenFavourites: () -> Unit,
    favouritesFlow: Flow<Set<Int>>
) {
    val pagingItems = pager.collectAsLazyPagingItems()
    val favourites by favouritesFlow.collectAsState(initial = emptySet())
    val searchQuery by searchQueryFlow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Posts") },
                actions = {
                    IconButton(onClick = onOpenFavourites) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Favourites")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearch,
                label = { Text("Search title or body") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
            if (searchQuery.isNotBlank()) {
                // show search results
                SearchResultsList(
                    query = searchQuery,
                    resultsFlow = searchResultsFlow,
                    onToggleFav = onToggleFav,
                    favourites = favourites
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    // Show posts
                    items(
                        count = pagingItems.itemCount,
                        key = { index -> pagingItems[index]?.id ?: index }
                    ) { index ->
                        val post = pagingItems[index]
                        post?.let {
                            PostRow(
                                post = it,
                                isFavourite = favourites.contains(it.id),
                                onToggleFav = { onToggleFav(it.id) }
                            )
                        }
                    }

                    // Append/load more state
                    when (val state = pagingItems.loadState.append) {
                        is LoadState.Loading -> item {
                            Box(Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is LoadState.Error -> item {
                            Text(
                                "Error: ${state.error.localizedMessage}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    query: String,
    resultsFlow: Flow<List<PostEntity>>,
    onToggleFav: (Int) -> Unit,
    favourites: Set<Int>
) {
    val results by resultsFlow.collectAsState(initial = emptyList())

    Column {
        Text(
            "Results for \"$query\"",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )

        if (results.isEmpty()) {
            Text(
                "No results found",
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn {
                items(results.size, key = { index -> results[index].id }) { index ->
                    val post = results[index]
                    PostRow(
                        post = post,
                        isFavourite = favourites.contains(post.id),
                        onToggleFav = { onToggleFav(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PostRow(post: PostEntity, isFavourite: Boolean, onToggleFav: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(post.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(post.body, maxLines = 2, overflow = TextOverflow.Ellipsis)
        },
        trailingContent = {
            IconButton(onClick = onToggleFav) {
                if (isFavourite) Icon(Icons.Filled.Favorite, contentDescription = "Unfavourite")
                else Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favourite")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    Divider()
}