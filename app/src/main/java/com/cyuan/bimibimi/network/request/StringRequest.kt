package com.cyuan.bimibimi.network.request

import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.network.Callback
import okhttp3.Headers

open class StringRequest : Request() {

    var url: String = Constants.BIMIBIMI_INDEX

    override fun url() = url

    override fun method() = GET

    override fun headers(builder: Headers.Builder): Headers.Builder {
        builder.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.70 Safari/537.36")
        return super.headers(builder)
    }

    fun url(url: String): StringRequest {
        this.url = url
        return this
    }

    override fun listen(callback: Callback?) {
        setListener(callback)
        inFlight(String::class.java)
    }
}