package com.cyuan.bimibimi.ui.detail.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.BimibimiApp
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity
import com.cyuan.bimibimi.ui.detail.holder.OnlinePlayHolder
import com.cyuan.bimibimi.ui.download.DownloadHelper
import com.cyuan.bimibimi.ui.player.PlayerActivity
import com.cyuan.bimibimi.widget.MessageDialog
import per.goweii.anylayer.AnyLayer
import per.goweii.anylayer.DialogLayer

class OnlinePlayListAdapter(private val context: Context,
                            private val episodes: ArrayList<Episode>,
                            private val movieDetail: MovieDetail,
                            var dataSourceIndex: Int,
                            @DrawableRes private val bgSector: Int = 0):
    RecyclerView.Adapter<OnlinePlayHolder>() {


    private var mLoadingDlg: DialogLayer = AnyLayer.dialog(context)
        .contentView(R.layout.dialog_parse_video_loading)
        .gravity(Gravity.CENTER)
        .cancelableOnTouchOutside(false)
        .cancelableOnClickKeyBack(true)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlinePlayHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.online_play_btn, parent, false)
        return OnlinePlayHolder(view)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }



    override fun onBindViewHolder(holder: OnlinePlayHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        val episode = episodes[adapterPosition]
        holder.btPlayText.text = episode.title
        if (bgSector != 0) {
            holder.btPlayText.setBackgroundResource(bgSector)
        }
        val dataSourceName = movieDetail.dataSources[dataSourceIndex].name
        holder.itemView.setOnClickListener {
            mLoadingDlg.show()
            context as MovieDetailActivity
            context.viewModel.getFinishedTaskByEpisodeHref(
                context, episode, episode.href, "",
                onError = {
                    mLoadingDlg.dismiss()
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
                .observe(context) { data ->
                    mLoadingDlg.dismiss()
                    val url: String = data!!
                    val intent = Intent(context, PlayerActivity::class.java)
                    intent.putExtra(PlayerKeys.URL, url)
                    intent.putExtra(PlayerKeys.MOVIE_TITLE, movieDetail.title)
                    intent.putExtra(PlayerKeys.EPISODE_NAME, episode.title)
                    intent.putExtra(PlayerKeys.DATA_SOURCE_INDEX, dataSourceIndex)
                    intent.putExtra(PlayerKeys.EPISODE_INDEX, adapterPosition)
                    intent.putExtra(PlayerKeys.DATA_SOURCE_NAME, dataSourceName)
                    intent.putExtra(PlayerKeys.MOVIE_DETAIL_HREF, movieDetail.href)
                    intent.putExtra(PlayerKeys.MOVIE_COVER, movieDetail.cover)
                    intent.putParcelableArrayListExtra(PlayerKeys.EPISODE_LIST, episodes)
                    context.startActivity(intent)
                }
        }
        holder.itemView.setOnLongClickListener {
            mLoadingDlg.show()
            context as MovieDetailActivity
            context.viewModel.getFinishedTaskByEpisodeHref(
                context, episode, episode.href, "",
                onError = {
                    mLoadingDlg.dismiss()
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
                .observe(context) { data ->
                    mLoadingDlg.dismiss()
                    MessageDialog.Builder((App.getContext() as BimibimiApp).getCurrentActivity())
                        .setMessage("缓存视频？")
                        .setListener(object : MessageDialog.OnListener {
                            override fun confirm(dialog: Dialog?) {
                                val taskInfo = DownloadTaskInfo()
                                taskInfo.taskUrl = data!!
                                taskInfo.coverUrl = movieDetail.cover
                                taskInfo.receiveSize = "0"
                                taskInfo.totalSize = "0"
                                taskInfo.title = movieDetail.title
                                taskInfo.dataSourceIndex = dataSourceIndex
                                taskInfo.episodeIndex = adapterPosition
                                taskInfo.episodeName = episode.title
                                taskInfo.href = movieDetail.href
                                taskInfo.episodeHref = episode.href
                                if (!GlobalUtil.isX86Device()) {
                                    val downloadHelper = DownloadHelper.getInstance(context.applicationContext)
                                    downloadHelper.addTask(taskInfo)
                                }
                            }

                            override fun cancel(dialog: Dialog?) {}
                        }).show()
                }
            true
        }
    }

    fun refreshData(episodes: MutableList<Episode>) {
        this.episodes.clear()
        this.episodes.addAll(episodes)
        this.notifyDataSetChanged()
    }
}