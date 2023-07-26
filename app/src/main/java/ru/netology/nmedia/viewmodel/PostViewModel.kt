package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.PostActionType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

//import java.io.IOException
//import kotlin.concurrent.thread


private val emptyPost = Post(
    id = 0,
    author = currentAuthor(),
    authorAvatar = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    countShare = 0,
    countViews = 0,
    attachment = null
)

private fun currentAuthor(): String = "Me"  // Надо вычислять текущего автора

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    private val _dataState = MutableLiveData(FeedModelState())
    val data: LiveData<FeedModel> =
        repository.data.map { FeedModel(posts = it, empty = it.isEmpty()) }
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    val edited = MutableLiveData(emptyPost)
    val draft = MutableLiveData(emptyPost)  // И будем сохранять это только "in memory"
    private val _postCreateLoading = MutableLiveData<Boolean>()
    private val _postCreated = SingleLiveEvent<Unit>()
    private val _postActionFailed = SingleLiveEvent<PostActionType>()  // Однократная ошибка
    private val _postActionSucceed =
        SingleLiveEvent<PostActionType>()  // Однократный успех (альтернатива ошибке)
    val postCreateLoading: LiveData<Boolean>
        get() = _postCreateLoading
    val postCreated: LiveData<Unit>
        get() = _postCreated
    val postActionFailed: LiveData<PostActionType>
        get() = _postActionFailed
    val postActionSucceed: LiveData<PostActionType>
        get() = _postActionSucceed

    init {
        loadPosts()
    }

    fun loadPosts() = refreshOrLoadPosts(refreshingState = false)

    fun refresh() = refreshOrLoadPosts(refreshingState = true)

    private fun refreshOrLoadPosts(refreshingState: Boolean) = viewModelScope.launch {
        if (refreshingState) {
            _dataState.value = FeedModelState(refreshing = true)  // Начинаем обновление
        } else {
            _dataState.value = FeedModelState(loading = true)  // Начинаем загрузку
        }
        try {
            repository.getAll()
            _dataState.value = FeedModelState()     // При успехе
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)  // При ошибке
        }
    }

    fun save() {
        // TODO
        // ДЗ

        /*edited.value?.let {
            _postCreateLoading.value = true
            repository.save(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(posts: Post) {

                    _postCreated.postValue(Unit) // Передаем сообщение, к-е обрабатывается однократно
                    // Если сохранились, то уже нет смысла в черновике (даже если сохранили другой пост)
                    _postCreateLoading.postValue(false) // Конец загрузки
                    postDraftContent("") // Чистим черновик, т.к. успешно вернулся результат и вызван CallBack
                    _postActionSucceed.postValue(PostActionType.ACTION_POST_SAVING)
                    quitEditing() // сбрасываем редактирование только при успешной записи
                }

                override fun onError(e: Exception) {
                    // Всплывающее сообщение об ошибке записи
                    _postActionFailed.postValue(PostActionType.ACTION_POST_SAVING)
                    // выход возможен либо при успехе, либо по кнопке "назад"
                    // отсюда выход в предыдущий фрагмент не делаем
                }
            })
        }*/

        //quitEditing()
    }


    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value =
            edited.value?.copy(content = text) // Тут мы действуем в главном потоке, поэтому присвоение
    }

    fun likeById(id: Long) {
        // TODO
        // ДЗ

        /*
        // оптимистичная модель
        val old = _data.value?.posts.orEmpty()
        var ratedPost: Post = emptyPost
        _data.value =
            _data.value?.copy(posts = _data.value?.posts.orEmpty() // Пока еще главный поток
                .map { post ->
                    if (post.id == id) {
                        ratedPost = post.copy(
                            likedByMe = !post.likedByMe,
                            likes = post.likes + if (post.likedByMe) -1 else 1
                        )
                        ratedPost   // Одновременно запомним и изменим обновленный пост в списке
                    } else post
                }
            )

        // Если даже пост не найден, и id остался нулевой (что маловероятно),
        // то все равно передадим его на сервер - пусть сервер вернет ошибку
        repository.likeById(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(posts: Post) {
                //if (this is FeedFragment) {
                _postActionSucceed.postValue(PostActionType.ACTION_POST_LIKE_CHANGE)
                //}
                // Ничего не делаем, потому что мы уже все сделали до вызова в расчете на успех
            }

            override fun onError(e: Exception) {

                _postActionFailed.postValue(PostActionType.ACTION_POST_LIKE_CHANGE)

                // Раз не ставится лайк, то вернемся к предыдущим данным
                _data.postValue(_data.value?.copy(posts = old))
            }
        })*/


        // завершение обработки лайка
    }

    fun shareById(id: Long) {
        // TODO()  //Наш сервер пока не обрабатывает шаринг, поэтому не наращиваем счетчик

    }

    fun removeById(id: Long) {
        // TODO

        // Оптимистичная модель - обновляем БД и экран до получения ответа от сервера

        val oldPosts = data.value?.posts.orEmpty()

        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                // Тут нужно:
                // 1) вывести ошибку
                _dataState.value = FeedModelState(error = true)
                            // однократную ошибку выводили раньше
                            // _postActionFailed.postValue(PostActionType.ACTION_POST_DELETION)
                            // но теперь состояние модели очень быстро изменится на "loading",
                            // так что ошибка и без спец.приемов отобразится однократно

                // 2) вернуть недоудаленный пост обратно
                // (запросим все посты с сервера, т.к. система отлажена)
                // При этом статус ошибки пропадет, а появится статус загрузки
                try {
                    repository.getAll()
                    _dataState.value = FeedModelState()     // При успехе
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)  // При ошибке
                }
            }

        }

    }


    fun startEditing(post: Post) {
        edited.value = post
    }

    fun continueEditing() {
        _postCreateLoading.value = false    // Сбрасываем статус загрузки поста
    }

    fun quitEditing() {
        edited.value = emptyPost
    }

    fun setDraftContent(draftContent: String) {
        draft.value = draft.value?.copy(content = draftContent.trim()) // Главный поток
    }

    fun postDraftContent(draftContent: String) {
        draft.postValue(draft.value?.copy(content = draftContent.trim())) // Фоновый поток!!!
    }

    fun getDraftContent(): String {
        return draft.value?.content ?: ""
    }
}
