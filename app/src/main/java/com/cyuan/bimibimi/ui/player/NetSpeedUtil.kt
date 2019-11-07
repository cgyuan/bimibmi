package com.cyuan.bimibimi.ui.player

import android.content.Context
import android.net.TrafficStats

class NetSpeedUtil {

    private var lastTotalRxBytes: Long = 0
    private var lastTimeStamp: Long = 0
    /**
     * 得到网络速度
     * @param context
     * @return
     */
    fun getNetSpeed(context: Context): String {
        var netSpeed = "0 kb/s"
        val nowTotalRxBytes =
            if (TrafficStats.getUidRxBytes(context.applicationInfo.uid) == TrafficStats.UNSUPPORTED.toLong())
                0
            else
                TrafficStats.getTotalRxBytes() / 1024//转为KB;
        val nowTimeStamp = System.currentTimeMillis()
        val speed =
            (nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp)//毫秒转换

        lastTimeStamp = nowTimeStamp
        lastTotalRxBytes = nowTotalRxBytes
        netSpeed = "$speed kb/s"
        return netSpeed
    }
}
