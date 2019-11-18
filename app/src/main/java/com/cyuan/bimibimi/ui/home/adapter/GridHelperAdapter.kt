package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.model.Section
import com.cyuan.bimibimi.ui.category.CategoryActivity
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity
import com.cyuan.bimibimi.ui.home.holder.HomeSectionHeaderHolder
import com.cyuan.bimibimi.ui.home.holder.HomeSectionItemHolder

class GridHelperAdapter(private val context: Context, private val section: Section): DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {


    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.hom_section_header_layout, parent, false)
            HomeSectionHeaderHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.movie_card_item_layout, parent, false)
            HomeSectionItemHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HomeSectionHeaderHolder) {
            holder.name.text = section.title
            if (TextUtils.isEmpty(section.moreLink)) {
                holder.more.visibility = View.GONE
            }
            holder.more.setOnClickListener {
                val intent = Intent(context, CategoryActivity::class.java)
                context.startActivity(intent)
            }
        } else {
            val movie = section.list[position - 1]
            holder as HomeSectionItemHolder
            Glide.with(holder.cover).load(movie.cover).placeholder(R.drawable.ic_default_grey).into(holder.cover)
            holder.title.text = movie.title
            holder.label.text = movie.label
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movie", movie)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return section.list.size + 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        val gridHelper = GridLayoutHelper(5)
        gridHelper.spanCount = 2
        gridHelper.marginTop = 30
//        gridHelper.setWeights(new float[]{20.0f,20.0f,20.0f,20.0f,20.0f});
        //设置垂直方向条目的间隔
        gridHelper.vGap = dp2px(10F)
        //设置水平方向条目的间隔
        gridHelper.hGap = dp2px(10F)
        gridHelper.marginLeft = 30
        gridHelper.marginRight = 30
        gridHelper.marginBottom = 5
        //自动填充满布局
        gridHelper.setAutoExpand(true)
        gridHelper.setSpanSizeLookup(object : GridLayoutHelper.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val pos = position - startPosition
                return if (getItemViewType(pos) == VIEW_TYPE_HEADER) gridHelper.spanCount else 1
            }
        })
        return gridHelper
    }
}