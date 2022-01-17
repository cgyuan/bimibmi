package com.cyuan.bimibimi.ui.splash

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.core.utils.DensityUtils
import com.cyuan.bimibimi.core.utils.SystemBarHelper
import com.cyuan.bimibimi.databinding.ActivitySplashBinding
import com.cyuan.bimibimi.ui.base.BaseActivity
import com.cyuan.bimibimi.ui.home.MainActivity
import com.cyuan.bimibimi.widget.DefaultAnimationListener

class SplashActivity: BaseActivity<ActivitySplashBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: Exception) {}
        super.onCreate(savedInstanceState)
        DensityUtils.setDensity(application, this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.systemUiVisibility = systemUiVisibility

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            /**
             *  * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT 全屏模式，内容下移，非全屏不受影响
             *  * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES 允许内容去延伸进刘海区
             *  * @see #LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER 不允许内容延伸进刘海区
             */
            val params = window.attributes
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }

        val container = LinearLayout(this)
        container.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        container.orientation = LinearLayout.HORIZONTAL
        val animList = listOf<Animation>(AnimationUtils.loadAnimation(this, R.anim.splash_anim_in_left),
            AnimationUtils.loadAnimation(this, R.anim.splash_anim_in_center),
            AnimationUtils.loadAnimation(this, R.anim.splash_anim_in_right)
        )
        val cardHeight = 185F
        val colHeight = dp2px(cardHeight) * 5
        for (row in 0..2) {
            val col = LinearLayout(this)
            col.orientation = LinearLayout.VERTICAL
            col.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                colHeight
            )
            for (i in 1..5) {
                val frame = View.inflate(this, R.layout.item_splash_section, null)
                val iv = frame.findViewById<ImageView>(R.id.item_img)
                val id = resources.getIdentifier("timg${i + row * 5}", "drawable", packageName)
                iv.setImageResource(id)
                col.addView(frame)
            }
            container.addView(col)
            col.startAnimation(animList[row])
        }
        container.getChildAt(0).translationY = (-dp2px(cardHeight)).toFloat()
        container.getChildAt(2).translationY = (-dp2px(cardHeight)).toFloat()
        animList[2].setAnimationListener(object : DefaultAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                val animLeft = TranslateAnimation(0f, 0f, 0f, dp2px(cardHeight).toFloat())
                val animRight = TranslateAnimation(0f, 0f, 0f, dp2px(cardHeight).toFloat())
                val animCenter = TranslateAnimation(0f, 0f, 0f, -dp2px(cardHeight).toFloat())
                animLeft.duration = 1500
                animLeft.fillAfter = true
                animRight.duration = 1500
                animRight.fillAfter = true
                animCenter.duration = 1500
                animCenter.fillAfter = true
                container.getChildAt(0).startAnimation(animLeft)
                container.getChildAt(1).startAnimation(animCenter)
                container.getChildAt(2).startAnimation(animRight)

                animCenter.setAnimationListener(object : DefaultAnimationListener() {
                    override fun onAnimationEnd(animation: Animation?) {
                        val slideAnim = AnimationUtils.loadAnimation(
                            this@SplashActivity,
                            R.anim.slide_in_bottom
                        )
                        val alphaAnim = AnimationUtils.loadAnimation(
                            this@SplashActivity,
                            R.anim.splash_masker_alpha_in
                        )
                        binding.descContent.visibility = View.VISIBLE
                        binding.posterMasker.visibility = View.VISIBLE
                        binding.posterMasker.startAnimation(alphaAnim)
                        binding.descContent.startAnimation(slideAnim)
                        slideAnim.setAnimationListener(object : DefaultAnimationListener() {
                            override fun onAnimationEnd(animation: Animation?) {
                                binding.showContent.postDelayed({
                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                    finish()
                                }, 1500)
                            }
                        })
                    }
                })
            }
        })
        binding.showContent.addView(container)
    }

}