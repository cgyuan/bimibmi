package com.cyuan.bimibimi.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.DateUtils
import com.cyuan.bimibimi.databinding.HistoryCardItemLayoutBinding
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModel
import com.cyuan.bimibimi.ui.player.PlayerActivity
import kotlinx.coroutines.launch

class HistoryAdapter(
    private val viewModel: HistoryViewModel,
    private val longClickCallback: View.OnLongClickListener?
): PagedListAdapter<History, HistoryAdapter.HistoryItemHolder>(HistoryCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemHolder {
        val binding = HistoryCardItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryItemHolder(binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryItemHolder, position: Int) {
        val history = getItem(position)!!
        Glide.with(holder.cover).load(history.cover).placeholder(R.drawable.ic_default_grey).into(holder.cover)
        holder.title.text = "【${history.title}】${history.episodeName}"
        holder.label.text = "已看到${DateUtils.formatElapsedTime(null, history.position / 1000, "%1\$d小时%2\$02d分%3\$02d秒", "%1\$02d分%2\$02d秒")}"

        holder.itemView.setOnClickListener {
            viewModel.viewModelScope.launch {
                var movieDetail: MovieDetail? = null
                try {
                    movieDetail = viewModel.fetchMovieDetail(history.href)
                } catch (e: Exception) {
                    Toast.makeText(App.getContext(), e.message, Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(App.getContext(), PlayerActivity::class.java)
                intent.putExtra(PlayerKeys.MOVIE_DETAIL_HREF, history.href)
                intent.putExtra(PlayerKeys.URL, history.url)
                intent.putExtra(PlayerKeys.MOVIE_TITLE, history.title)
                intent.putExtra(PlayerKeys.EPISODE_NAME, history.episodeName)
                intent.putExtra(PlayerKeys.EPISODE_INDEX, history.episodeIndex)
                intent.putExtra(PlayerKeys.MOVIE_COVER, history.cover)
                intent.putExtra(PlayerKeys.PLAY_POSITION, history.position)
                intent.putParcelableArrayListExtra(PlayerKeys.EPISODE_LIST, movieDetail?.dataSources?.get(history.dataSourceIndex)?.episodes)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                App.getContext().startActivity(intent)
            }
        }
        holder.itemView.tag = history
        holder.itemView.setOnLongClickListener(longClickCallback)
    }


    class HistoryItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
        val title: TextView = itemView.findViewById(R.id.title)
        val label: TextView = itemView.findViewById(R.id.label)
    }
}