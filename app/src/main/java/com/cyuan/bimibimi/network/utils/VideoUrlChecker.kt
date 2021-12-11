package com.cyuan.bimibimi.network.utils

import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.request.Request


class HeaderRequest(val url: String): Request() {


    override fun url() = url

    override fun method() = HEADER

    override fun listen(callback: Callback?) {
    }

}

object VideoUrlChecker {


    fun isVideoUrl(resUrl: String): Boolean {
        return try {
            val response = HeaderRequest(resUrl).syncRequest()
            val contentType = response.header("Content-Type", "") ?: ""
            contentType.contains("video")
        } catch (e: Exception) {
            false
        }
    }
}