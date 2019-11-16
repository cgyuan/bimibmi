package com.cyuan.bimibimi.core.extension

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * 隐藏软键盘。
 */
fun Activity.hideSoftKeyboard() {
    try {
        val view = currentFocus
        if (view != null) {
            val binder = view.windowToken
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    } catch (e: Exception) {
        logWarn(e.message, e)
    }
}