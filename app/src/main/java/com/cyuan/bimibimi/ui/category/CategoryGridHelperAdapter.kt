package com.cyuan.bimibimi.ui.category

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.LayoutHelper
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.BaseVLayoutAdapter
import com.cyuan.bimibimi.ui.base.LayoutHelperFactory
import com.cyuan.bimibimi.ui.base.LoadingMoreViewHolder
import com.cyuan.bimibimi.ui.home.holder.HomeSectionItemHolder


class CategoryGridHelperAdapter(
    private val fragment: CategoryFragment,
    dataList: List<Movie>,
    itemLayoutId: Int,
    footerLayoutId: Int?
) : BaseVLayoutAdapter<Movie>(fragment.requireContext(), dataList, itemLayoutId, null, footerLayoutId) {

    private val isLoadFailed: Boolean
        get() = fragment.isLoadFailed

    private val isNoMoreData: Boolean
        get() = fragment.isNoMoreData

    override fun onCreateViewHolder(itemView: View, viewType: Int) = when (viewType) {
        VIEW_TYPE_ITEM -> HomeSectionItemHolder(itemView)
        VIEW_TYPE_FOOTER -> LoadingMoreViewHolder(itemView)
        else -> throw IllegalArgumentException()
    }

    override fun onBindFooterHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as LoadingMoreViewHolder
        when {
            isNoMoreData -> {
                holder.progress.visibility = View.GONE
                holder.failed.visibility = View.GONE
                holder.end.visibility = View.VISIBLE
            }
            isLoadFailed -> {
                holder.progress.visibility = View.GONE
                holder.failed.visibility = View.VISIBLE
                holder.end.visibility = View.GONE
            }
            else -> {
                holder.progress.visibility = View.VISIBLE
                holder.failed.visibility = View.GONE
                holder.end.visibility = View.GONE
            }
        }
        holder.failed.setOnClickListener {
            fragment.loadData()
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun getLayoutHelper(): LayoutHelper {
        return LayoutHelperFactory.createGridLayoutHelper()
    }

}