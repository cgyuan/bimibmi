package com.cyuan.bimibimi.ui.home.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.core.utils.GlobalUtil
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
            // 加载数据失败，尝试更换资源
            if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
                PreferenceManager.getDefaultSharedPreferences(App.getContext())
                    .edit().putString(Constants.HOST, Constants.HALITV_INDEX).apply()
                GlobalUtil.hostCache = Constants.HALITV_INDEX
            } else {
                PreferenceManager.getDefaultSharedPreferences(App.getContext())
                    .edit().putString(Constants.HOST, Constants.BIMIBIMI_INDEX).apply()
                GlobalUtil.hostCache = Constants.BIMIBIMI_INDEX
            }
            fetchHomeData()
        })
    }
}