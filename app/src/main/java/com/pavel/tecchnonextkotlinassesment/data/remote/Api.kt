package com.pavel.tecchnonextkotlinassesment.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

interface JsonPlaceholderApi {
    // Supports pagination via _page and _limit
    @GET("posts")
    suspend fun getPosts(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int
    ): List<PostDto>
}