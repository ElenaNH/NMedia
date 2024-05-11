package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post

/** Сравнение двух элементов между собой */
class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) return false

        if ((oldItem is Post) && (newItem is Post)) {
            if (oldItem.unconfirmed != newItem.unconfirmed) return false
        }

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

    // Для работы с Payload переопределим еще один метод
    override fun getChangePayload(oldItem: FeedItem, newItem: FeedItem): Any? =
        if ((oldItem is Post) && (newItem is Post))
            Payload(
                likes = newItem.likes.takeIf { it != oldItem.likes },
                likedByMe = newItem.likedByMe.takeIf { it != oldItem.likedByMe },
                content = newItem.content.takeIf { it != oldItem.content }
            )
        else
            null

}

