package com.cyuan.bimibimi.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_category.*
import skin.support.widget.SkinCompatSupportable

class CategoryActivity : BaseActivity(), SkinCompatSupportable {

    private val pagesPath = listOf(
        "/type/riman", "/type/guoman", "/type/fanzu", "/type/juchang", "/type/move"
    )

    private val pagesTitle = listOf(
        "新番放送", "国产动漫", "番组计划", "剧场动画", "影视"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val category = intent.getStringExtra(PlayerKeys.MOVIE_CATEGORY)
        val currentPage = pagesPath.indexOf(category!!)

        mTabLayout.setupWithViewPager(viewPager)

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return CategoryFragment(pagesPath[position])
            }

            override fun getCount(): Int {
                return pagesPath.size
            }

            override fun getPageTitle(position: Int) = pagesTitle[position]

        }
        viewPager.currentItem = currentPage
        applySkin()
    }

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)
        SupportSkinHelper.tintViewBackground(this, mTabLayout)
    }

}