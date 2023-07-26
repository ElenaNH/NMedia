package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    /*@Query("UPDATE PostEntity SET content = :content, videoLink = :videoLink WHERE id = :id")
    fun updateContentById(id: Long, content: String, videoLink: String) // Лучше весь пост передавать*/
    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post)
        else updateContentById(
            post.id,
            post.content
        ) // updateContentById(post.id, post.content, post.videoLink ?: "")

    // поле на сервере теперь называется likes (вместо countLikes)
    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likeById(id: Long)

    /*  // Пока нам не дали api на это действие, так что заменим его ничего не делающим действием
          @Query(
            """
            UPDATE PostEntity SET
            countShare = countShare + 1
            WHERE id = :id
            """
        ) */
    @Query(
        """
            UPDATE PostEntity SET
            likes = likes
            WHERE id = :id
            """
    )
    suspend fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}


