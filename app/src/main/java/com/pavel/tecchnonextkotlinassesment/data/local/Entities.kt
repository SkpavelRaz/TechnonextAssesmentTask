package com.pavel.tecchnonextkotlinassesment.data.local

import androidx.room.*

@Entity(tableName = "posts",)
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)

@Entity(tableName = "favourites")
data class FavouriteEntity(
    @PrimaryKey val postId: Int
)

data class PostWithFavourite(
    @Embedded val post: PostEntity,
    @Relation(parentColumn = "id", entityColumn = "postId")
    val fav: FavouriteEntity?
)