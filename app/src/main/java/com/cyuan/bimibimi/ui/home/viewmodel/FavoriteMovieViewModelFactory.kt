package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.repository.FavoriteMovieRepository

class FavoriteMovieViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = FavoriteMovieRepository(AppDatabase.instance.favoriteMovieDao())
        return FavoriteMovieViewModel(repository) as T
    }
}