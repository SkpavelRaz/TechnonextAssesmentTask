package com.pavel.tecchnonextkotlinassesment.data.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Upsert
    suspend fun upsertAll(posts: List<PostEntity>)

    @Query("SELECT * FROM posts ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    @Query("SELECT * FROM posts WHERE title LIKE '%' || :q || '%' OR body LIKE '%' || :q || '%' ORDER BY id ASC")
    fun search(q: String): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun count(): Int
}

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(fav:FavouriteEntity)

    @Query("DELETE FROM favourites WHERE postId = :id")
    suspend fun remove(id: Int)

    @Query("SELECT * FROM favourites")
    fun all(): Flow<List<FavouriteEntity>>

    @Query("SELECT p.* FROM posts p INNER JOIN favourites f ON p.id = f.postId ORDER BY p.id ASC")
    fun favouritePosts(): Flow<List<PostEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE postId = :id)")
    fun isFavourite(id: Int): Flow<Boolean>
}

@Database(entities = [PostEntity::class, FavouriteEntity::class], version = 1)
abstract class AppDb: RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun favouriteDao(): FavouriteDao
}