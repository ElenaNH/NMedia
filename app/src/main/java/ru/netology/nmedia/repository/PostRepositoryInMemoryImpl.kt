package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf (
        Post(
            id = 1,
            author = "Нетология-1.! Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология!",
            published = "21 мая в 18:36",
            likedByMe = false,
            countLikes = 1099,
            countShare = 997,
            countViews = 5
        ),
        Post(
            id = 2,
            author = "Нетология-2.+ Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:37",
            likedByMe = false,
            countLikes = 99,
            countShare = 997,
            countViews = 15
        ),
        Post(
            id = 3,
            author = "Нетология-3. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:38",
            likedByMe = false,
            countLikes = 1099,
            countShare = 997,
            countViews = 5
        )
    )


    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countLikes = it.countLikes + if (!it.likedByMe) 1 else -1
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(countShare = it.countShare + 1)
        }
        data.value = posts
    }

/*
override fun get(): LiveData<Post> = data
override fun like() {
    post = post.copy(
        likedByMe = !post.likedByMe,
        countLikes = post.countLikes + if (!post.likedByMe) 1 else -1
    )
    data.value = post
}

override fun share() {
    post = post.copy(countShare = post.countShare + 1)
    data.value = post
}

 */
}

