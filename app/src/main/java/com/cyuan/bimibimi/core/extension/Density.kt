package com.cyuan.bimibimi.core.extension

import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.DensityUtils

/**
 * 单位转换工具类，会根据手机的分辨率来进行单位转换。
 *
 */

/**
 * 根据手机的分辨率将dp转成为px
 */
fun dp2px(dp: Float): Int {
    val scale = if (DensityUtils.targetDensity != 0F) {
        DensityUtils.targetDensity
    } else {
        App.getContext().resources.displayMetrics.density
    }
    return (dp * scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率将px转成dp
 */
fun px2dp(px: Float): Int {
    val scale = if (DensityUtils.targetDensity != 0F) {
        DensityUtils.targetDensity
    } else {
        App.getContext().resources.displayMetrics.density
    }
    return (px / scale + 0.5f).toInt()
}
