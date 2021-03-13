package com.cyuan.bimibimi.ui.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cyuan.bimibimi.repository.OnlineMovieRepository
import kotlinx.coroutines.flow.catch

class CategoryViewModel(
    private val repository: OnlineMovieRepository
) : ViewModel() {


    fun fetchCategoryContent(path: String, onError: (String?) -> Unit = {}) =
        repository.fetchCategoryContent(path)
            .catch { onError(it.message) }
            .asLiveData(viewModelScope.coroutineContext)

    companion object {
        fun provideFactory() = object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CategoryViewModel(OnlineMovieRepository.instance) as T
            }

        }
    }
}