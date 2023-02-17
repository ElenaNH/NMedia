package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.repository.PostRepositorySharedPrefsImpl
import ru.netology.nmedia.repository.PostRepositoryFileImpl

private val emptyPost = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    countLikes = 0,
    countShare = 0,
    countViews = 0
)

//class PostViewModel : ViewModel()
class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
//    private val repository: PostRepository =
//        PostRepositoryInMemoryImpl() // Пока сохраним упрощенный код, хоть так обычно и не делается
//    private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)
    private val repository: PostRepository = PostRepositoryFileImpl(application)


    val data = repository.getAll()
    val edited = MutableLiveData(emptyPost)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        quitEditing()
    }

    fun startEditing(post: Post) {
        edited.value = post
    }

    fun quitEditing() {
        edited.value = emptyPost
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

}
