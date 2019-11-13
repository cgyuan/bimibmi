package com.cyuan.bimibimi.ui.base

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R


/**
 * 用于在RecyclerView当中显示更加更多的进度条。
 */
class LoadingMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val progress: ProgressBar = itemView.findViewById(R.id.loadProgress)

    val end: ImageView = itemView.findViewById(R.id.loadingEnd)

    val failed: TextView = itemView.findViewById(R.id.loadFailed)
}