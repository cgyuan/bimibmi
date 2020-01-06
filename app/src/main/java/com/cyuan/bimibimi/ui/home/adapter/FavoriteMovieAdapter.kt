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
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity

class FavoriteMovieAdapter(
    private val longClickCallback: View.OnLongClickListener? = null
): PagedListAdapter<FavoriteMovie, FavoriteMovieAdapter.FavoriteItemHolder>(FavoriteMovieCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemHolder {
        val binding = MovieCardItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteItemHolder(binding.root)
    }

    override fun onBindViewHolder(holder: FavoriteItemHolder, position: Int) {
        val movie = getItem(position)!!
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
        holder.itemView.tag = movie
        holder.itemView.setOnLongClickListener(longClickCallback)
    }


    class FavoriteItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
        val title: TextView = itemView.findViewById(R.id.title)
        val label: TextView = itemView.findViewById(R.id.label)
    }
}