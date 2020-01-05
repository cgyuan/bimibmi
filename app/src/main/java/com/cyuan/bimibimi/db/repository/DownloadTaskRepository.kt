package com.cyuan.bimibimi.db.repository

import com.cyuan.bimibimi.db.dao.DownloadTaskDao
import com.cyuan.bimibimi.model.DownloadTaskInfo

class DownloadTaskRepository private constructor(
    private val downloadTaskDao: DownloadTaskDao
){

    fun isTaskDownloading(taskInfo: DownloadTaskInfo) = downloadTaskDao.isTaskDownloading(taskInfo.taskUrl, taskInfo.filePath)

    fun getAllDownloadingTask() = downloadTaskDao.queryDownloadingTasks()

    fun getAllDownloadedTask() = downloadTaskDao.queryFinishTasks()

    fun saveTask(taskInfo: DownloadTaskInfo) {
        val record = downloadTaskDao.queryTaskByUrl(taskInfo.taskUrl)
        if (record != null) {
            taskInfo.id = record.id
            downloadTaskDao.update(taskInfo)
        } else {
            downloadTaskDao.insert(taskInfo)
        }
    }

    fun getFinishedTask(url: String) = downloadTaskDao.queryFinishTaskByEpisodeUrl(url)

    fun deleteTask(taskInfo: DownloadTaskInfo) = downloadTaskDao.deleteTaskByUrl(taskInfo.taskUrl)

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