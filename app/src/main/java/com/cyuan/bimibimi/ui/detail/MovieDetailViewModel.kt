package com.cyuan.bimibimi.ui.detail

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.db.repository.DownloadTaskRepository
import com.cyuan.bimibimi.db.repository.FavoriteMovieRepository
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.repository.OnlineMovieRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class MovieDetailViewModel(
        private val repository: OnlineMovieRepository,
        private val favoriteMovieRepository: FavoriteMovieRepository,
        private val downloadTaskRepository: DownloadTaskRepository
    ) : ViewModel() {

    val movieDetail = MutableLiveData<MovieDetail>()
    var viewState = MutableLiveData(Constants.ViewState.DONE)
    var isLoading = MutableLiveData(true)
    val isFavorite = MutableLiveData(false)

    fun fetchMovieDetail(url: String) {
        isLoading.value = true
        viewState.value = Constants.ViewState.DONE
        launch({
            val detail = repository.fetchMovieDetail(url)
            movieDetail.value = detail
            viewState.value = Constants.ViewState.DONE
            isLoading.value = false
        }, {
            Toast.makeText(App.getContext(), it.message, Toast.LENGTH_SHORT).show()
            viewState.value = Constants.ViewState.ERROR
            isLoading.value = false
        })
    }

    fun isFavorite(movie: Movie) {
        launch({
            isFavorite.value = favoriteMovieRepository.isFavorite(movie)
        })
    }

    fun removeFavoriteMovie(movie: Movie) {
        launch({
            favoriteMovieRepository.removeMovie(movie)
        })
    }

    fun addFavoriteMovie(movie: Movie) {
        launch({
            favoriteMovieRepository.addMovie(movie)
        })
    }

    fun getFinishedTaskByEpisodeHref(
        context: Context,
        episode: Episode,
        href: String,
        dataSourceName: String = "",
        onError: (String?) -> Unit = {}
    ): LiveData<String?> {
        return downloadTaskRepository.getFinishedTask(href)
            .map {
                var res = it?.filePath
                res ?: repository.parseVideoSource(context, episode, dataSourceName)
                    .collect { url ->
                        res = url
                    }
                res
            }.catch { onError(it.message) }
            .asLiveData(viewModelScope.coroutineContext)
    }
}