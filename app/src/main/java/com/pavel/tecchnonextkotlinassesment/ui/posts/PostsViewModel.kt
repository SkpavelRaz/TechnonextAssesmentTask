package com.pavel.tecchnonextkotlinassesment.ui.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import com.pavel.tecchnonextkotlinassesment.data.repo.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val repo: PostsRepository
): ViewModel() {

    val pager: Flow<PagingData<PostEntity>> = repo.pagedPosts().cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val favouriteIds = repo.favouriteIds()

    fun updateSearch(q: String) { _searchQuery.value = q }

    fun toggleFavourite(id: Int) {
        viewModelScope.launch {
            // crude toggle: if id in current fav set -> remove, else add
            val set = favouriteIds.first()
            if (id in set) repo.removeFavourite(id) else repo.addFavourite(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<List<PostEntity>> = searchQuery
        .debounce(250)
        .flatMapLatest { q ->
            if (q.isBlank()) flowOf(emptyList()) else repo.searchPosts(q)
        }
}