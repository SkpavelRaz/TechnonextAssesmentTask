package com.pavel.tecchnonextkotlinassesment.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pavel.tecchnonextkotlinassesment.data.local.AppDb
import com.pavel.tecchnonextkotlinassesment.data.local.PostEntity
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 20

@OptIn(ExperimentalPagingApi::class)
class PostsRemoteMediator(
    private val db: AppDb,
    private val api: JsonPlaceholderApi
): RemoteMediator<Int, PostEntity>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) STARTING_PAGE_INDEX else (lastItem.id / NETWORK_PAGE_SIZE) + 1
            }
        }

        try {
            val apiResponse = api.getPosts(page = page, limit = NETWORK_PAGE_SIZE)
            val endOfPaginationReached = apiResponse.isEmpty()

            val entities = apiResponse.map { PostEntity(it.id, it.userId, it.title, it.body) }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.postDao().clearAll()
                }
                db.postDao().upsertAll(entities)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (io: IOException) {
            return MediatorResult.Error(io)
        } catch (he: HttpException) {
            return MediatorResult.Error(he)
        }
    }
}