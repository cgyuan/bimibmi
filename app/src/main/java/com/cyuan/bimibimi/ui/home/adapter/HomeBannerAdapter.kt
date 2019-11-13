package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.home.MovieDetailActivity
import com.cyuan.bimibimi.ui.home.holder.HomeBannerViewHolder
import com.cyuan.bimibimi.core.utils.GlideImageLoader

class HomeBannerAdapter(
    private val context: Context,
    private val bannerList: List<String>,
    private val movies: MutableList<Movie>
) : DelegateAdapter.Adapter<HomeBannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_banner_layout, parent, false)
        return HomeBannerViewHolder(view)
    }

    override fun getItemCount() = 1

    override fun onCreateLayoutHelper(): LayoutHelper {
        val singHelper = SingleLayoutHelper()
        singHelper.bgColor = R.color.colorPrimary
        singHelper.setMargin(0, 0, 0, 0)
        return singHelper
    }

    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {
        holder.banner.setImageLoader(GlideImageLoader())
            .setImages(bannerList)
            .setOnBannerListener { position ->
                val intent = Intent(context, MovieDetailActivity::class.java)
                intent.putExtra("movie", movies[position])
                context.startActivity(intent)
            }
        holder.banner.start()
    }

}