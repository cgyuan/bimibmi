package com.cyuan.bimibimi.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cyuan.bimibimi.repository.OnlineMovieRepository
import kotlinx.coroutines.flow.catch

class SearchViewModel(
    private val repository: OnlineMovieRepository
): ViewModel() {

    fun fetchSearchContent(keyWork: String, onError: (String?) -> Unit = {}) =
        repository.fetchSearchContent(keyWork)
            .catch { onError(it.message) }
            .asLiveData(viewModelScope.coroutineContext)

    companion object {
        fun provideFactory() = object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(OnlineMovieRepository.instance) as T
            }

        }
    }
}