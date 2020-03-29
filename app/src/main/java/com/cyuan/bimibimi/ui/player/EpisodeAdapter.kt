package com.cyuan.bimibimi.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.model.Episode
import per.goweii.anylayer.Layer

class EpisodeAdapter(
    private val episodeList: MutableList<Episode>,
    private val choseEpisodeLayer: Layer,
    private val clickedListener: CustomVideoController.OnItemClickedListener
) : RecyclerView.Adapter<EpisodeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode_list_item, parent, false)
        return EpisodeHolder(view)
    }

    override fun getItemCount() = episodeList.size

    override fun onBindViewHolder(holder: EpisodeHolder, position: Int) {
        holder.btn.isSelected = CustomVideoController.sCurrentIndex == position
        holder.btn.text = episodeList[position].title
        holder.btn.setOnClickListener {
            choseEpisodeLayer.dismiss()
            if (CustomVideoController.sCurrentIndex == position) {
                return@setOnClickListener
            }
            clickedListener.clicked(position)
            CustomVideoController.sCurrentIndex = position
            notifyDataSetChanged()
        }
    }

}