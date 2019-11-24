package com.cyuan.bimibimi

import android.app.Activity
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.cyuan.bimibimi.core.App
import me.ele.uetool.UETool
import zmovie.com.dlan.DlanLib


class BimibimiApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()


//        UETool.putFilterClass(FilterOutView::class.java!!)
//        UETool.putAttrsProviderClass(CustomAttribution::class.java!!)
        App.initialize(this)
        DlanLib.initDlan(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            private var visibleActivityCount: Int = 0
            private var uetoolDismissY = -1

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
                visibleActivityCount++
                if (visibleActivityCount == 1 && uetoolDismissY >= 0) {
                    UETool.showUETMenu(uetoolDismissY)
                }
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {
                visibleActivityCount--
                if (visibleActivityCount == 0) {
                    uetoolDismissY = UETool.dismissUETMenu()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }
}