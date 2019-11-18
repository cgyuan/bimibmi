package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.home.holder.BannerHolder
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity

class BannerAdapter(private val context: Context, recentUpdate: MutableList<Movie>) :
    RecyclerView.Adapter<BannerHolder>() {
    private val movies: List<Movie>?

    init {
        this.movies = recentUpdate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false)
        return BannerHolder(view)
    }

    override fun onBindViewHolder(holder: BannerHolder, position: Int) {
        if (movies == null) {
            return
        }
        val video = movies[position]
        val url = video.cover
        val requestOptions = RequestOptions().placeholder(R.drawable.ic_place_hoder)
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade(300))
            .apply(requestOptions).into(holder.iv)
        holder.title.text = video.title

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra("movie", video)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movies?.size ?: 0
    }

}
