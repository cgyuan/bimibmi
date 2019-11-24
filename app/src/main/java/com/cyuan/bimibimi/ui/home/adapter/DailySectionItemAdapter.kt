package com.cyuan.bimibimi.ui.home.adapter

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

class DailySectionItemAdapter(private val movieList: List<Movie>): RecyclerView.Adapter<DailySectionItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DailyUpdateMovieCardItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun getItemCount() = movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
        val title: TextView = itemView.findViewById(R.id.title)
        val label: TextView = itemView.findViewById(R.id.label)
    }
}