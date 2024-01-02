package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token

class AuthViewModel(
    private val appAuth: AppAuth,
) : ViewModel() {
    val data: LiveData<Token?> = appAuth.data
        .asLiveData()    // Берем StateFlow и преобразуем его к лайвдате

    val isAuthorized: Boolean
        get() = appAuth.data.value != null    // Берем StateFlow и проверяем
}
