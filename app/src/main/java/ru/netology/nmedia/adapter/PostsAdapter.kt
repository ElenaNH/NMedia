package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.statisticsToString   // при этом dto.Post импортируется через PostViewModel и связанный с ней Repository

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
}


class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(
            binding,
            onInteractionListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            messageAuthor.text = post.author
            messagePublished.text = post.published
            messageContent.text = post.content
            /* //Для типа элемента ImageButton
            ibtnLikes.setImageResource(
                if (post.likedByMe) R.drawable.ic_heart_filled_red else R.drawable.ic_heart_unfilled
            )*/
            // Для типа элемента CheckBox
            ibtnLikes.isChecked = post.likedByMe
            txtCountLikes.text = post.countLikes.statisticsToString()
            txtCountShare.text = post.countShare.statisticsToString()
            txtCountViews.text = post.countViews.statisticsToString()

            // Обработчики кликов

            ibtnLikes.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            ibtnShare.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            ibtnMenuMoreActions.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {      // НОВЫЙ ПУНКТ МЕНЮ
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}



