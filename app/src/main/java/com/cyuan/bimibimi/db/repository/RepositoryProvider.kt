package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.DownloadTaskDatabase
import com.cyuan.bimibimi.repository.OnlineMovieRepository

object RepositoryProvider {

    fun provideFavoriteMovieRepository(): FavoriteMovieRepository {
        val dao = AppDatabase.instance.favoriteMovieDao()
        return FavoriteMovieRepository.getInstance(dao)
    }

    fun provideDownloadTaskRepository(): DownloadTaskRepository {
        val dao = DownloadTaskDatabase.instance.downloadTaskDao()
        return DownloadTaskRepository.getInstance(dao)
    }

    fun provideHistoryRepository(): HistoryRepository {
        val dao = AppDatabase.instance.historyDao()
        return HistoryRepository.getInstance(dao)
    }
}