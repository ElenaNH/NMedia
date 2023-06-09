package ru.netology.nmedia.activity

import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.netology.nmedia.R
import ru.netology.nmedia.enumeration.PostActionType
import ru.netology.nmedia.viewmodel.PostViewModel

fun Fragment.whenPostActionFailed(viewModel: PostViewModel, postActionType: PostActionType) {
    // При подписке на однократную ошибку
    val fragmentInput = this
    val toastInfo = when (postActionType) {
        PostActionType.ACTION_POST_SAVING -> {
            if (fragmentInput is NewPostFragment) viewModel.continueEditing() // разрешим повторное сохранение
            fragmentInput.getString(R.string.error_post_saving)
        } //
        PostActionType.ACTION_POST_LIKE_CHANGE -> fragmentInput.getString(R.string.error_post_like_change)
        PostActionType.ACTION_POST_DELETION -> fragmentInput.getString(R.string.error_post_deletion)
    }
    showToast(fragmentInput, toastInfo)  // Всплывающее сообщение
}

fun Fragment.whenPostActionSucceed(viewModel: PostViewModel, postActionType: PostActionType) {
    // При подписке на однократное успешное действие
    val fragmentInput = this
    val toastInfo = when (postActionType) {
        PostActionType.ACTION_POST_SAVING -> fragmentInput.getString(R.string.succeed_post_saving)
        PostActionType.ACTION_POST_LIKE_CHANGE -> fragmentInput.getString(R.string.succeed_post_like_change)
        PostActionType.ACTION_POST_DELETION -> fragmentInput.getString(R.string.succeed_post_deletion)
    }
    showToast(this, toastInfo)  // Всплывающее сообщение
}

private fun showToast(fragmentInput: Fragment, toastInfo: String) {
    // Всплывающее сообщение
    val warnToast = Toast.makeText(
        fragmentInput.activity,
        toastInfo,
        Toast.LENGTH_SHORT
    )
    warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
    warnToast.show()
}
