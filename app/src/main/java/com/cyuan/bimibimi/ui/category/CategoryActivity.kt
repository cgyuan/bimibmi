package com.cyuan.bimibimi.ui.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.databinding.ActivityCategoryBinding
import com.cyuan.bimibimi.ui.base.BaseActivity
import skin.support.widget.SkinCompatSupportable

class CategoryActivity : BaseActivity<ActivityCategoryBinding>(), SkinCompatSupportable {

    private val pagesPath = Constants.CATEGORY_MAP[GlobalUtil.host]!![Constants.PATH_KEY]!!

    private val pagesTitle = Constants.CATEGORY_MAP[GlobalUtil.host]!![Constants.TITLE_KEY]!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra(PlayerKeys.MOVIE_CATEGORY) ?: pagesPath[0]
        val currentPage = pagesPath.indexOf(category)

        binding.mToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.mTabLayout.setupWithViewPager(binding.viewPager)

        binding.viewPager.offscreenPageLimit = pagesPath.size

        binding.viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return CategoryFragment(pagesPath[position])
            }

            override fun getCount(): Int {
                return pagesPath.size
            }

            override fun getPageTitle(position: Int) = pagesTitle[position]

        }
        binding.viewPager.currentItem = currentPage
        applySkin()
    }

    override fun applySkin() {
        if (isBindingInitialized) {
            SupportSkinHelper.tintStatusBar(this)
            SupportSkinHelper.tintViewBackground(this, binding.mTabLayout)
        }
    }

}