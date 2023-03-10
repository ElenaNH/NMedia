package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
//import androidx.navigation.Navigation.findNavController  // этот не подходит
//import androidx.navigation.findNavController  // и этот не подходит (но он использовался для перехода из активити)
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.uiview.PostInteractionListenerImpl
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.databinding.FragmentFeedBinding


class FeedFragment : Fragment() {
    //  viewModels используем теперь с аргументом, чтобы сделать общую viewModel для всех фрагментов
    val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    // interactionListener должен быть доступен также из фрагмента PostFragment
    // private убираем???
/*
    private val interactionListener = object : OnInteractionListener {
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
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            // А здесь мы могли запустить наш intent без красоты, либо улучшенный shareIntent
            startActivity(shareIntent)
            // Увеличиваем счетчик шаринга
            viewModel.shareById(post.id)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.startEditing(post)
            // Если пост непустой, то запустим фрагмент редактирования поста newPostFragment

            // Поскольку мы уже нахдимся во фрагменте, то не нужен аргумент, задающий граф навигации
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
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
            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            // А здесь мы могли запустить наш intent без красоты, либо улучшенный shareIntent
            startActivity(shareIntent)
        }

        override fun onViewSingle(post: Post) {
            if (!this.javaClass.toString().contains("FeedFragment")) return

            // Если мы тут, то это FeedFragment
            findNavController().navigate(
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
*/

    private val interactionListener by lazy { PostInteractionListenerImpl(viewModel, this) }

    // создаем привязку к элементам макета по первому обращению к ним
    //private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var binding: FragmentFeedBinding // как сделать by lazy ????

    val adapter by lazy { PostsAdapter(interactionListener) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = makeBinding(
            container
        )   // в лекции layoutInflater, а в примерах переданный параметр inflater

        // Содержимое onCreate, оставшееся от activity, должно быть здесь
        binding.list.adapter =
            adapter   // val adapter = PostsAdapter(interactionListener) вынесли выше и отдали by lazy
        subscribe()     // все подписки, которые могут нам потребоваться в данной активити
        setListeners()  // все лиснеры всех элементов данной активити

        return binding.root
    }


    private fun makeBinding(
        container: ViewGroup?
    ): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(
            layoutInflater,
            container,
            false  // false означает, что система сама добавить этот view, когда посчитает нужным
        )  // в лекции layoutInflater, а в примерах переданный параметр inflater
    }

    private fun subscribe() {
        // Подписки:

        // Подписка на список сообщений
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val newPost = (adapter.currentList.size < posts.size) // элементов в списке стало больше
            // далее обновление списка отображаемых постов в RecyclerView
            adapter.submitList(posts) {
                // далее - прокрутка до верхнего элемента списка (с индексом 0)
                // ее нужно делать только если обновился список в адаптере
                // иначе он не к верхнему прокрутит, а ко второму
                if (newPost) binding.list.smoothScrollToPosition(0)
            }
        }
    }

    private fun setListeners() {
        // Обработчики кликов

        // Пока что все обработчики либо в адаптере, либо в другом обработчике,
        // fab не получилось сделать безопасно (не знаю, как сделать by lazy с аргументами)

        binding.fab.setOnClickListener {
            // Запуск фрагмента NewPostFragment
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    //textArg = ""  // В запускаемый фрагмент передаем пустое содержимое нового поста
                    textArg = viewModel.getDraftContent()  // В запускаемый фрагмент передаем содержимое черновика
                }
            )

        }

    }
}
