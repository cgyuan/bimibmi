package com.cyuan.bimibimi.ui.download

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.ui.download.adapter.DownloadedTaskAdapter
import com.cyuan.bimibimi.ui.player.OnlinePlayerActivity
import kotlinx.android.synthetic.main.fragment_donwload_task.*

class DownloadedTaskFragment: Fragment(), DownloadedTaskAdapter.OnItemClickListener {

    private val mAdapter by lazy {
        DownloadedTaskAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donwload_task, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener(this)
    }

    fun refresh(taskInfos: MutableList<DownloadTaskInfo>?) {
        mAdapter.refresh(taskInfos)
    }

    override fun onClick(task: DownloadTaskInfo) {
        val intent = Intent(context, OnlinePlayerActivity::class.java)
        intent.putExtra(PlayerKeys.URL, task.filePath)
        intent.putExtra(PlayerKeys.MOVIE_TITLE, task.title)
        intent.putExtra(PlayerKeys.EPISODE_NAME, task.episodeName)
        intent.putExtra(PlayerKeys.DATA_SOURCE_INDEX, task.dataSourceIndex)
        intent.putExtra(PlayerKeys.EPISODE_INDEX, task.episodeIndex)
        intent.putExtra(PlayerKeys.MOVIE_DETAIL_HREF, task.href)
        intent.putExtra(PlayerKeys.MOVIE_COVER, task.coverUrl)
        startActivity(intent)
    }
}