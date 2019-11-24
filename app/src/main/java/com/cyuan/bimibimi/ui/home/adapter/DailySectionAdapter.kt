package com.cyuan.bimibimi.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.model.Movie

class DailySectionAdapter(
    private val context: Context,
    private val dayOfWeeks: List<String>,
    private val movieLists: List<List<Movie>>) : RecyclerView.Adapter<DailySectionAdapter.DailySectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailySectionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_update_section_layout, parent, false)
        return DailySectionViewHolder(itemView)
    }

    override fun getItemCount() = dayOfWeeks.size

    override fun onBindViewHolder(holder: DailySectionViewHolder, position: Int) {
        holder.titleView.text = "星期${dayOfWeeks[position]}"
        holder.dailySectionRv.layoutManager = GridLayoutManager(context, 2)
        holder.dailySectionRv.adapter = DailySectionItemAdapter(movieLists[position])
    }


    class DailySectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.dayOfWeek)
        val dailySectionRv: RecyclerView = itemView.findViewById(R.id.dailySectionRv)

    }
}