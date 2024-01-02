package ru.netology.nmedia

import android.app.Application
import ru.netology.nmedia.di.DependencyContainer

class App : Application() {
// Чтобы у этого класса была связь с процессом, его нужно зарегистрировать в манифесте

    override fun onCreate() {
        super.onCreate()
        DependencyContainer.initApp(this)
    }
}
