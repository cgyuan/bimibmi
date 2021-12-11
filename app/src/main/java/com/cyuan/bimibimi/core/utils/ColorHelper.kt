package com.cyuan.bimibimi.core.utils

import android.graphics.Color
import android.util.Log
import kotlin.math.floor


object ColorHelper {


    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     * Android中我们一般使用它的16进制，
     * 例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     * red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     * 所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    fun colorBurn(RGBValues: Int): Int {
        val alpha = RGBValues shr 24
        var red = RGBValues shr 16 and 0xFF
        var green = RGBValues shr 8 and 0xFF
        var blue = RGBValues and 0xFF
        red = floor(red * (1 - 0.2)).toInt()
        green = floor(green * (1 - 0.2)).toInt()
        blue = floor(blue * (1 - 0.2)).toInt()
        Log.e("color", red.toString() + "" + green + "" + blue)
        return Color.rgb(red, green, blue)
    }
}
