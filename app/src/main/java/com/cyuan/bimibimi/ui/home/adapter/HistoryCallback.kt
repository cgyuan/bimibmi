package com.cyuan.bimibimi.ui.home.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.cyuan.bimibimi.model.History

class HistoryCallback: DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }
}