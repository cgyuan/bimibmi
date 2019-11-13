package com.cyuan.bimibimi.ui.home.holder

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.BaseVLayoutAdapter
import com.cyuan.bimibimi.ui.home.MovieDetailActivity

class HomeSectionItemHolder(itemView: View) : BaseVLayoutAdapter.ViewHolder<Movie>(itemView) {
    override fun onBind(data: Movie) {
        Glide.with(cover).load(data.cover).placeholder(R.drawable.ic_default_grey).into(cover)
        title.text = data.title
        label.text = data.label
        itemView.setOnClickListener {
            val intent = Intent(App.getContext(), MovieDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("movie", data)
            App.getContext().startActivity(intent)
        }
    }

    val cover: AppCompatImageView = itemView.findViewById(R.id.cover)
    val title: TextView = itemView.findViewById(R.id.title)
    val label: TextView = itemView.findViewById(R.id.label)
}