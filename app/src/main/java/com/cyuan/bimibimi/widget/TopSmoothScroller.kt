package com.cyuan.bimibimi.widget

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

class TopSmoothScroller(context: Context) : LinearSmoothScroller(context) {

    override fun getHorizontalSnapPreference(): Int {
        // Align child view's left or top with parent view's left or top
        return SNAP_TO_START
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }

}