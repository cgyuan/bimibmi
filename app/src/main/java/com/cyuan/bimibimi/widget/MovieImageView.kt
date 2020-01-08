package com.cyuan.bimibimi.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class MovieImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val size = MeasureSpec.getSize(widthMeasureSpec)
        val height = (size * 1.303).toInt()
        setMeasuredDimension(size, height)
    }
}