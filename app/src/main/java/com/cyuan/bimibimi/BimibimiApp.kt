package com.cyuan.bimibimi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.multidex.MultiDexApplication
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.SharedUtil
import me.ele.uetool.UETool
import skin.support.SkinCompatManager
import skin.support.app.SkinAppCompatViewInflater
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater
import zmovie.com.dlan.DlnaLib
import zmovie.com.dlan.JettyResourceService
import java.lang.ref.WeakReference


class BimibimiApp : MultiDexApplication() {

    var currentActivityWeakRef: WeakReference<FragmentActivity>? = null

    override fun onCreate() {
        super.onCreate()
//        UETool.putFilterClass(FilterOutView::class.java!!)
//        UETool.putAttrsProviderClass(CustomAttribution::class.java!!)
        App.initialize(this)
        DlnaLib.initDlna(this)

        val isNight: Boolean = SharedUtil.read(Constants.IS_NIGHT_MODE_KEY, false)
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        startService(Intent(this, JettyResourceService::class.java))

        SkinCompatManager.withoutActivity(this)
            .addInflater(SkinAppCompatViewInflater()) // 基础控件换肤
            .addInflater(SkinMaterialViewInflater()) // material design
            .addInflater(SkinConstraintViewInflater()) // ConstraintLayout
            .addInflater(SkinCardViewInflater()) // CardView v7
            .setSkinStatusBarColorEnable(true)              // 关闭状态栏换肤
//                .setSkinWindowBackgroundEnable(false)           // 关闭windowBackground换肤
                .setSkinAllActivityEnable(true)                // true: 默认所有的Activity都换肤; false: 只有实现SkinCompatSupportable接口的Activity换肤
            .loadSkin()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

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
                currentActivityWeakRef = WeakReference(activity as FragmentActivity)
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

    fun getCurrentActivity() :FragmentActivity? {
        return currentActivityWeakRef?.get()
    }

}