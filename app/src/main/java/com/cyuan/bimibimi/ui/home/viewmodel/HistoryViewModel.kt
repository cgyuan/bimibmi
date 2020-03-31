package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class HistoryViewModel(
    private val historyRepository: HistoryRepository,
    private val movieRepository: OnlineMovieRepository) : ViewModel() {

    suspend fun fetchMovieDetail(url: String): MovieDetail {
        return movieRepository.fetchMovieDetail(url)
    }

    val host = MutableLiveData<String>(GlobalUtil.host)

    val viewState = MutableLiveData(Constants.ViewState.LOADING)
    val movies: LiveData<PagedList<History>> =
        Transformations.switchMap(host) { buildPagedListData() }

    private fun buildPagedListData(): LiveData<PagedList<History>>? {
        return LivePagedListBuilder<Int, History>(historyRepository.getAllHistoryByPage(GlobalUtil.host),
            PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .build()).build()
    }

}