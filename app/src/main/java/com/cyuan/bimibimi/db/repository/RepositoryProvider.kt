package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.DownloadTaskDatabase

object RepositoryProvider {

    fun providerDownloadTaskRepository(): DownloadTaskRepository {
        val dao = DownloadTaskDatabase.instance.downloadTaskDao()
        return DownloadTaskRepository.getInstance(dao)
    }
}