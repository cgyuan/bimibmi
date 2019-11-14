package com.cyuan.bimibimi.ui.home.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R

class RecommendMovieItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cover: ImageView = itemView.findViewById(R.id.cover)

    val title: TextView = itemView.findViewById(R.id.title)

    companion object {
        fun createViewHolder(context: Context, parent: ViewGroup): RecommendMovieItemHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.recommend_movie_item, parent, false)
            return RecommendMovieItemHolder(view)
        }
    }
}