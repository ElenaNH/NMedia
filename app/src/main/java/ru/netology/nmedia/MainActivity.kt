package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.statisticsToString

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = getString(R.string.message_title),
            content = getString(R.string.message_text),
            published = getString(R.string.message_date_time),
            likedByMe = false,
            countLikes = 1099,
            countShare = 997 ,
            countViews = 5
        )

        with(binding) {
            messageTitle.text = post.author
            messageText.text = post.content
            messageDateTime.text = post.published
            if (post.likedByMe) {
                ibtnLikes.setImageResource(R.drawable.ic_heart_filled_red)
            }

            // Тут надо сделать 1К, 1.1К, 1М и т.п.
            txtCountLikes.text = post.countLikes.statisticsToString()
            txtCountShare.text = post.countShare.statisticsToString()
            txtCountViews.text = post.countViews.statisticsToString()

            // Обработчики кликов
            ibtnLikes.setOnClickListener {
                post.likedByMe = !post.likedByMe
                ibtnLikes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_heart_filled_red else R.drawable.ic_heart_unfilled
                )
                post.countLikes += if (post.likedByMe) 1 else -1
                txtCountLikes.text = post.countLikes.statisticsToString()
            }

            ibtnShare.setOnClickListener {
                post.countShare++
                txtCountShare.text = post.countShare.statisticsToString()
            }

        }
    }
}
