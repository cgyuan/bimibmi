package com.cyuan.bimibimi

import org.junit.Test

import org.junit.Assert.*
import java.net.URLDecoder
import java.net.URLEncoder

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
}
