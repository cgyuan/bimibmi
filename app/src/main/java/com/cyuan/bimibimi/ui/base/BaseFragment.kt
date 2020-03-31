package com.cyuan.bimibimi.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.cyuan.bimibimi.core.utils.GlobalUtil

abstract class BaseFragment: Fragment(), UICallback {

    private lateinit var saveHost: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        saveHost = GlobalUtil.host
    }

    override fun onResume() {
        super.onResume()
        if (saveHost != GlobalUtil.host) {
            saveHost = GlobalUtil.host
            reload()
        }
    }
}