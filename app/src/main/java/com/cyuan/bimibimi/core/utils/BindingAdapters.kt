package com.cyuan.bimibimi.core.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.ui.detail.adapter.OnlinePlayListAdapter
import com.cyuan.bimibimi.widget.EmptyView
import com.google.android.material.bottomsheet.BottomSheetDialog

//<attr name="viewStatus" format="enum">
//  <enum name="loading" value="0"/>
//  <enum name="empty" value="1" />
//  <enum name="error" value="2"/>
//  <enum name="done" value="3"/>
//</attr>
@BindingAdapter("viewStatus")
fun EmptyView.viewStatus(state: Int) {
    when (state) {
        Constants.ViewState.LOADING->triggerLoading()
        Constants.ViewState.EMPTY->triggerEmpty()
        Constants.ViewState.ERROR->triggerNetError()
        Constants.ViewState.DONE->triggerOk()
    }
}

@BindingAdapter("imageUrl", "placeDrawable", "errorDrawable", "transform", requireAll = false) //requireAll表示所有参数都必须有
fun imageUrl(view:ImageView, imageUrl: String?, placeDrawable: Drawable?, errorDrawable: Drawable?, transformation: Transformation<Bitmap>?) = imageUrl.let {
    val builder = Glide.with(view).load(imageUrl)
    placeDrawable?.let {
        builder.placeholder(placeDrawable)
    }
    errorDrawable?.let {
        builder.error(errorDrawable)
    }
    transformation?.let {
        builder.transform(transformation)
    }
    builder.into(view)
}

@BindingAdapter("movieSource")
fun LinearLayout.movieSource(movieDetail: MovieDetail?) = movieDetail?.let {
    removeAllViews()
    val bottomSheetDialog = BottomSheetDialog(context)
    val contentView = View.inflate(context, R.layout.play_all_list_layout, null)
    bottomSheetDialog.setContentView(contentView)
    val closeBtn = contentView.findViewById<ImageView>(R.id.close)
    val bottomSheetPlayList = contentView.findViewById<RecyclerView>(R.id.playListRv)
    bottomSheetPlayList.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
    closeBtn.setOnClickListener {
        bottomSheetDialog.dismiss()
    }
    val bottomSheetAdapter = OnlinePlayListAdapter(
        context,
        arrayListOf(),
        movieDetail,
        0,
        R.drawable.bottom_sheet_play_bt_shape
    )
    bottomSheetPlayList.adapter = bottomSheetAdapter
    movieDetail.dataSources.forEachIndexed { index, dataSource ->
        val view = LayoutInflater.from(context).inflate(R.layout.online_detail_data_source_hold_layout, this, false)
        val dataSourceLabel = view.findViewById<TextView>(R.id.dataSourceLabel)
        val btnViewALl = view.findViewById<TextView>(R.id.viewAll)
        val episodesRV = view.findViewById<RecyclerView>(R.id.episodesRecyclerView)

        dataSourceLabel.text = context.getString(R.string.data_source_label, dataSource.name)
        episodesRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        episodesRV.adapter = OnlinePlayListAdapter(
            context,
            dataSource.episodes,
            movieDetail,
            dataSourceIndex = index
        )

        btnViewALl.setOnClickListener {
            bottomSheetDialog.show()
            bottomSheetAdapter.dataSourceIndex = index
            bottomSheetAdapter.refreshData(dataSource.episodes)
        }
        addView(view)
    }
}