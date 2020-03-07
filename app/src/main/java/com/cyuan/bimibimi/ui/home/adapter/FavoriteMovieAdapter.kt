package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.paging.AsyncPagedListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.Reflector
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.BasePagedListAdapter
import com.cyuan.bimibimi.ui.base.MyAdapterListUpdateCallback
import com.cyuan.bimibimi.ui.detail.MovieDetailActivity

class FavoriteMovieAdapter(
    context: Context,
    private val longClickCallback: View.OnLongClickListener? = null
): BasePagedListAdapter<FavoriteMovie, FavoriteMovieAdapter.FavoriteItemHolder>(
    context,
    R.layout.movie_card_item_layout,
    FavoriteMovieCallback(),
    null, R.layout.empty_bottom_space
) {

    init {
        if (headerLayoutId != null) {
            // 通过反射修改本来的更新数据方法（主要是索引号向前移动一位），使其支持单个header
            val pagedListDiffer = Reflector.with(this).field("mDiffer").get<AsyncPagedListDiffer<*>>()
            Reflector.with(pagedListDiffer).field("mUpdateCallback").set(MyAdapterListUpdateCallback(this))
        }
    }

    override fun onCreateViewHolder(itemView: View, viewType: Int) = when (viewType) {
        VIEW_TYPE_ITEM -> FavoriteItemHolder(itemView)
        VIEW_TYPE_FOOTER -> FooterHolder(itemView)
        else -> throw IllegalArgumentException()
    }

    override fun onBindItemHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as FavoriteItemHolder
        val movie = getItem(position)!!
        Glide.with(holder.cover).load(movie.cover).placeholder(R.drawable.ic_default_grey).into(holder.cover)
        holder.title.text = movie.title
        holder.label.text = movie.label
        holder.itemView.setOnClickListener {
            val intent = Intent(App.getContext(), MovieDetailActivity::class.java)
            val mv = Movie(movie.href, movie.title, movie.cover, "", movie.label)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(PlayerKeys.MOVIE, mv)
            App.getContext().startActivity(intent)
        }
        holder.itemView.tag = movie
        holder.itemView.setOnLongClickListener(longClickCallback)
    }


    class FavoriteItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
        val title: TextView = itemView.findViewById(R.id.title)
        val label: TextView = itemView.findViewById(R.id.label)
    }

    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}