package com.cyuan.bimibimi.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.db.repository.FavoriteMovieRepository
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie

class FavoriteMovieViewModel(
    private val repository: FavoriteMovieRepository
) : ViewModel() {


    val host = MutableLiveData<String>(GlobalUtil.host)

    val movies: LiveData<PagedList<FavoriteMovie>> =
        Transformations.switchMap(host) {
            LivePagedListBuilder<Int, FavoriteMovie>(repository.getAllMovieByPage(GlobalUtil.host),
                PagedList.Config.Builder()
                    .setPageSize(10)
                    .setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(10)
                    .build()).build()
        }

    val viewState = MutableLiveData(Constants.ViewState.LOADING)

    fun removeMovie(movie: Movie) {
        launch({
            repository.removeMovie(movie)
        })
    }

}