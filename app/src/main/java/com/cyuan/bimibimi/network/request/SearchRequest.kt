package com.cyuan.bimibimi.network.request

import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.GlobalUtil

class SearchRequest: StringRequest() {

    private val params = mutableMapOf<String, String>()

    override fun url(): String {
        return if (GlobalUtil.host == Constants.BIMIBIMI_INDEX) {
            "${Constants.BIMIBIMI_INDEX}vod/search/"
        } else {
            "${Constants.HALITV_INDEX}search/"
        }
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