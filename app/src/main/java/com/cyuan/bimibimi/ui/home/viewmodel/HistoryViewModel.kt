package com.cyuan.bimibimi.ui.home.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class HistoryViewModel(historyRepository: HistoryRepository, private val movieRepository: OnlineMovieRepository) : ViewModel() {

    val movieDetail = MutableLiveData<MovieDetail>()

    fun fetchMovieDetail(url: String) {
        launch({
            val detail = movieRepository.fetchMovieDetail(url)
            movieDetail.value = detail
        }, {
            Toast.makeText(App.getContext(), it.message, Toast.LENGTH_SHORT).show()
        })
    }

    val movies: LiveData<PagedList<History>> =
        LivePagedListBuilder<Int, History>(historyRepository.getAllHistoryByPage(),
            PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .build()).build()
}