package com.cyuan.bimibimi.ui.player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.ImageView


class BatteryReceiver(private val pow: ImageView) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val current = extras.getInt("level")// 获得当前电量
        val total = extras.getInt("scale")// 获得总电量
        val percent = current * 100 / total
        pow.drawable.level = percent
    }
}