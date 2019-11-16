package com.cyuan.bimibimi.ui.search

import android.view.View
import com.alibaba.android.vlayout.LayoutHelper
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.BaseVLayoutAdapter
import com.cyuan.bimibimi.ui.base.LayoutHelperFactory
import com.cyuan.bimibimi.ui.home.holder.HomeSectionItemHolder


class SearchGridHelperAdapter(
    activity: SearchActivity,
    dataList: List<Movie>,
    itemLayoutId: Int
) : BaseVLayoutAdapter<Movie>(activity, dataList, itemLayoutId, null, null) {

    override fun onCreateViewHolder(itemView: View, viewType: Int) = HomeSectionItemHolder(itemView)


    override fun getLayoutHelper(): LayoutHelper {
        return LayoutHelperFactory.createGridLayoutHelper()
    }

}