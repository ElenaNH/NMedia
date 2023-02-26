package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import android.view.Gravity
import android.widget.Toast
import ru.netology.nmedia.R
import ru.netology.nmedia.util.ARG_POST_ID

//import ru.netology.nmedia.databinding.FragmentFeedBinding

class NewPostFragment : Fragment() {

    companion object {  // Объект для сокращения кода при передаче аргумента между фрагментами
        var Bundle.textArg: String? by StringArg
    }

    //  viewModels используем теперь с аргументом, чтобы сделать общую viewModel для всех фрагментов
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
// Если удастся объявить binding через by lazy, то этот кусочек кода уйдет
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false  // false означает, что система сама добавит этот view, когда посчитает нужным
        )

        arguments?.textArg
            ?.let(binding.editContent::setText) // Задаем текст поста из передаточного элемента textArg

        // Пока не пойму, как объявить binding через by lazy, лучше не выносить отсюда этот лиснер
        binding.btnOk.setOnClickListener {
            if (binding.editContent.text.isNullOrBlank()) {

                // Предупреждение о непустом содержимом
                val warnToast = Toast.makeText(
                    this.activity,
                    getString(R.string.error_empty_content),
                    Toast.LENGTH_SHORT
                )
                warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                warnToast.show()
                return@setOnClickListener

            } else {
                // Поскольку viewModel общая, то можно прямо тут сохраниться
                viewModel.changeContent(binding.editContent.text.toString())
                viewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                // Закрытие текущего фрагмента (переход к нижележащему в стеке)
                findNavController().navigateUp()
            }
        }
        return binding.root
    }


    private fun makeBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPostBinding {
        return FragmentNewPostBinding.inflate(
            inflater,
            container,
            false  // false означает, что система сама добавит этот view, когда посчитает нужным
        )
    }

}

