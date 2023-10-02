package com.sekalisubmit.githubmu.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.sekalisubmit.githubmu.data.repository.FavsRepository

class FavoriteViewModel(application: Application) : ViewModel() {

    private val cFavsRepository: FavsRepository = FavsRepository(application)

    fun getAllFavUsers() = cFavsRepository.getAllFavUsers()
}