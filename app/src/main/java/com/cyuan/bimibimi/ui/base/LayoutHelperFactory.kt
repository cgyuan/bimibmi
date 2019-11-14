package com.cyuan.bimibimi.ui.base

import android.content.res.Resources
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.GridLayoutHelper
import com.cyuan.bimibimi.core.extension.dp2px

object LayoutHelperFactory {
    fun createGridLayoutHelper(): LayoutHelper {

        val spanCount = (Resources.getSystem().displayMetrics.widthPixels - dp2px(30F)) / dp2px(165F)
        val gridHelper = GridLayoutHelper(spanCount)
        gridHelper.marginTop = 30
//        gridHelper.setWeights(new float[]{20.0f,20.0f,20.0f,20.0f,20.0f});
        //设置垂直方向条目的间隔
        gridHelper.vGap = dp2px(10F)
        //设置水平方向条目的间隔
        gridHelper.hGap = dp2px(10F)
        gridHelper.marginLeft = dp2px(15F)
        gridHelper.marginRight = dp2px(15F)
        gridHelper.marginBottom = 5
        //自动填充满布局
        gridHelper.setAutoExpand(false)
        return gridHelper
    }
}