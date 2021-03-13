package com.cyuan.bimibimi.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyuan.bimibimi.db.repository.RepositoryProvider
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class MovieDetailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieDetailViewModel(
            OnlineMovieRepository.instance,
            RepositoryProvider.provideFavoriteMovieRepository(),
            RepositoryProvider.provideDownloadTaskRepository()
        ) as T
    }
}