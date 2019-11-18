package com.cyuan.bimibimi.ui.detail

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class MovieDetailViewModel(private val repository: OnlineMovieRepository) : ViewModel() {

    val movieDetail = MutableLiveData<MovieDetail>()

    fun fetchMovieDetail(url: String) {
        launch({
            val detail = repository.fetchMovieDetail(url)
            movieDetail.value = detail
        }, {
            Toast.makeText(App.getContext(), it.message, Toast.LENGTH_SHORT).show()
        })
    }
}