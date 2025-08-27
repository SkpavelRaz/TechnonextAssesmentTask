package com.pavel.tecchnonextkotlinassesment.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import com.pavel.tecchnonextkotlinassesment.data.repo.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repo: PostsRepository
): ViewModel() {
    val favouritePosts: Flow<List<PostEntity>> = repo.favouritePosts()
    fun toggleFavourite(id: Int) {
        viewModelScope.launch {
            repo.removeFavourite(id)
        }
    }
}