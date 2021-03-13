package com.cyuan.bimibimi.ui.player.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.db.repository.DownloadTaskRepository
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.db.repository.RepositoryProvider
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.repository.OnlineMovieRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class PlayerViewModel(
    private val repository: HistoryRepository,
    private val downloadTaskRepository: DownloadTaskRepository,
    private val onlineMovieRepository: OnlineMovieRepository
): ViewModel() {

    fun saveHistory(history: History) {
        launch({
            repository.saveHistory(history)
        })
    }

    fun getFinishedTaskByEpisodeHref(
        context: Context,
        episode: Episode,
        href: String,
        dataSourceName: String
    ): LiveData<String?> {
        return downloadTaskRepository.getFinishedTask(href)
            .map {
                var res = it?.filePath
                res ?: onlineMovieRepository.parseVideoSource(context, episode, dataSourceName)
                    .collect { url ->
                        res = url
                    }
                res
            }.asLiveData(viewModelScope.coroutineContext)
    }


    companion object {
        fun provideFactory() = object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PlayerViewModel(
                    RepositoryProvider.provideHistoryRepository(),
                    RepositoryProvider.provideDownloadTaskRepository(),
                    OnlineMovieRepository.instance
                ) as T
            }

        }
    }
}