package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.databinding.DailyUpdateMovieCardItemLayoutBinding
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity

class DailySectionAdapter(
    private val context: Context,
    private val dayOfWeeks: List<String>,
    private val movieLists: List<List<Movie>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_TOP_BAR_LABEL = "VIEW_TYPE_TOP_BAR"
    companion object {
        const val VIEW_TYPE_TOP_BAR = 1
        const val VIEW_TYPE_CARD = 2
    }

    private val movieList = mutableListOf<Movie>().apply {
        movieLists.forEachIndexed { index, list ->
            val movie = Movie()
            movie.title = dayOfWeeks[index]
            movie.label = VIEW_TYPE_TOP_BAR_LABEL
            add(movie)
            addAll(list)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CARD) {
            val binding = DailyUpdateMovieCardItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ViewHolder(binding.root)
        } else {
            TopBarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.daily_update_top_bar, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (movieList[position].label == VIEW_TYPE_TOP_BAR_LABEL) {
            return VIEW_TYPE_TOP_BAR
        }
        return VIEW_TYPE_CARD
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TopBarViewHolder) {
            holder.titleView.text = "星期${movieList[position].title}"
        } else {
            holder as ViewHolder
            val movie = movieList[position]
            Glide.with(holder.cover).load(movie.cover).placeholder(R.drawable.ic_default_grey).into(holder.cover)
            holder.title.text = movie.title
            holder.label.text = movie.label
            holder.itemView.setOnClickListener {
                val intent = Intent(App.getContext(), MovieDetailActivity::class.java)
                val mv = Movie(movie.href, movie.title, movie.cover, "", movie.label)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(PlayerKeys.MOVIE, mv)
                App.getContext().startActivity(intent)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
        val title: TextView = itemView.findViewById(R.id.title)
        val label: TextView = itemView.findViewById(R.id.label)
    }


    class TopBarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.dayOfWeek)
    }
}