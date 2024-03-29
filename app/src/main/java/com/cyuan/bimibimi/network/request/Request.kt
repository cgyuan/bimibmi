package com.cyuan.bimibimi.network.request

import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.OriginThreadCallback
import com.cyuan.bimibimi.network.Response
import com.cyuan.bimibimi.network.exception.ResponseCodeException
import com.cyuan.bimibimi.network.utils.HttpsUtils
import com.cyuan.bimibimi.network.utils.Utility
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class Request {
    private lateinit var okHttpClient: OkHttpClient

    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

    private var params: Map<String, String>? = null

    private var deviceName: String

    private var deviceSerial: String

    private var getParamsAlready = false

    private var callback: Callback? = null

    init {
        connectTimeout(10)
        writeTimeout(10)
        readTimeout(10)
        sslSocketFactory()
        notFollowRedirect()
        deviceName = Utility.deviceName
        deviceSerial = Utility.getDeviceSerial()
    }

    /**
     * 设置响应回调接口
     * @param callback
     * 回调的实例
     */
    fun setListener(callback: Callback?) {
        this.callback = callback
    }

    open fun params(): Map<String, String>? {
        return null
    }

    private fun build() {
        okHttpClient = okHttpBuilder.build()
    }

    // 不跟随重定向，这里会导致部分视频解析被重定向到 www.baidu.com
    private fun notFollowRedirect() {
        okHttpBuilder.followRedirects(false)
            .followSslRedirects(false)
    }

    private fun connectTimeout(seconds: Int) {
        okHttpBuilder.connectTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    private fun writeTimeout(seconds: Int) {
        okHttpBuilder.writeTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    private fun readTimeout(seconds: Int) {
        okHttpBuilder.readTimeout(seconds.toLong(), TimeUnit.SECONDS)
    }

    private fun sslSocketFactory() {
        okHttpBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.UnSafeTrustManager)
            .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
    }

    abstract fun url(): String

    abstract fun method(): Int

    abstract fun listen(callback: Callback?)

    /**
     * Android客户端的所有请求都需要添加User-Agent: Android这样一个请求头。每个接口的封装子类可以添加自己的请求头。
     * @param builder
     * 请求头builder
     * @return 添加完请求头后的builder。
     */
    open fun headers(builder: Headers.Builder): Headers.Builder {
        return builder
    }

    private suspend fun <T> Call.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    val body = response.body
                    if (body != null) continuation.resume(body as T)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

            })
        }
    }

    fun syncRequest(): okhttp3.Response {
        val request = buildRequest()
        return okHttpClient.newCall(request).execute()
    }


    /**
     * 组装网络请求后添加到HTTP发送队列，并监听响应回调。
     * @param requestModel
     * 网络请求对应的实体类
     */
    fun <T> inFlight(requestModel: Class<T>) {
        val request = buildRequest()
        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                try {
                    if (response.isSuccessful || response.code == 302) {
                        val body = response.body
                        val result = body?.string() ?: ""
//                        logVerbose(LoggingInterceptor.TAG, result)
                        if (requestModel == String::class.java) {
                            notifyResponse(result)
                        } else {
                            val gson = GsonBuilder().disableHtmlEscaping().create()
                            val responseModel = gson.fromJson(result, requestModel)
                            response.close()
                            responseModel as Response
                            notifyResponse(responseModel)
                        }
                    } else {
                        notifyFailure(ResponseCodeException(response.code))
                    }
                } catch (e: Exception) {
                    notifyFailure(e)
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                notifyFailure(e)
            }

        })
    }

    private fun buildRequest(): Request {
        build()
        val requestBuilder = Request.Builder()
        if (method() == GET && getParams() != null) {
            requestBuilder.url(urlWithParam())
        } else {
            requestBuilder.url(url())
        }
        requestBuilder.headers(headers(Headers.Builder()).build())
        when {
            method() == POST -> requestBuilder.post(formBody())
            method() == PUT -> requestBuilder.put(formBody())
            method() == DELETE -> requestBuilder.delete(formBody())
            method() == HEADER -> requestBuilder.head()
        }
        val request = requestBuilder.build()
        return request
    }

    private fun notifyResponse(response: String) {
        callback?.let {
            if (it is OriginThreadCallback) {
                it.onResponseString(response)
                callback = null
            } else {
                App.getHandler().post {
                    it.onResponseString(response)
                    callback = null
                }
            }
        }
    }

    /**
     * 当请求响应成功的时候，将服务器响应转换后的实体类进行回调。
     * @param response
     * 服务器响应转换后的实体类
     */
    private fun notifyResponse(response: Response) {
        callback?.let {
            if (it is OriginThreadCallback) {
                it.onResponse(response)
                callback = null
            } else {
                App.getHandler().post {
                    it.onResponse(response)
                    callback = null
                }
            }
        }
    }

    /**
     * 当请求响应失败的时候，将具体的异常进行回调。
     * @param e
     * 请求响应的异常
     */
    private fun notifyFailure(e: Exception) {
        callback?.let {
            if (it is OriginThreadCallback) {
                it.onFailure(e)
                callback = null
            } else {
                App.getHandler().post {
                    it.onFailure(e)
                    callback = null
                }
            }
        }
    }

    /**
     * 构建POST、PUT、DELETE请求的参数体。
     *
     * @return 组装参数后的FormBody。
     */
    open fun formBody(): RequestBody {
        val builder = FormBody.Builder()
        val params = getParams()
        if (params != null) {
            val keys = params.keys
            if (keys.isNotEmpty()) {
                for (key in keys) {
                    val value = params[key]
                    if (value != null) {
                        builder.add(key, value)
                    }
                }
            }
        }
        return builder.build()
    }

    /**
     * 当GET请求携带参数的时候，将参数以key=value的形式拼装到GET请求URL的后面，并且中间以?符号隔开。
     * @return 携带参数的URL请求地址。
     */
    private fun urlWithParam(): String {
        val params = getParams()
        if (params != null) {
            val keys = params.keys
            if (!keys.isEmpty()) {
                val paramsBuilder = StringBuilder()
                var needAnd = false
                for (key in keys) {
                    if (needAnd) {
                        paramsBuilder.append("&")
                    }
                    paramsBuilder.append(key).append("=").append(params[key])
                    needAnd = true
                }
                return url() + "?" + paramsBuilder.toString()
            }
        }
        return url()
    }

    /**
     * 获取本次请求所携带的所有参数。
     *
     * @return 本次请求所携带的所有参数，以Map形式返回。
     */
    private fun getParams(): Map<String, String>? {
        if (!getParamsAlready) {
            params = params()
            getParamsAlready = true
        }
        return params
    }

    companion object {

        const val GET = 0

        const val POST = 1

        const val PUT = 2

        const val DELETE = 3

        const val HEADER = 4
    }
}