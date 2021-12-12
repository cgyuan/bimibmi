package com.cyuan.bimibimi.core.utils

import android.app.Activity
import android.content.res.Configuration
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import q.rorbin.verticaltablayout.VerticalTabLayout
import skin.support.SkinCompatManager

object SupportSkinHelper {

    fun tintStatusBar(activity: Activity) {
        val color = getColor(activity, "colorPrimary")
        if (color != -1) {
            SystemBarHelper.tintStatusBar(activity, color, 0F)
        }
    }

    fun tintVerticalTabLayout(activity: Activity, tabLayout: VerticalTabLayout) {
        val colorPrimary = getColor(activity, "colorPrimary")
        val indicatorColor = getColor(activity, "colorTabIndicate")
        if (colorPrimary != -1 && indicatorColor != -1) {
            tabLayout.setBackgroundColor(colorPrimary)
            tabLayout.setIndicatorColor(indicatorColor)
        }
    }

    fun tintViewBackground(activity: Activity, view: View?) {
        val color = getColor(activity, "colorPrimary")
        if (color != -1) {
            view?.setBackgroundColor(color)
        }
    }

    private fun getColor(activity: Activity, colorName: String): Int {
        val skinName: String = SharedUtil.read(Constants.SKIN_NAME, Constants.DEFAULT_SKIN)
        val name = if (skinName == Constants.DEFAULT_SKIN) {
            colorName
        } else {
            "${colorName}_${skinName}"
        }
        val id = activity.resources.getIdentifier(name, "color", activity.packageName)
        if (id != 0) {
            return activity.resources.getColor(id)
        }
        return -1
    }

    fun setSkin(skinName: String) {
        when (skinName) {
            "night" -> {
                AppCompatDelegate.getDefaultNightMode()
                SkinCompatManager.getInstance().restoreDefaultTheme()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                SharedUtil.save(Constants.IS_NIGHT_MODE_KEY, true)
                val saveSkinName = SharedUtil.read(Constants.SKIN_NAME, "cyan")
                SharedUtil.save(Constants.SKIN_NAME_BEFORE_NIGHT_MODE, saveSkinName)
            }
            else -> {
                val mode = App.getContext().resources.configuration.uiMode and  Configuration.UI_MODE_NIGHT_MASK
                val isNight: Boolean = SharedUtil.read(Constants.IS_NIGHT_MODE_KEY, false) or (mode  == Configuration.UI_MODE_NIGHT_YES)
                if (isNight) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                SharedUtil.save(Constants.IS_NIGHT_MODE_KEY, false)
                SkinCompatManager.getInstance()
                    .loadSkin(skinName, null, SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN)
            }
        }
        SharedUtil.save(Constants.SKIN_NAME, skinName)
    }
}