package com.cyuan.bimibimi.ui.theme

import android.os.Bundle
import android.view.MenuItem
import android.widget.RadioButton
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.databinding.ActivityChooseThemeBinding
import com.cyuan.bimibimi.ui.base.BaseActivity
import skin.support.widget.SkinCompatSupportable

class ChooseThemeActivity: BaseActivity<ActivityChooseThemeBinding>(), SkinCompatSupportable {

    private val mSkinNameIdMap = mapOf(
        Pair("cyan", R.id.mCyan),
        Pair("pink", R.id.mPink),
        Pair("night", R.id.mNight),
        Pair("red", R.id.mRed),
        Pair("yellow", R.id.mYellow),
        Pair("green", R.id.mGreen),
        Pair("blue", R.id.mBlue),
        Pair("purple", R.id.mPurple)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val skin = SharedUtil.read(Constants.SKIN_NAME, "cyan")
        binding.mThemeGroup.check(mSkinNameIdMap[skin] ?: R.id.mCyan)
        binding.mThemeGroup.setOnCheckedChangeListener { group, checkedId ->
            val skinName = binding.mThemeGroup.findViewById<RadioButton>(checkedId).tag as String
            SupportSkinHelper.setSkin(skinName)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)
    }
}