package com.cyuan.bimibimi.core.utils

import java.util.regex.Pattern

object UrlHelper {

    fun extractIqyVideoId(url: String): String {
        val pattern = "v_\\w*"
        val regex = Pattern.compile(pattern)
        val matcher = regex.matcher(url)

        if (matcher.find()) {
            var result = matcher.group(matcher.groupCount()) // get last group
            result = result?.replace("v_", "")
            return result
        }
        return ""
    }

    fun rebuildQQVideoUrl(url: String): String {
        // http://dalaowangsan.cn/wangerjiexi/api.php?url=https://m.v.qq.com/cover/0/0gsf9fytppje54d.html?vid=k0027nolupz
        val arr = url.split("/")
        val vid = arr[arr.size - 1].replace(".html", "")
        val cid = arr[arr.size - 2]
        return "http://dalaowangsan.cn/wangerjiexi/api.php?url=https://m.v.qq.com/cover/0/${cid}.html?vid=${vid}"
    }
}