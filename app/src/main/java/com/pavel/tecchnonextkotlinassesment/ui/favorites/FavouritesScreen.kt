package com.pavel.tecchnonextkotlinassesment.ui.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import com.pavel.tecchnonextkotlinassesment.ui.posts.PostRow
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    postsFlow: Flow<List<PostEntity>>,
    onBack: () -> Unit,
    onToggleFav: (Int) -> Unit
) {
    val posts by postsFlow.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favourites") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        if (posts.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize()) { Text("No favourites yet", modifier = Modifier.padding(16.dp)) }
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(posts.size) { idx ->
                    val p = posts[idx]
                    PostRow(p, true) { onToggleFav(p.id) }
                }
            }
        }
    }
}