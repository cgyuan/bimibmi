package com.cyuan.bimibimi.ui.player

import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R

class EpisodeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val btn = itemView.findViewById<Button>(R.id.play_btn)
}