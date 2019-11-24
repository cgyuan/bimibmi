package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class DailyUpdateViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DailyUpdateViewModel(OnlineMovieRepository.instance) as T
    }
}