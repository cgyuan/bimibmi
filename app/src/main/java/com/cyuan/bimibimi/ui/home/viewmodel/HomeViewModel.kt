package com.cyuan.bimibimi.ui.home.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.Section
import com.cyuan.bimibimi.repository.OnlineMovieRepository

class HomeViewModel(private val repository: OnlineMovieRepository) : ViewModel() {

    var bannerList = MutableLiveData<List<Movie>>()

    var sectionList = MutableLiveData<List<Section>>()

    fun fetchHomeData() {
        launch({
            val data = repository.fetchHomeInfo()
            bannerList.value = data.first
            sectionList.value = data.second
        }, {
            Toast.makeText(App.getContext(), it.message, Toast.LENGTH_SHORT).show()
        })
    }
}