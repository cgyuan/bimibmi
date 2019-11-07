package com.cyuan.bimibimi.widget

import android.content.Context
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatTextView

/**
 * 实现跑马灯效果的TextView
 */
class MarqueeTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    override fun isFocused(): Boolean {
        return true
    }
}