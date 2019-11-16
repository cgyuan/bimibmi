package com.cyuan.bimibimi.network.request

import com.cyuan.bimibimi.constant.Constants

class SearchRequest: StringRequest() {

    private val params = mutableMapOf<String, String>()

    override fun url(): String {
        return "${Constants.BIMIBIMI_INDEX}vod/search/"
    }

    fun addParam(key: String, value: String): SearchRequest {
        params[key] = value
        return this
    }


    override fun method(): Int {
        return POST
    }

    override fun params(): Map<String, String>? {
        return params
    }

}