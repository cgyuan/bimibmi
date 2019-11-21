package com.cyuan.bimibimi

import org.junit.Test

import org.junit.Assert.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun urlEncode() {
        val url = "https%3A%2F%2Fv.qq.com%2Fx%2Fcover%2Frm3tmmat4li8uul%2Fs0032qugw7q.html"
        val decode = URLDecoder.decode(url, "utf-8")
        println(decode)
        val encode = URLEncoder.encode(decode, "utf-8")
        println(encode)
    }

    @Test
    fun regex() {
        val input = "https://www.iqiyi.com/v_19rrok4pg4.html"
        var pattern = "v_\\w*"
        val regex = Pattern.compile(pattern)
        val matcher = regex.matcher(input)

        if (matcher.find()) {
            var result = matcher.group(matcher.groupCount()) // get last group
            result = result.replace("v_", "")
            println(result)
        }
    }

    @Test
    fun vQQInfo() {

        // \w{6,}[^.html|/]
        val url = "https://v.qq.com/x/cover/enj7gj9pcksq89p/g0761hr9ih3.html"

        val arr = url.split("/")
        val vid = arr[arr.size - 1].replace(".html", "")
        println(vid)
        val cid = arr[arr.size - 2]
        println(cid)
    }
}
