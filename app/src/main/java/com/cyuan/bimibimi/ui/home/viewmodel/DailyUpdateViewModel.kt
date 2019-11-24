package com.cyuan.bimibimi.ui.home.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class DailyUpdateViewModel(private val repository: OnlineMovieRepository) : ViewModel() {

    var dailyUpdateList = MutableLiveData<List<List<Movie>>>()

    var viewState = MutableLiveData(Constants.ViewState.LOADING)

    fun fetchDailyUpdateMovie() {
        viewState.postValue(Constants.ViewState.LOADING)
        launch({
            val data = repository.fetchDailyUpdateMovie()
            dailyUpdateList.value = data
            if (dailyUpdateList.value.isNullOrEmpty() ) {
                viewState.postValue(Constants.ViewState.EMPTY)
            } else {
                viewState.postValue(Constants.ViewState.DONE)
            }
        }, {
            viewState.postValue(Constants.ViewState.ERROR)
            Toast.makeText(App.getContext(), it.message, Toast.LENGTH_SHORT).show()
        })
    }
}