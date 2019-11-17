package com.cyuan.bimibimi.ui.home.holder


import android.view.View
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.widget.PosterItemView

class BannerHolder(view: View) : RecyclerView.ViewHolder(view) {


    var iv: PosterItemView = view.findViewById(R.id.banner)
    var title: TextView = view.findViewById(R.id.movTitle)

}
