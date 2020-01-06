package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyuan.bimibimi.db.repository.RepositoryProvider

class FavoriteMovieViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = RepositoryProvider.providerFavoriteMovieRepository()
        return FavoriteMovieViewModel(repository) as T
    }
}