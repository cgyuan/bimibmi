package com.cyuan.bimibimi.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.paging.AsyncPagedListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.core.extension.logInfo
import com.cyuan.bimibimi.core.utils.Reflector

abstract class BasePagedListAdapter<T, VH : RecyclerView.ViewHolder>(
    private val context: Context,
    @LayoutRes val itemLayoutId: Int,
    @NonNull diffCallback: ItemCallback<T>,
    @LayoutRes val headerLayoutId: Int?,
    @LayoutRes val footerLayoutId: Int?
) : PagedListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {
    val VIEW_TYPE_HEADER = 0
    val VIEW_TYPE_ITEM = 1
    val VIEW_TYPE_FOOTER = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root = when (viewType) {
            VIEW_TYPE_HEADER -> LayoutInflater.from(context)
                .inflate(headerLayoutId!!, parent, false)
            VIEW_TYPE_ITEM -> LayoutInflater.from(context)
                .inflate(itemLayoutId, parent, false)
            else -> LayoutInflater.from(context)
                .inflate(footerLayoutId!!, parent, false)
        }
        return onCreateViewHolder(root, viewType)
    }

    abstract fun onCreateViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            onBindHeaderHolder(holder, position)
        } else if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
            onBindFooterHolder(holder, position)
        } else {
            val pos = if (headerLayoutId != null) position - 1 else position
            onBindItemHolder(holder, pos)
        }
    }

    open fun onBindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    open fun onBindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    open fun onBindHeaderHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemViewType(position: Int): Int {
        return if(position == 0 && headerLayoutId != null) {
            VIEW_TYPE_HEADER
        } else if (position == (itemCount - 1) && footerLayoutId != null) {
            VIEW_TYPE_FOOTER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        var itemCount = super.getItemCount()
        if (headerLayoutId != null) {
            itemCount += 1
        }
        if (footerLayoutId != null) {
            itemCount += 1
        }
        return itemCount
    }
}