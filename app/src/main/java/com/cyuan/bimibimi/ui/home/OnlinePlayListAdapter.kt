package com.cyuan.bimibimi.ui.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.extension.logWarn
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.StringRequest
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.ui.player.OnlinePlayerActivity
import org.json.JSONObject
import java.net.URLDecoder

class OnlinePlayListAdapter(private val context: Context,
                            private val episodes: List<Episode>):
    RecyclerView.Adapter<OnlinePlayHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlinePlayHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.online_play_btn, parent, false)
        return OnlinePlayHolder(view)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }

    override fun onBindViewHolder(holder: OnlinePlayHolder, position: Int) {
        holder.btPlayText.text = episodes[position].title
        holder.itemView.setOnClickListener {
            Toast.makeText(context, Constants.BIMIBIMI_INDEX + episodes[position].href, Toast.LENGTH_SHORT).show()
            StringRequest().url(episodes[position].href)
                .listen(object: Callback {
                    override fun onFailure(e: Exception) {
                        logWarn("fail to load video url data")
                    }

                    override fun onResponseString(response: String) {
                        HtmlDataParser
                        val start = response.indexOf("player_data=")
                        val end = response.indexOf("}", start + "player_data=".length) + 1
                        val jsonStr = response.substring(start + "player_data=".length, end)
                        val jsonObj = JSONObject(jsonStr)
                        var url = jsonObj.getString("url")
                        url = URLDecoder.decode(url, "utf-8")
                        val intent = Intent(context, OnlinePlayerActivity::class.java)
                        intent.putExtra("url", url)
                        context.startActivity(intent)
                    }
                })
        }
    }
}

//var player_data={"flag":"play","encrypt":1,"trysee":0,"points":0,"link":"\/bangumi\/669\/play\/1\/1\/","link_next":"\/bangumi\/669\/play\/1\/2\/","link_pre":"","url":"%37%33%31%31%62%30%64%35%66%63%39%39%31%37%33%39%2E%6D%70%34","url_next":"%36%65%61%66%66%33%39%64%31%65%32%39%63%64%64%31%2E%6D%70%34","from":"niux","server":"no","note":""}