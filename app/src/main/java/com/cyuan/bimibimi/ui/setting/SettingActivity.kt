package com.cyuan.bimibimi.ui.setting

import android.os.Bundle
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.databinding.ActivitySearchBinding
import com.cyuan.bimibimi.databinding.ActivitySettingBinding
import com.cyuan.bimibimi.ui.base.BaseActivity
import skin.support.widget.SkinCompatSupportable

class SettingActivity : BaseActivity<ActivitySettingBinding>(), SkinCompatSupportable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbarLayout.mToolbar.title = "设置与帮助"
        binding.toolbarLayout.mToolbar.run {
            setSupportActionBar(this)
            title = "设置与帮助"
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { finish() }
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.setting_container, SettingFragment())
            .commit()
    }

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)
    }
}