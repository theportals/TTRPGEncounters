package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.repositories.UserRepository

class SplashScreenViewModel(application: Application): AndroidViewModel(application) {
    fun isUserLoggedIn() = UserRepository.isUserLoggedIn()
}