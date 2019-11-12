package com.cyuan.bimibimi.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseVideoCallback
import com.cyuan.bimibimi.ui.home.holder.OnlinePlayHolder
import com.cyuan.bimibimi.ui.player.OnlinePlayerActivity
import java.util.ArrayList

class OnlinePlayListAdapter(private val context: Context,
                            private val episodes: ArrayList<Episode>,
                            private val movieTitle: String,
                            private val bgSector: Int = 0):
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
            HtmlDataParser.parseVideoSource(context, episodes[position], object : ParseVideoCallback {
                override fun onSuccess(url: String) {
                    val intent = Intent(context, OnlinePlayerActivity::class.java)
                    intent.putExtra(PlayerKeys.URL, url)
                    intent.putExtra(PlayerKeys.MOVIE_TITLE, movieTitle)
                    intent.putExtra(PlayerKeys.EPISODE_NAME, episodes[position].title)
                    intent.putExtra(PlayerKeys.EPISODE_INDEX, position)
                    intent.putParcelableArrayListExtra(PlayerKeys.EPISODE_LIST, episodes)
                    context.startActivity(intent)
                }

                override fun onFail(msg: String) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun refreshData(episodes: MutableList<Episode>) {
        this.episodes.clear()
        this.episodes.addAll(episodes)
        this.notifyDataSetChanged()
    }
}