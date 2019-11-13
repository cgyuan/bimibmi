package com.cyuan.bimibimi.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.ui.home.MainActivity
import com.cyuan.bimibimi.widget.DefaultAnimationListener
import kotlinx.android.synthetic.main.splash_layout.*

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        val container = LinearLayout(this)
        container.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        container.orientation = LinearLayout.HORIZONTAL
        val animList = listOf<Animation>(AnimationUtils.loadAnimation(this, R.anim.splash_anim_in_left),
            AnimationUtils.loadAnimation(this, R.anim.splash_anim_in_center),
            AnimationUtils.loadAnimation(this, R.anim.splash_anim_in_right)
        )
        val colHeight = dp2px(170F) * 5
        for (row in 0..2) {
            val col = LinearLayout(this)
            col.orientation = LinearLayout.VERTICAL
            col.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                colHeight
            )
            if (row != 0) {
                val lp = col.layoutParams as LinearLayout.LayoutParams
                lp.leftMargin = dp2px(15f)
            }
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
        container.getChildAt(0).translationY = (-dp2px(170F)).toFloat()
        container.getChildAt(2).translationY = (-dp2px(170F)).toFloat()
        animList[2].setAnimationListener(object : DefaultAnimationListener() {
            override fun onAnimationEnd(animation: Animation?) {
                val animLeft = TranslateAnimation(0f, 0f, 0f, dp2px(170F).toFloat())
                val animRight = TranslateAnimation(0f, 0f, 0f, dp2px(170F).toFloat())
                val animCenter = TranslateAnimation(0f, 0f, 0f, -dp2px(170F).toFloat())
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
                        descContent.visibility = View.VISIBLE
                        posterMasker.visibility = View.VISIBLE
                        posterMasker.startAnimation(alphaAnim)
                        descContent.startAnimation(slideAnim)
                        slideAnim.setAnimationListener(object : DefaultAnimationListener() {
                            override fun onAnimationEnd(animation: Animation?) {
                                showContent.postDelayed({
                                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                    finish()
                                }, 1500)
                            }
                        })
                    }
                })
            }
        })
        showContent.addView(container)

    }


}