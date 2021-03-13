package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.dao.DownloadTaskDao
import com.cyuan.bimibimi.model.DownloadTaskInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class DownloadTaskRepository private constructor(
    private val downloadTaskDao: DownloadTaskDao
){

    fun isTaskDownloading(taskInfo: DownloadTaskInfo) = flow {
        val isTaskDownloading = downloadTaskDao.isTaskDownloading(taskInfo.taskUrl, taskInfo.filePath)
        emit(isTaskDownloading)
    }.flowOn(Dispatchers.IO)

    fun getAllDownloadingTask() = downloadTaskDao.queryDownloadingTasks()

    fun getAllDownloadedTask() = downloadTaskDao.queryFinishTasks()

    suspend fun saveTask(taskInfo: DownloadTaskInfo) = withContext(Dispatchers.IO) {
        val record = downloadTaskDao.queryTaskByUrl(taskInfo.taskUrl)
        if (record != null) {
            taskInfo.id = record.id
            downloadTaskDao.update(taskInfo)
        } else {
            downloadTaskDao.insert(taskInfo)
        }
    }

    fun getFinishedTask(url: String) = flow {
        val res = downloadTaskDao.queryFinishTaskByEpisodeUrl(url)
        emit(res)
    }.flowOn(Dispatchers.IO)

    suspend fun deleteTask(taskInfo: DownloadTaskInfo) = withContext(Dispatchers.IO) {
        downloadTaskDao.deleteTaskByUrl(taskInfo.taskUrl)
    }

    companion object {
        @Volatile
        private var instance: DownloadTaskRepository? = null

        fun getInstance(downloadTaskDao: DownloadTaskDao) : DownloadTaskRepository {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = DownloadTaskRepository(downloadTaskDao)
                    }
                }
            }
            return instance!!
        }
    }
}