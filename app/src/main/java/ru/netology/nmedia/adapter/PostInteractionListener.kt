package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
//import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.ARG_POST_ID
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.activity.FeedFragment
import ru.netology.nmedia.activity.PostFragment
import ru.netology.nmedia.viewmodel.PostViewModel

class PostInteractionListener(viewModelInput: PostViewModel, fragmentInput: Fragment) :
    OnInteractionListener {
    /* Поскольку интерфейс OnInteractionListener описан в пакете adapter, то и наш класс разместим тут */
    private val viewModel: PostViewModel = viewModelInput
    private val fragmentParent = fragmentInput

    override fun onLike(post: Post) {
        viewModel.likeById(post.id)
    }

    override fun onShare(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }
        // Следующая строка необязательная - интент на красивый выбор запускаемого приложения
        val shareIntent =
            Intent.createChooser(intent, fragmentParent.getString(R.string.chooser_share_post))
        // А здесь мы могли запустить наш intent без красоты, либо улучшенный shareIntent
        fragmentParent.startActivity(shareIntent)
        // Увеличиваем счетчик шаринга
        viewModel.shareById(post.id)
    }

    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
        if (!(fragmentParent is FeedFragment))
            fragmentParent.findNavController()
                .navigateUp() //  Закрытие текущего фрагмента (если это не стартовый FeedFragment)
    }

    override fun onEdit(post: Post) {
        viewModel.startEditing(post)
        // Если пост непустой, то запустим фрагмент редактирования поста newPostFragment

        // Поскольку мы уже нахдимся во фрагменте, то не нужен аргумент, задающий граф навигации
        // Но нам нужно знать, в каком мы фрагменте, чтобы задать правильный переход
        val action_from_to =
            when {
                (fragmentParent is FeedFragment) -> R.id.action_feedFragment_to_newPostFragment
                (fragmentParent is PostFragment) -> R.id.action_postFragment_to_newPostFragment
                else -> null
            }

        if (action_from_to != null)
            fragmentParent.findNavController().navigate(
                action_from_to,
                Bundle().apply {
                    textArg =
                        post.content.toString()  // В запускаемый фрагмент передаем данные редактируемого поста
                }
            ) // Когда тот фрагмент закроется, опять окажемся здесь (по стеку)
    }

    override fun onVideoLinkClick(post: Post) {
        // Тут по-другому создается интент
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink))

        // Следующая строка необязательная - интент на красивый выбор запускаемого приложения
        val shareIntent =
            Intent.createChooser(intent, fragmentParent.getString(R.string.chooser_share_post))
        // А здесь мы могли запустить наш intent без красоты, либо улучшенный shareIntent
        fragmentParent.startActivity(shareIntent)
    }

    override fun onViewSingle(post: Post) {
        if (!(fragmentParent is FeedFragment)) return
        // Если мы тут, то это FeedFragment
        fragmentParent.findNavController().navigate(
            R.id.action_feedFragment_to_postFragment,
            Bundle().apply {
                putLong(ARG_POST_ID, post.id)
                // Почему-то написание еще одного синглтона не кажется хорошей идеей
                // Не писать же синглтон под каждый аргумент - чем это проще putLong?

                //textArg =
                //    post.content.toString()  // В запускаемый фрагмент передаем данные редактируемого поста
            }
        )
    }
}
