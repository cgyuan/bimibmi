package com.cyuan.bimibimi.ui.download.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.launch
import com.cyuan.bimibimi.core.utils.FileUtils
import com.cyuan.bimibimi.db.DownloadTaskDatabase
import com.cyuan.bimibimi.db.repository.DownloadTaskRepository
import com.cyuan.bimibimi.ui.download.DownloadHelper
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.xunlei.downloadlib.XLTaskHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DownloadViewModel(
    application: Application,
    val repository: DownloadTaskRepository
) : AndroidViewModel(application) {

    val mDownloadTasks = repository.getAllDownloadingTask()

    val mDownloadedTask = repository.getAllDownloadedTask()

    var loopFlag = true

    init {
        viewModelScope.launch {
            while (loopFlag) {
                syncTaskStatus()
                delay(3000)
            }
        }
    }

    private fun syncTaskStatus() {
        if (mDownloadTasks.value == null) return
        mDownloadTasks.value as List<DownloadTaskInfo>
        for (taskInfo in mDownloadTasks.value!!) {
            if (!taskInfo.taskUrl.toLowerCase().contains("m3u8")) {
                val xlTaskInfo = XLTaskHelper.instance().getTaskInfo(taskInfo.taskId.toLong())
                taskInfo.totalSize = xlTaskInfo.mFileSize.toString()
                taskInfo.taskStatus = xlTaskInfo.mTaskStatus
                taskInfo.receiveSize = xlTaskInfo.mDownloadSize.toString()
                taskInfo.speed = FileUtils.convertFileSize(xlTaskInfo.mDownloadSpeed)
                launch({
                    repository.saveTask(taskInfo)
                })
            }
        }
    }

    fun saveTask(taskInfo: DownloadTaskInfo) {
        launch({
            repository.saveTask(taskInfo)
        })
    }

    fun isTaskDownloading(taskInfo: DownloadTaskInfo) =
        repository.isTaskDownloading(taskInfo)
            .asLiveData(viewModelScope.coroutineContext)

    fun deleteTask(task: DownloadTaskInfo) {
        launch({
            repository.deleteTask(task)
        })
    }

    override fun onCleared() {
        super.onCleared()
        loopFlag = false
    }

}