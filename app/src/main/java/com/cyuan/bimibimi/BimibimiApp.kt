package com.cyuan.bimibimi

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import androidx.multidex.MultiDexApplication
import androidx.work.*
import androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.utils.GlobalUtil.processName
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.service.JettyResourceService
import com.tencent.bugly.Bugly
import me.ele.uetool.UETool
import skin.support.SkinCompatManager
import skin.support.app.SkinAppCompatViewInflater
import skin.support.app.SkinCardViewInflater
import skin.support.constraint.app.SkinConstraintViewInflater
import skin.support.design.app.SkinMaterialViewInflater
import zmovie.com.dlan.DlnaLib
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


class BimibimiApp : MultiDexApplication() {

    var currentActivityWeakRef: WeakReference<FragmentActivity>? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("BimibimiApp", "onCreate")
//        UETool.putFilterClass(FilterOutView::class.java!!)
//        UETool.putAttrsProviderClass(CustomAttribution::class.java!!)
        App.initialize(this)
        if (packageName != processName) {
            return
        }
        DlnaLib.initDlna(this)

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

        val skinName = SharedUtil.read(Constants.SKIN_NAME_BEFORE_NIGHT_MODE, "cyan")
        SupportSkinHelper.setSkin(skinName)

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

        Bugly.init(applicationContext, "852f771d92", BuildConfig.DEBUG)
    }

    fun getCurrentActivity() :FragmentActivity? {
        return currentActivityWeakRef?.get()
    }

}