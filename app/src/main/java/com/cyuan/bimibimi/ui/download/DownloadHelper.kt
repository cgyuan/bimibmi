package com.cyuan.bimibimi.ui.download

import android.content.Context
import android.os.Environment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.model.ITask
import com.cyuan.bimibimi.ui.download.viewmodel.DownloadViewModel
import com.hdl.m3u8.M3U8DownloadTask
import com.hdl.m3u8.bean.OnDownloadListener
import com.hdl.m3u8.utils.NetSpeedUtils
import com.xunlei.downloadlib.XLDownloadManager
import com.xunlei.downloadlib.XLTaskHelper
import java.util.*
import kotlin.collections.ArrayList

class DownloadHelper(
    val context: Context,
    private var iTask: ITask?
) {
    private var taskId: String? = null
    private var newTaskId: String? = null
    private var mDownloadingTasks = ArrayList<DownloadTaskInfo>()
    private lateinit var downloadViewModel: DownloadViewModel

    fun refreshTaskList() {
        downloadViewModel.mDownloadTasks.postValue(mDownloadingTasks)
    }


    fun addTask(taskInfo: DownloadTaskInfo) {
        val taskName = XLTaskHelper.instance().getFileName(taskInfo.taskUrl)
        val path = App.getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!.path
//        taskInfo.title = taskName
        taskInfo.filePath = "$path/$taskName"
        taskInfo.taskStatus = -1
        if (taskInfo.taskUrl.toLowerCase().endsWith("m3u8")) {
            if (taskInfo.taskId.isEmpty()) {
                taskId = UUID.randomUUID().toString()
            }
            val downloadTask = M3U8DownloadTask(taskId)

            downloadTask.saveFilePath = "$path/${taskInfo.title}"
            downloadTask.isClearTempDir = true
            downloadTask.download(taskInfo.taskUrl, object : OnDownloadListener {

                private var lastLength: Long = 0

                override fun onSuccess() {
                    taskInfo.taskStatus = 2
                }

                /**
                 * @param itemFileSize 单个文件大小
                 * @param totalTs 总文件个数
                 * @param curTs   已下载文件个数
                 */
                override fun onDownloading(itemFileSize: Long, totalTs: Int, curTs: Int) {
                    taskInfo.taskStatus = 1
//                    taskInfo.receiveSize = (itemFileSize * curTs).toString()
                    taskInfo.totalSize = (itemFileSize * totalTs).toString()
                }

                /**
                 * @param curLength 已下载大小
                 */
                override fun onProgress(curLength: Long) {
                    taskInfo.taskStatus = 1
                    taskInfo.speed = NetSpeedUtils.getInstance().displayFileSize(curLength - lastLength)
                    lastLength = curLength
                }

                override fun onError(p0: Throwable?) {
                    taskInfo.taskStatus = 3
                }

                override fun onStart() {
                    taskInfo.taskStatus = 1
                }


            })

        } else if (taskInfo.taskUrl.contains("magnet") || XLTaskHelper.instance().getFileName(taskInfo.taskUrl).endsWith("torrent")) {
            taskId = if (taskInfo.taskUrl.startsWith("magnet")) {
                addMagnetTask(taskInfo.taskUrl, path, taskInfo.title)
            } else {
                addMagnetTask(getRealUrl(taskInfo.taskUrl), path, taskInfo.title)
            }
        } else {
            taskId = addThunderTask(taskInfo.taskUrl, path, taskInfo.title)
        }
        taskInfo.taskId = taskId!!
        if(!mDownloadingTasks.map { task -> task.taskUrl }.contains(taskInfo.taskUrl)) {
            mDownloadingTasks.add(taskInfo)
        }
    }

    private fun addMagnetTask(url: String, savePath: String, fileName: String?): String? {
        try {
            taskId = XLTaskHelper.instance().addMagnetTask(url, savePath, fileName).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return taskId
    }

    private fun addThunderTask(url: String, savePath: String, fileName: String?): String? {
        try {
            taskId = XLTaskHelper.instance().addThunderTask(url, savePath, fileName).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val localUrl = XLTaskHelper.instance().getLoclUrl(savePath + XLTaskHelper.instance().getFileName(url))
        return taskId
    }


    /**
     * 迅雷thunder://地址与普通url地址转换
     * 其实迅雷的thunder://地址就是将普通url地址加前缀‘AA’、后缀‘ZZ’，再base64编码后得到的字符串
     *
     * @param url
     * @return
     */
    private fun getRealUrl(url: String): String {
        var targetUrl = url
        if (url.startsWith("thunder://")) {
            targetUrl = XLDownloadManager.getInstance().parserThunderUrl(url)
        }
        return targetUrl
    }

    fun initDownloadLiveData(activity: DownloadActivity) {
        downloadViewModel = ViewModelProviders.of(activity).get(DownloadViewModel::class.java)
        downloadViewModel.mDownloadTasks.postValue(mDownloadingTasks)
        downloadViewModel.mDownloadTasks.observe(activity, Observer {
            iTask?.updateIngTask(it)
        })
    }

    fun setITask(iTask: ITask?) {
        this.iTask = iTask
    }

    /**
     *  重启已存在列表中的任务，原理上也是新任务，不过不插入数据库，而是更新已有记录，先添加，拿到ID后再更新记录里的ID
     */
    fun resumeNormalTask(taskInfo: DownloadTaskInfo) {
        addTask(taskInfo)
    }

    companion object {
        @Volatile
        private var instance: DownloadHelper? = null
        fun getInstance(context: Context, iTask: ITask?): DownloadHelper {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance =
                            DownloadHelper(
                                context,
                                iTask
                            )
                        //初始化本地下载
                        XLTaskHelper.init(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }
}