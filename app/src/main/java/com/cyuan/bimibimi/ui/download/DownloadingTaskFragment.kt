package com.cyuan.bimibimi.ui.download

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cyuan.bimibimi.core.utils.Reflector
import com.cyuan.bimibimi.databinding.FragmentDonwloadTaskBinding
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.ui.download.adapter.DownloadingTaskAdapter
import com.cyuan.bimibimi.widget.MessageDialog
import com.hdl.m3u8.M3U8DownloadTask
import com.hdl.m3u8.utils.MUtils
import com.xunlei.downloadlib.XLTaskHelper
import java.io.File

class DownloadingTaskFragment: Fragment(), DownloadingTaskAdapter.OnItemClickListener{

    private lateinit var mAdapter: DownloadingTaskAdapter

    private lateinit var binding: FragmentDonwloadTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonwloadTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mAdapter = DownloadingTaskAdapter()
        binding.recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener(this)
    }

    fun refresh(taskInfos: MutableList<DownloadTaskInfo>?) {
        mAdapter.refresh(taskInfos)
    }

    override fun onClick(task: DownloadTaskInfo) {
        // 暂停，继续下载
        if (task.taskStatus == 0 || task.taskStatus == 4 || task.taskStatus == 3) {
            (activity as DownloadActivity).mDownloadHelper.resumeNormalTask(task)
        } else if (task.taskStatus != 2 || task.taskStatus != -1) {
            if (!task.taskUrl.toLowerCase().contains("m3u8")) {
                XLTaskHelper.instance().removeTask(task.taskId.toLong())
            } else {
                val m3U8DownloadTask = M3U8DownloadTask(task.taskId)
                m3U8DownloadTask.stop()
                task.taskStatus = 0
            }
        }
    }

    override fun onLongClick(task: DownloadTaskInfo) {
        MessageDialog.Builder(activity)
            .setMessage("确定删除当前任务吗？")
            .setListener(object : MessageDialog.OnListener {
                override fun confirm(dialog: Dialog?) {
                    if (task.taskUrl.toLowerCase().contains("m3u8")) {
                        val m3U8DownloadTask = M3U8DownloadTask(task.taskId)
                        m3U8DownloadTask.stop()
                        task.taskStatus = 0
                        val tempDir = Reflector.on(m3U8DownloadTask::class.java).field("tempDir").get<String>(m3U8DownloadTask)
                        MUtils.clearDir(File(tempDir))
                    } else {
                        File(task.filePath).delete()
                        File("${task.filePath}.js").delete()
                        XLTaskHelper.instance().removeTask(task.taskId.toLong())
                    }
                    val viewModel = (activity as DownloadActivity).viewModel
                    viewModel.deleteTask(task)
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                }
                override fun cancel(dialog: Dialog?) {}
            }).show()
    }
}