package com.cyuan.bimibimi.ui.base

import android.app.Activity
import androidx.fragment.app.Fragment
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.widget.EmptyView

interface UICallback {
    fun reload()
}

fun Activity.bindEmptyViewCallback(callback: UICallback?, emptyMsgId: Int = R.string.prompt_empty) {
    val emptyView: EmptyView? = findViewById(R.id.emptyView)
    initEmptyView(emptyView, callback, emptyMsgId)
}

fun Fragment.bindEmptyViewCallback(callback: UICallback?, emptyMsgId: Int = R.string.prompt_empty) {
    val emptyView: EmptyView? = view?.findViewById(R.id.emptyView)
    initEmptyView(emptyView, callback, emptyMsgId)
}

fun initEmptyView(emptyView: EmptyView?, callback: UICallback?, emptyMsgId: Int) {
    emptyView?.setEmptyMessageId(emptyMsgId)
    emptyView?.setOnClickListener {
        if (emptyView.state != Constants.ViewState.LOADING ||
            emptyView.state != Constants.ViewState.EMPTY ||
            emptyView.state != Constants.ViewState.DONE) {
            callback?.reload()
        }
    }
}