package com.cyuan.bimibimi.ui.download

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.utils.Reflector
import com.cyuan.bimibimi.databinding.FragmentDonwloadTaskBinding
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.ui.download.adapter.DownloadedTaskAdapter
import com.cyuan.bimibimi.ui.player.PlayerActivity
import com.cyuan.bimibimi.widget.MessageDialog
import com.hdl.m3u8.M3U8DownloadTask
import com.hdl.m3u8.utils.MUtils
import com.xunlei.downloadlib.XLTaskHelper
import java.io.File

class DownloadedTaskFragment: Fragment(), DownloadedTaskAdapter.OnItemClickListener {

    private lateinit var binding: FragmentDonwloadTaskBinding
    private val mAdapter by lazy {
        DownloadedTaskAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonwloadTaskBinding.inflate(inflater, container, false)
        return binding.recyclerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener(this)
    }

    fun refresh(taskInfos: MutableList<DownloadTaskInfo>?) {
        mAdapter.refresh(taskInfos)
    }

    override fun onClick(task: DownloadTaskInfo) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra(PlayerKeys.URL, task.filePath)
        intent.putExtra(PlayerKeys.MOVIE_TITLE, task.title)
        intent.putExtra(PlayerKeys.EPISODE_NAME, task.episodeName)
        intent.putExtra(PlayerKeys.DATA_SOURCE_INDEX, task.dataSourceIndex)
        intent.putExtra(PlayerKeys.EPISODE_INDEX, task.episodeIndex)
        intent.putExtra(PlayerKeys.MOVIE_DETAIL_HREF, task.href)
        intent.putExtra(PlayerKeys.MOVIE_COVER, task.coverUrl)
        startActivity(intent)
    }

    override fun onLongClick(task: DownloadTaskInfo) {
        MessageDialog.Builder(activity)
            .setMessage("确定删除已下载内容【${task.title}】${task.episodeName}吗？")
            .setListener(object : MessageDialog.OnListener {
                override fun confirm(dialog: Dialog?) {
                    if (!task.taskUrl.toLowerCase().endsWith("m3u8")) {
                        XLTaskHelper.instance().removeTask(task.taskId.toLong())
                    }
                    File(task.filePath).delete()
                    val viewModel = (activity as DownloadActivity).viewModel
                    viewModel.deleteTask(task)
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show()
                }
                override fun cancel(dialog: Dialog?) {}
            }).show()
    }
}