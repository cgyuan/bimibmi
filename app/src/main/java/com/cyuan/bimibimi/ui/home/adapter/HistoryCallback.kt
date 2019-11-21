package com.cyuan.bimibimi.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.cyuan.bimibimi.model.History

class HistoryCallback: DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.cover == newItem.cover && oldItem.href == newItem.href
    }
}