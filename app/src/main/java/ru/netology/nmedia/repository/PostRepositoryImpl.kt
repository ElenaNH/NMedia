package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
//import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.Exception


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    /* // Это бы годилось, если бы Post и PostEntity были идентичны по набору полей, а у нас они разные
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val typeTokenOnePost = object : TypeToken<Post>() {}*/
    private val typeToken = object : TypeToken<List<PostEntity>>() {}
    private val typeTokenOnePost = object : TypeToken<PostEntity>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(postsCallBack: PostsCallBack<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    postsCallBack.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        postsCallBack.onError(Exception(response.message))
                    }
                    /*// ЛИБО
                    val body = response.body?.string() ?: throw RuntimeException("body is null")*/
                    // ЛИБО РАВНОСИЛЬНО:
                    val body = requireNotNull(response.body?.string()) { "body is null" }
                    val postEntities = gson.fromJson<List<PostEntity>>(body, typeToken.type)
                    val posts = postEntities.map { it.toDto() }
                    postsCallBack.onSuccess(posts)
                }
            })
    }


    override fun likeById(ratedPost: Post, postsCallBack: PostsCallBack<Unit>) {
        // TODO: do this in homework
        // POST /api/posts/{id}/likes
        // DELETE /api/posts/{id}/likes


        val request: Request = when {
            (ratedPost.likedByMe) -> Request.Builder()
                .post(gson.toJson(ratedPost).toRequestBody(jsonType))

            else -> Request.Builder()
                .delete(gson.toJson(ratedPost).toRequestBody(jsonType))
        }
            .url("${BASE_URL}/api/posts/${ratedPost.id}/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        postsCallBack.onError(Exception(response.message))
                    }
                    // Ничего дополнительно не делаем - все необходимое сервер сделал
                    // А нам остается только вернуть успешный сигнал
                    postsCallBack.onSuccess(Unit)
                }

                override fun onFailure(call: Call, e: IOException) {
                    postsCallBack.onError(e)
                }
            })

    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")  //Наш сервер пока не обрабатывает шаринг, поэтому не наращиваем счетчик

    }

    override fun save(post: Post, postsCallBack: PostsCallBack<Post> ) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        postsCallBack.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()) { "body is null" }
                    val postEntity = gson.fromJson<PostEntity>(body, typeTokenOnePost.type)
                    val post = postEntity.toDto()
                    postsCallBack.onSuccess(post)
                }

                override fun onFailure(call: Call, e: IOException) {
                    postsCallBack.onError(e)
                }
            })
    }

    override fun removeById(id: Long, postsCallBack: PostsCallBack<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    postsCallBack.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        postsCallBack.onError(Exception(response.message))
                    }
                    // Ничего дополнительно не делаем - все необходимое сервер сделал
                    // А нам остается только вернуть успешный сигнал
                    postsCallBack.onSuccess(Unit)
                }
            })
    }

    private fun getByIdAsync(id: Long, postsCallBack: PostsCallBack<Post> ) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        postsCallBack.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()) { "body is null" }
                    val postEntity = gson.fromJson<PostEntity>(body, typeTokenOnePost.type)
                    val post = postEntity.toDto()
                    postsCallBack.onSuccess(post)
                }

                override fun onFailure(call: Call, e: IOException) {
                    postsCallBack.onError(e)
                }
            })

    }

/*    private fun getEntityById(id: Long): PostEntity {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeTokenOnePost.type)
            }
    }

    private fun getById(id: Long): Post {
        return getEntityById(id)
            .toDto()
    }*/
}
