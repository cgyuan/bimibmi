package com.cyuan.bimibimi.network

/**
 * 网络请求响应的回调接口。
 *
 */
interface Callback {

    fun onResponseString(response: String) {}

    fun onResponse(response: Response) {}

    fun onFailure(e: Exception)

}
