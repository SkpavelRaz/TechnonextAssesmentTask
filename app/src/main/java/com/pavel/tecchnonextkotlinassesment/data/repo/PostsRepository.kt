package com.pavel.tecchnonextkotlinassesment.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pavel.tecchnonextkotlinassesment.data.local.AppDb
import com.pavel.tecchnonextkotlinassesment.data.local.FavouriteEntity
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import com.pavel.tecchnonextkotlinassesment.data.remote.JsonPlaceholderApi
import com.pavel.tecchnonextkotlinassesment.data.remote.PostsRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val PAGE_SIZE = 20

class PostsRepository @Inject constructor(
    private val db: AppDb,
    private val api: JsonPlaceholderApi
) {
    @OptIn(ExperimentalPagingApi::class)
    fun pagedPosts(): Flow<PagingData<PostEntity>> {
        val pagingSourceFactory = { db.postDao().pagingSource() }
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            remoteMediator = PostsRemoteMediator(db, api),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchPosts(query: String): Flow<List<PostEntity>> =
        db.postDao().search(query)

    suspend fun toggleFavourite(id: Int) {
        val favs = db.favouriteDao()
        val current = favs.isFavourite(id)
        // can't get value directly; we simply try remove then add
        db.runInTransaction {
            // naive toggle using SQL
        }
    }

    suspend fun addFavourite(id: Int) = db.favouriteDao().add(FavouriteEntity(id))
    suspend fun removeFavourite(id: Int) = db.favouriteDao().remove(id)

    fun favouriteIds(): Flow<Set<Int>> =
        db.favouriteDao().all().map { it.map { f -> f.postId }.toSet() }

    fun favouritePosts(): Flow<List<PostEntity>> = db.favouriteDao().favouritePosts()
}