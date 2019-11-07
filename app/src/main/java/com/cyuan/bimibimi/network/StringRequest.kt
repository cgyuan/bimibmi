package com.cyuan.bimibimi.network

import com.cyuan.bimibimi.constant.Constants

class StringRequest : Request() {

    var url: String = Constants.BIMIBIMI_INDEX

    override fun url() = url

    override fun method() = GET

    fun url(url: String): StringRequest {
        this.url = url
        return this
    }

    override fun listen(callback: Callback?) {
        setListener(callback)
        inFlight(String::class.java)
    }
}