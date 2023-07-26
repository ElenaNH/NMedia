package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.fromDto
import ru.netology.nmedia.entity.toDto
//import java.io.IOException
//import java.util.concurrent.TimeUnit
//import kotlin.Exception
import java.lang.RuntimeException


class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data: LiveData<List<Post>> = postDao.getAll().map {
        it.toDto()
    }
    // override val data: LiveData<List<Post>> = postDao.getAll().map { it.map(PostEntity::toDto) }

    override suspend fun getAll() {
        val response = PostsApi.retrofitService.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }
        val posts = response.body() ?: throw RuntimeException("body is null")
        // Если данные пришли хорошие, то
        postDao.insert(posts.fromDto())
        //postDao.insert(posts.map { PostEntity.fromDto(it)})
    }

    override suspend fun save(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        // TODO

        // Сначала удаляем в локальной БД (оптимистичная модель)
        postDao.removeById(id)

        // Затем отправляем запрос удаления на сервер
        val response = PostsApi.retrofitService.removeById(id)

        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }
        val responseUnit = response.body() ?: throw RuntimeException("body is null")
        // Если вернулся ожидаемый Unit,а не null, то
        // Ничего не делаем (ведь мы уже заранее удалили в локальной БД)

        // Если же мы ранее вывалились по любой из ошибок,
    // то на уровне ViewModel мы должны обратно обновить данные (вернуть недоудаленный пост)
    }

    override suspend fun likeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun shareById(id: Long) {
        TODO("Not yet implemented")
    }


}
