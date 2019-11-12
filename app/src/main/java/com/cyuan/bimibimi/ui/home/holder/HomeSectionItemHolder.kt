package com.cyuan.bimibimi.ui.home.holder

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R

class HomeSectionItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val cover = itemView.findViewById<AppCompatImageView>(R.id.cover)
    val title = itemView.findViewById<TextView>(R.id.title)
    val label = itemView.findViewById<TextView>(R.id.label)
}