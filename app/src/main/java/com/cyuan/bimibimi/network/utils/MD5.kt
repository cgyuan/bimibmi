package com.cyuan.bimibimi.network.utils

import java.io.FileInputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * MD5加密辅助工具类
 */
object MD5 {

    private val DIGITS_UPPER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    /**
     * 对传入的字符串进行MD5加密。
     * @param origin
     * 原始字符串。
     * @return 经过MD5加密后的字符串。
     */
    fun encrypt(origin: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(origin.toByteArray(Charset.defaultCharset()))
            return String(toHex(digest.digest()))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 获取文件的MD5值。
     * @param path
     * 文件的路径
     * @return 文件的MD5值。
     */
    fun getFileMD5(path: String): String {
        try {
            val fis = FileInputStream(path)
            val md = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(1024)
            var length: Int
            while (fis.read(buffer).also { length = it } != -1) {
                md.update(buffer, 0, length)
            }
            val bigInt = BigInteger(1, md.digest())
            return bigInt.toString(16).toUpperCase()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    private fun toHex(data: ByteArray): CharArray {
        val toDigits = DIGITS_UPPER
        val l = data.size
        val out = CharArray(l shl 1)
        // two characters form the hex value.
        var i = 0
        var j = 0
        while (i < l) {
            out[j++] = toDigits[(0xF0 and data[i].toInt()).ushr(4)]
            out[j++] = toDigits[0x0F and data[i].toInt()]
            i++
        }
        return out
    }
}
