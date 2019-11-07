package com.cyuan.bimibimi.network

/**
 * 网络请求响应的回调接口，回调时保留原来线程进行回调，不切换到主线程。
 *
 */
interface OriginThreadCallback : Callback
