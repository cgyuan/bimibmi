package com.cyuan.bimibimi.ui.detail

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class MovieDetailViewModel(private val repository: OnlineMovieRepository) : ViewModel() {

    val movieDetail = MutableLiveData<MovieDetail>()
    var viewState = MutableLiveData(Constants.ViewState.DONE)
    var loadDataState = MutableLiveData(Constants.ViewState.LOADING)

    fun fetchMovieDetail(url: String) {
        loadDataState.value = Constants.ViewState.LOADING
        viewState.value = Constants.ViewState.DONE
        launch({
            val detail = repository.fetchMovieDetail(url)
            movieDetail.value = detail
            viewState.value = Constants.ViewState.DONE
            loadDataState.value = Constants.ViewState.DONE
        }, {
            Toast.makeText(App.getContext(), it.message, Toast.LENGTH_SHORT).show()
            viewState.value = Constants.ViewState.ERROR
            loadDataState.value = Constants.ViewState.ERROR
        })
    }
}