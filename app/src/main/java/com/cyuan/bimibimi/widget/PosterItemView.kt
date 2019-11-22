package com.cyuan.bimibimi.widget

import android.content.Context
import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PosterItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var reverseBitmap: Bitmap? = null
    private val bitmapPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint: TextPaint
    private var mTitle: String? = null
    private var bitmap: Bitmap? = null
    private val rect: Rect

    init {
        bitmapPaint.flags = FILTER_BITMAP_FLAG
        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 30f

        rect = Rect(0, 0, 360, 500)

    }

    fun setReverseImageBitmap(bitmap: Bitmap) {
        this.reverseBitmap = bitmap
        invalidate()
    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (bitmap != null) {
            canvas.drawBitmap(bitmap!!, null, rect, bitmapPaint)
        }
        if (!TextUtils.isEmpty(mTitle)) {
            canvas.drawText(
                mTitle!!,
                (measuredWidth / 2).toFloat(),
                (measuredHeight + 30).toFloat(),
                textPaint
            )
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top + 50, right, bottom)
    }

    fun setTitleText(downdtitle: String) {
        this.mTitle = downdtitle
        invalidate()
    }

    fun setImageBackGround(resource: Bitmap) {
        this.bitmap = resource
        invalidate()
    }
}
