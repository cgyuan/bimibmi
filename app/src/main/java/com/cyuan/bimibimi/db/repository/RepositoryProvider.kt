package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.DownloadTaskDatabase

object RepositoryProvider {

    fun providerFavoriteMovieRepository(): FavoriteMovieRepository {
        val dao = AppDatabase.instance.favoriteMovieDao()
        return FavoriteMovieRepository.getInstance(dao)
    }

    fun providerDownloadTaskRepository(): DownloadTaskRepository {
        val dao = DownloadTaskDatabase.instance.downloadTaskDao()
        return DownloadTaskRepository.getInstance(dao)
    }
}