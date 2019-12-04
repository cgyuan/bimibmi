package com.cyuan.bimibimi.ui.setting

import android.os.Bundle
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.toolbar_layout.toolbar
import skin.support.widget.SkinCompatSupportable

class SettingActivity : BaseActivity(), SkinCompatSupportable {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        toolbar.run {
            setSupportActionBar(this)
            title = "设置"
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