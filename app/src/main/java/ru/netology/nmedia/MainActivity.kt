package ru.netology.nmedia          // Пока не будем вкладывать в лишний пакет package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
//import ru.netology.nmedia.databinding.CardPostBinding
//import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.util.AndroidUtils

//import ru.netology.nmedia.dto.statisticsToString   // при этом dto.Post импортируется через PostViewModel и связанный с ней Repository

class MainActivity : AppCompatActivity() {
    val viewModel: PostViewModel by viewModels()
    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.startEditing(post)
        }
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val adapter by lazy { PostsAdapter(interactionListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)  // binding вынесли выше и отдали by lazy, и только при первом вызове реально создастся binding
        binding.list.adapter = adapter   // val adapter = PostsAdapter(interactionListener) вынесли выше и отдали by lazy

        subscribe()
        setListeners()
    }

    private fun subscribe() {
        // Подписки:

        // Подписка на список сообщений
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        // Подписка на нижнее поле добавления/изменения
        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            with(binding.editContent) {
                requestFocus()
                setText(post.content)
            }
        }

    }

    private fun setListeners() {
        // Обработчики кликов

        binding.ibtnSave.setOnClickListener {
            with(binding.editContent) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()
            }
            clearEditContent()
        }

        binding.ibtnClear.setOnClickListener {
            viewModel.quitEditing()
            clearEditContent()
        }
    }

    private fun clearEditContent() {
        with(binding.editContent) {
            setText("")
            clearFocus()
            AndroidUtils.hideKeyboard(this)
        }
    }
}

