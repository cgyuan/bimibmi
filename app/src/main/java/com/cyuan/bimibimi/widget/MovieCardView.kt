package com.cyuan.bimibimi.widget

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.cyuan.bimibimi.core.extension.dp2px

class MovieCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec) - dp2px(8F),
            MeasureSpec.EXACTLY
        )
        val heightSp = MeasureSpec.makeMeasureSpec(
            (width * 1.586f).toInt(),
            MeasureSpec.EXACTLY
        )
        super.onMeasure(widthSpec, heightSp)
    }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}