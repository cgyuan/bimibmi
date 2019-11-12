package com.cyuan.bimibimi.ui.home.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R

class HomeSectionHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.name)
    val more = itemView.findViewById<TextView>(R.id.more)
}