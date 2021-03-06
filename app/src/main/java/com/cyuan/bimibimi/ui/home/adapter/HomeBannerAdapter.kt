package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.utils.GlideImageLoader
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.home.holder.HomeBannerViewHolder

class HomeBannerAdapter(
    private val context: Context,
    private val bannerList: List<String>,
    private val movies: List<Movie>
) : DelegateAdapter.Adapter<HomeBannerViewHolder>() {

    private lateinit var homeBannerViewHolder: HomeBannerViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_banner_layout, parent, false)
        homeBannerViewHolder = HomeBannerViewHolder(view)
        return homeBannerViewHolder
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
                val navController = Navigation.findNavController(holder.banner)
                val args = Bundle()
                args.putParcelable(PlayerKeys.MOVIE, movies[position])
                navController.navigate(R.id.action_homeFragment_to_movieDetailActivity, args)
            }
        holder.banner.start()
    }

    fun stopAutoPlay() {
        if (::homeBannerViewHolder.isInitialized) {
            homeBannerViewHolder.banner.stopAutoPlay()
        }
    }

    fun startAutoPlay() {
        if (::homeBannerViewHolder.isInitialized) {
            homeBannerViewHolder.banner.startAutoPlay()
        }
    }

}