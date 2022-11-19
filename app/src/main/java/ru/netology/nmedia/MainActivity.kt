package ru.netology.nmedia          // Пока не будем вкладывать в лишний пакет package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
//import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
//import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

//import ru.netology.nmedia.dto.statisticsToString   // при этом dto.Post импортируется через PostViewModel и связанный с ней Repository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter({
            viewModel.likeById(it.id)
        }, {
            viewModel.shareById(it.id)
        })
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
//            adapter.list = posts
        }
    }


}


