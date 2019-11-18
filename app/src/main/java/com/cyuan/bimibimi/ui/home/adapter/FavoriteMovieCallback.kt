package com.cyuan.bimibimi.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.cyuan.bimibimi.model.FavoriteMovie

class FavoriteMovieCallback: DiffUtil.ItemCallback<FavoriteMovie>() {
    override fun areItemsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteMovie, newItem: FavoriteMovie): Boolean {
        return oldItem.cover == newItem.cover && oldItem.href == newItem.href
    }
}