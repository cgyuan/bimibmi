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

class GridDividerItemDecoration
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
                itemWidth = (width - spaceWidth) / spanCount - 40
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

        var top = 0
        var left = 0
        var right = 0
        var bottom = 0

        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).getViewLayoutPosition()
        spanCount = getSpanCount(parent)
        val childCount = parent.adapter!!.itemCount
        val maxAllDividerWidth = getMaxDividerWidth(view) //

        var spaceWidth = 0//首尾两列与父布局之间的间隔
        if (isNeedSpace)
            spaceWidth = mDividerWidth

        val eachItemWidth = maxAllDividerWidth / spanCount//每个Item left spacing + right spacing

        val dividerItemWidth =
            (maxAllDividerWidth - 2 * spaceWidth) / (spanCount - 1)//item与item之间的距离

        left = itemPosition % spanCount * (dividerItemWidth - eachItemWidth) + spaceWidth

        right = eachItemWidth - left
        bottom = mDividerWidth
        if (mFirstRowTopMargin > 0 && isFirstRow(parent, itemPosition, spanCount, childCount))
        //第一行顶部是否需要间隔
            top = mFirstRowTopMargin
        if (!isLastRowNeedSpace && isLastRow(
                parent,
                itemPosition,
                spanCount,
                childCount
            )
        ) {//最后一行是否需要间隔
            bottom = 0
        }

        outRect.set(left, top, right, bottom)
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

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        draw(c, parent)
    }

    //绘制item分割线
    private fun draw(canvas: Canvas, parent: RecyclerView) {
        val childCount = parent.getChildCount()
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val layoutParams = child.getLayoutParams() as RecyclerView.LayoutParams

            //画水平分隔线
            var left = child.getLeft()
            var right = child.getRight()
            var top = child.getBottom() + layoutParams.bottomMargin
            var bottom = top + mDividerWidth
            if (mPaint != null) {
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    mPaint!!
                )
            }
            //画垂直分割线
            top = child.getTop()
            bottom = child.getBottom() + mDividerWidth
            left = child.getRight() + layoutParams.rightMargin
            right = left + mDividerWidth
            if (mPaint != null) {
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    mPaint!!
                )
            }
        }
    }

    /**
     * 判读是否是第一列
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isFirstColumn(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.getLayoutManager()
        if (layoutManager is GridLayoutManager) {
            if (pos % spanCount == 0) {
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager.orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if (pos % spanCount == 0) {// 第一列
                    return true
                }
            } else {

            }
        }
        return false
    }

    /**
     * 判断是否是最后一列
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isLastColumn(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.getLayoutManager()
        if (layoutManager is GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = (layoutManager as StaggeredGridLayoutManager)
                .getOrientation()
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {// 最后一列
                    return true
                }
            } else {

            }
        }
        return false
    }

    /**
     * 判读是否是最后一行
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isLastRow(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.getLayoutManager()
        if (layoutManager is GridLayoutManager) {
            val lines =
                if (childCount % spanCount == 0) childCount / spanCount else childCount / spanCount + 1
            return lines == pos / spanCount + 1
        } else if (layoutManager is StaggeredGridLayoutManager) {

        }
        return false
    }

    /**
     * 判断是否是第一行
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private fun isFirstRow(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.getLayoutManager()
        if (layoutManager is GridLayoutManager) {
            return pos / spanCount + 1 == 1
        } else if (layoutManager is StaggeredGridLayoutManager) {

        }
        return false
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