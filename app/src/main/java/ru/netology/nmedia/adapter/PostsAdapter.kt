package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.statisticsToString   // при этом dto.Post импортируется через PostViewModel и связанный с ней Repository


typealias OnLikeListener = (post: Post) -> Unit
typealias OnShareListener = (post: Post) -> Unit


class PostsAdapter(private val onLikeListener: OnLikeListener, private val onShareListener: OnShareListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
/*    var list = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        val post = list[position]
        val post = getItem(position)
        holder.bind(post)
    }

//    override fun getItemCount(): Int = list.size  // Без переопределения просто будет функция super.getItemCount()
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            messageAuthor.text = post.author
            messagePublished.text = post.published
            messageContent.text = post.content
            ibtnLikes.setImageResource(
                if (post.likedByMe) R.drawable.ic_heart_filled_red else R.drawable.ic_heart_unfilled
            )
            txtCountLikes.text = post.countLikes.statisticsToString()
            txtCountShare.text = post.countShare.statisticsToString()
            txtCountViews.text = post.countViews.statisticsToString()

            // Обработчики кликов
            ibtnLikes.setOnClickListener {
                onLikeListener(post)
            }
            ibtnShare.setOnClickListener {
                onShareListener(post)
            }

        }
    }
}


