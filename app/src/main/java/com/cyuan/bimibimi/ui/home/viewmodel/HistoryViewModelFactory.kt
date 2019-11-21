package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.repository.FavoriteMovieRepository
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class HistoryViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = HistoryRepository.getInstance(AppDatabase.instance.historyDao())
        return HistoryViewModel(repository, OnlineMovieRepository.instance) as T
    }
}