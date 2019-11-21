package com.cyuan.bimibimi.network.request

import okhttp3.Headers

class ParseVideoUrlRequest: StringRequest() {

    private lateinit var targetUrl: String
    private val params = mutableMapOf<String, String>()

    override fun url(): String {
        return "https://bb.nmbaojie.com/mingri/api.php"
    }

    open fun targetUrl(targetUrl: String): ParseVideoUrlRequest {
        this.targetUrl = targetUrl
        return this
    }

    fun addParam(key: String, value: String): ParseVideoUrlRequest {
        params[key] = value
        return this
    }

    override fun headers(builder: Headers.Builder): Headers.Builder {
        builder.add(
            "referer",
            "https://v.nmbaojie.com/mingri/inde.php?url=${targetUrl}"
        )
        builder.add("Content-Type", "application/x-www-form-urlencoded")
        return super.headers(builder)
    }

    override fun method(): Int {
        return POST
    }

    override fun params(): Map<String, String>? {
        // ref=0&type=&vid=&lg=4
        params["ref"] = "0"
        params["type"] = ""
        params["vid"] = ""
        params["lg"] = "4"
        params["time"] = "1573396681"
        return params
    }

//    override fun formBody(): RequestBody {
//        return "time=1573396681&key=061c803f0daf9023c822236f2f3ea8fc&key2=a5ccdd1a5b70a2de3ec184c692a4a7ff&key3=cd8ca062cbe010055be0f2a854785bac&key4=34611bacf8a9393ca66b57962b3b7879&url=${URLEncoder.encode(targetUrl, "utf-8")}&ref=0&type=&vid=&lg=4".toRequestBody("application/x-www-form-urlencoded".toMediaType())
//    }

}