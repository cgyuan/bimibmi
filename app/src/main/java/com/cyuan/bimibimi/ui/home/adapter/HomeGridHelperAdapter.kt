package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.LayoutHelper
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.BaseVLayoutAdapter
import com.cyuan.bimibimi.ui.base.LayoutHelperFactory
import com.cyuan.bimibimi.ui.category.CategoryActivity
import com.cyuan.bimibimi.ui.home.holder.HomeSectionHeaderHolder
import com.cyuan.bimibimi.ui.home.holder.HomeSectionItemHolder


class HomeGridHelperAdapter(
    context: Context,
    dataList: List<Movie>,
    itemLayoutId: Int,
    headerLayoutId: Int,
    private val title: String,
    private val moreLink: String
) : BaseVLayoutAdapter<Movie>(context, dataList, itemLayoutId, headerLayoutId, null) {

    override fun onCreateViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            HomeSectionHeaderHolder(itemView)
        } else {
            HomeSectionItemHolder(itemView)
        }
    }

    override fun onBindHeaderHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as HomeSectionHeaderHolder
        holder.name.text = title
        if (TextUtils.isEmpty(moreLink)) {
            holder.more.visibility = View.GONE
        }
        holder.more.setOnClickListener {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra(PlayerKeys.MOVIE_CATEGORY, moreLink)
            context.startActivity(intent)
        }
    }

    override fun getLayoutHelper(): LayoutHelper {
        return LayoutHelperFactory.createGridLayoutHelper()
    }

}