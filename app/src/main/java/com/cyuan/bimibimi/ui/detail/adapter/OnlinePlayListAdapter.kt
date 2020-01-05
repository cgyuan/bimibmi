package com.cyuan.bimibimi.ui.detail.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.BimibimiApp
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.ui.download.DownloadHelper
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.cyuan.bimibimi.ui.detail.holder.OnlinePlayHolder
import com.cyuan.bimibimi.ui.player.OnlinePlayerActivity
import com.cyuan.bimibimi.widget.MessageDialog
import java.util.*

class OnlinePlayListAdapter(private val context: Context,
                            private val episodes: ArrayList<Episode>,
                            private val movieDetail: MovieDetail,
                            var dataSourceIndex: Int,
                            @DrawableRes private val bgSector: Int = 0):
    RecyclerView.Adapter<OnlinePlayHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlinePlayHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.online_play_btn, parent, false)
        return OnlinePlayHolder(view)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    override fun onBindViewHolder(holder: OnlinePlayHolder, position: Int) {
        holder.btPlayText.text = episodes[position].title
        if (bgSector != 0) {
            holder.btPlayText.setBackgroundResource(bgSector)
        }
        holder.itemView.setOnClickListener {
            HtmlDataParser.parseVideoSource(context, episodes[position], object : ParseResultCallback<String> {
                override fun onSuccess(data: String) {
                    val url: String = data
                    val intent = Intent(context, OnlinePlayerActivity::class.java)
                    intent.putExtra(PlayerKeys.URL, url)
                    intent.putExtra(PlayerKeys.MOVIE_TITLE, movieDetail.title)
                    intent.putExtra(PlayerKeys.EPISODE_NAME, episodes[position].title)
                    intent.putExtra(PlayerKeys.DATA_SOURCE_INDEX, dataSourceIndex)
                    intent.putExtra(PlayerKeys.EPISODE_INDEX, position)
                    intent.putExtra(PlayerKeys.MOVIE_DETAIL_HREF, movieDetail.href)
                    intent.putExtra(PlayerKeys.MOVIE_COVER, movieDetail.cover)
                    intent.putParcelableArrayListExtra(PlayerKeys.EPISODE_LIST, episodes)
                    context.startActivity(intent)
                }

                override fun onFail(msg: String) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            })
        }
        holder.itemView.setOnLongClickListener {
            HtmlDataParser.parseVideoSource(context, episodes[position], object : ParseResultCallback<String> {
                override fun onSuccess(data: String) {
                    App.getHandler().post {
                        MessageDialog.Builder((App.getContext() as BimibimiApp).getCurrentActivity())
                            .setMessage("缓存视频？")
                            .setListener(object : MessageDialog.OnListener {
                                override fun confirm(dialog: Dialog?) {
                                    val taskInfo = DownloadTaskInfo()
                                    taskInfo.taskUrl = data
                                    taskInfo.coverUrl = movieDetail.cover
                                    taskInfo.receiveSize = "0"
                                    taskInfo.totalSize = "0"
                                    taskInfo.title = movieDetail.title
                                    taskInfo.dataSourceIndex = dataSourceIndex
                                    taskInfo.episodeIndex = position
                                    taskInfo.episodeName = episodes[position].title
                                    taskInfo.href = movieDetail.href
                                    taskInfo.episodeHref = episodes[position].href
                                    val downloadHelper = DownloadHelper.getInstance(context, null)
                                    downloadHelper.addTask(taskInfo)
                                }

                                override fun cancel(dialog: Dialog?) {}
                            }).show()
                    }
                }

                override fun onFail(msg: String) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            })
            true
        }
    }

    fun refreshData(episodes: MutableList<Episode>) {
        this.episodes.clear()
        this.episodes.addAll(episodes)
        this.notifyDataSetChanged()
    }
}