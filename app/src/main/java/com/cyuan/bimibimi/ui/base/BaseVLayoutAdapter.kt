package com.cyuan.bimibimi.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper

abstract class BaseVLayoutAdapter<DATA>
    (val context: Context,
     private val dataList: List<DATA>,
     @LayoutRes val itemLayoutId: Int,
     @LayoutRes val headerLayoutId: Int?,
     @LayoutRes val footerLayoutId: Int?): DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {


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
            onBindItemHolder(holder, position)
            if (holder is ViewHolder<*>) {
                holder as ViewHolder<DATA>
                val pos = if (headerLayoutId != null) position - 1 else position
                holder.bindData(dataList[pos])
            }
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
        var itemCount = dataList.size
        if (headerLayoutId != null) {
            itemCount += 1
        }
        if (footerLayoutId != null) {
            itemCount += 1
        }
        return itemCount
    }

    override fun onCreateLayoutHelper(): LayoutHelper {

        val layoutHelper = getLayoutHelper()

        if (layoutHelper is GridLayoutHelper) {
            layoutHelper.setSpanSizeLookup(object : GridLayoutHelper.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val pos = position - startPosition
                    return if (getItemViewType(pos) == VIEW_TYPE_HEADER ||
                        getItemViewType(pos) == VIEW_TYPE_FOOTER) {
                                layoutHelper.spanCount
                            } else 1
                }
            })
        }

        return layoutHelper
    }

    abstract fun getLayoutHelper(): LayoutHelper

    abstract class ViewHolder<DATA>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: DATA? = null
        fun bindData(data: DATA) {
            this.data = data
            onBind(data)
        }
        abstract fun onBind(data: DATA)
    }
}