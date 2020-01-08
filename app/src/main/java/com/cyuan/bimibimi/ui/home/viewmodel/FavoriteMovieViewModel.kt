package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.db.repository.FavoriteMovieRepository
import com.cyuan.bimibimi.model.FavoriteMovie

class FavoriteMovieViewModel(repository: FavoriteMovieRepository) : ViewModel() {

    val movies: LiveData<PagedList<FavoriteMovie>> =
        LivePagedListBuilder<Int, FavoriteMovie>(repository.getAllMovieByPage(),
            PagedList.Config.Builder()
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(10)
                .build()).build()

    val viewState = MutableLiveData(Constants.ViewState.LOADING)
}