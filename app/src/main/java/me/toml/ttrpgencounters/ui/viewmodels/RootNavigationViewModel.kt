package me.toml.ttrpgencounters.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import me.toml.ttrpgencounters.ui.repositories.UserRepository

class RootNavigationViewModel(application: Application): AndroidViewModel(application) {
    fun signOutUser() = UserRepository.signOutUser()
}