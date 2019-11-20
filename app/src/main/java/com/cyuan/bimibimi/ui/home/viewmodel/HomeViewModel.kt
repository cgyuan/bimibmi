package com.cyuan.bimibimi.ui.home.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.Section
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class HomeViewModel(private val repository: OnlineMovieRepository) : ViewModel() {

    var bannerList = MutableLiveData<List<Movie>>()

    var sectionList = MutableLiveData<List<Section>>()

    var viewState = MutableLiveData(Constants.ViewState.LOADING)

    fun fetchHomeData() {
        viewState.postValue(Constants.ViewState.LOADING)
        launch({
            val data = repository.fetchHomeInfo()
            bannerList.value = data.bannerList
            sectionList.value = data.sectionList
            if (bannerList.value.isNullOrEmpty() || sectionList.value.isNullOrEmpty()) {
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