package com.cyuan.bimibimi.ui.home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.databinding.MovieCardItemLayoutBinding
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.ui.player.OnlinePlayerActivity

class HistoryAdapter: PagedListAdapter<History, HistoryAdapter.HistoryItemHolder>(HistoryCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemHolder {
        val binding = MovieCardItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryItemHolder(binding.root)
    }

    override fun onBindViewHolder(holder: HistoryItemHolder, position: Int) {
        val history = getItem(position)!!
        Glide.with(holder.cover).load(history.cover).placeholder(R.drawable.ic_default_grey).into(holder.cover)
        holder.title.text = history.title
        holder.label.text = history.label
        holder.itemView.setOnClickListener {
            val intent = Intent(App.getContext(), OnlinePlayerActivity::class.java)
            intent.putExtra(PlayerKeys.URL, history.url)
            intent.putExtra(PlayerKeys.MOVIE_TITLE, history.title)
            intent.putExtra(PlayerKeys.EPISODE_NAME, "")
            intent.putExtra(PlayerKeys.EPISODE_INDEX, position)
            intent.putExtra(PlayerKeys.MOVIE_DETAIL_HREF, history.href)
            intent.putExtra(PlayerKeys.MOVIE_COVER, history.cover)
//            intent.putParcelableArrayListExtra(PlayerKeys.EPISODE_LIST, episodes)
//            val mv = Movie(history.href, history.title, history.cover, "", history.label)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra(PlayerKeys.MOVIE, mv)
            App.getContext().startActivity(intent)
        }
    }


    class HistoryItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
        val title: TextView = itemView.findViewById(R.id.title)
        val label: TextView = itemView.findViewById(R.id.label)
    }
}