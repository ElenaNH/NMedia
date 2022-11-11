package ru.netology.nmedia          // Пока не будем вкладывать в лишний пакет package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.dto.statisticsToString   // при этом dto.Post импортируется через PostViewModel и связанный с ней Repository

class MainActivity : AppCompatActivity() {
    val viewModel: PostViewModel by viewModels()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribe()
        setListeners()
    }

    private fun setListeners() {
        // Обработчики кликов
        binding.ibtnLikes.setOnClickListener {
            viewModel.like()
        }
        binding.ibtnShare.setOnClickListener {
            viewModel.share()
        }
    }

    private fun subscribe() {
        // Подписка
        viewModel.data.observe(this) { post ->
            with(binding) {
                messageAuthor.text = post.author
                messagePublished.text = post.published
                messageContent.text = post.content
                ibtnLikes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_heart_filled_red else R.drawable.ic_heart_unfilled
                )
                txtCountLikes.text = post.countLikes.statisticsToString()
                txtCountShare.text = post.countShare.statisticsToString()
                txtCountViews.text = post.countViews.statisticsToString()
            }
        }
    }
}


