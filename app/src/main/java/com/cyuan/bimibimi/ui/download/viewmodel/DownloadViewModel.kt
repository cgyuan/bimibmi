package com.cyuan.bimibimi.ui.download.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.FileUtils
import com.cyuan.bimibimi.ui.download.DownloadHelper
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.xunlei.downloadlib.XLTaskHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    val mDownloadTasks = MutableLiveData<ArrayList<DownloadTaskInfo>>(ArrayList())
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
        DownloadHelper.getInstance(App.getContext(), null).refreshTaskList()
        mDownloadTasks.value as List<DownloadTaskInfo>
        for (i in 0 until mDownloadTasks.value!!.size) {
            val taskInfo = mDownloadTasks.value!![i]
            if (!taskInfo.taskUrl.toLowerCase().endsWith("m3u8")) {
                val xlTaskInfo = XLTaskHelper.instance().getTaskInfo(taskInfo.taskId.toLong())
                taskInfo.totalSize = xlTaskInfo.mFileSize.toString()
                taskInfo.taskStatus = xlTaskInfo.mTaskStatus
                taskInfo.receiveSize = xlTaskInfo.mDownloadSize.toString()
                taskInfo.speed = FileUtils.convertFileSize(xlTaskInfo.mDownloadSpeed)
            }
        }
        DownloadHelper.getInstance(App.getContext(), null).refreshTaskList()
    }

    override fun onCleared() {
        super.onCleared()
        loopFlag = false
    }

}