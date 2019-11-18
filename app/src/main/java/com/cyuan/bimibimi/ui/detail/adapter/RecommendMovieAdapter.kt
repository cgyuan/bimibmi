package com.cyuan.bimibimi.ui.detail.adapter

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity
import com.cyuan.bimibimi.ui.detail.holder.RecommendMovieItemHolder

class RecommendMovieAdapter(
    private val context: Context,
    private val movieList: List<Movie>
): RecyclerView.Adapter<RecommendMovieItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecommendMovieItemHolder.createViewHolder(
            context,
            parent
        )

    override fun getItemCount() = movieList.size

    override fun onBindViewHolder(holder: RecommendMovieItemHolder, position: Int) {
        val movie = movieList[position]
        Glide.with(holder.cover).load(movie.cover)
            .placeholder(R.drawable.ic_default_grey)
            .centerCrop()
            .into(holder.cover)
        holder.title.text = movie.title
        holder.itemView.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra(PlayerKeys.MOVIE, movie)
            context.startActivity(intent)
        }
    }
}