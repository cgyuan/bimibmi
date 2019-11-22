package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class HistoryViewModel(historyRepository: HistoryRepository, private val movieRepository: OnlineMovieRepository) : ViewModel() {

    suspend fun fetchMovieDetail(url: String): MovieDetail {
        return movieRepository.fetchMovieDetail(url)
    }

    val movies: LiveData<PagedList<History>> =
        LivePagedListBuilder<Int, History>(historyRepository.getAllHistoryByPage(),
            PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .build()).build()
}