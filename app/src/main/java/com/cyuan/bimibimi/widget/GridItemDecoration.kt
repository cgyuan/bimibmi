package com.cyuan.bimibimi.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cyuan.bimibimi.core.extension.dp2px

class GridItemDecoration
/**
 * @param dividerWidth 间隔宽度
 * @param firstRowTopMargin 第一行顶部是否需要间隔
 * @param isNeedSpace 第一列和最后一列是否需要间隔
 * @param isLastRowNeedSpace 最后一行是否需要间隔
 */
@JvmOverloads constructor(
    private val mContext: Context,
    private val mDividerWidth: Int,          //您所需指定的间隔宽度，主要为第一列和最后一列与父控件的间隔；行间距，列间距将动态分配
    firstRowTopMargin: Int,
    isNeedSpace: Boolean,
    isLastRowNeedSpace: Boolean = false, @ColorInt color: Int = Color.WHITE
) : RecyclerView.ItemDecoration() {
    private var mPaint: Paint?
    private var mFirstRowTopMargin = 0 //第一行顶部是否需要间隔
    private var isNeedSpace = false//第一列和最后一列是否需要指定间隔(默认不指定)
    private var isLastRowNeedSpace = false//最后一行是否需要间隔(默认不需要)
    private var spanCount = 0


    /**
     * 根据屏幕宽度和item数量分配 item View的width和height
     * @return
     */
    private val attachCloumnWidth: Int
        get() {
            var itemWidth = 0
            var spaceWidth = 0
            try {
                val width =
                    if (mContext.resources.displayMetrics.widthPixels > mContext.resources.displayMetrics.heightPixels)
                        mContext.resources.displayMetrics.heightPixels
                    else
                        mContext.resources.displayMetrics.widthPixels
                if (isNeedSpace)
                    spaceWidth = 2 * mDividerWidth
                itemWidth = (width - spaceWidth) / spanCount - dp2px(7.5F)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return itemWidth
        }

    /**
     *
     * @param dividerWidth 间隔宽度
     * @param isNeedSpace 第一列和最后一列是否需要间隔
     */
    constructor(context: Context, dividerWidth: Int, isNeedSpace: Boolean) : this(
        context,
        dividerWidth,
        0,
        isNeedSpace,
        false
    )

    init {
        this.isNeedSpace = isNeedSpace
        this.isLastRowNeedSpace = isLastRowNeedSpace
        this.mFirstRowTopMargin = firstRowTopMargin

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = color
        mPaint?.style = Paint.Style.FILL
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val lm = parent.layoutManager ?: return
        val position = lm.getPosition(view)
        spanCount = getSpanCount(parent)
        if (position / spanCount == 0) {
            outRect.top = mFirstRowTopMargin
        }
        outRect.bottom = mDividerWidth
    }

    /**
     * 获取Item View的大小，若无则自动分配空间
     * 并根据 屏幕宽度-View的宽度*spanCount 得到屏幕剩余空间
     * @param view
     * @return
     */
    private fun getMaxDividerWidth(view: View): Int {
        val itemWidth = view.layoutParams.width
        val itemHeight = view.layoutParams.height

        val screenWidth =
            if (mContext.resources.displayMetrics.widthPixels > mContext.resources.displayMetrics.heightPixels)
                mContext.resources.displayMetrics.heightPixels
            else
                mContext.resources.displayMetrics.widthPixels

        var maxDividerWidth = screenWidth - itemWidth * spanCount
        if (itemHeight < 0 || itemWidth < 0 || isNeedSpace && maxDividerWidth <= (spanCount - 1) * mDividerWidth) {
            view.layoutParams.width = attachCloumnWidth
            view.layoutParams.height = attachCloumnWidth

            maxDividerWidth = screenWidth - view.layoutParams.width * spanCount
        }
        return maxDividerWidth
    }

    /**
     * 获取列数
     * @param parent
     * @return
     */
    private fun getSpanCount(parent: RecyclerView): Int {
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

}