package com.cyuan.bimibimi.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.PermissionsMgr
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.databinding.ActivityMainBinding
import com.cyuan.bimibimi.ui.base.BaseActivity
import com.cyuan.bimibimi.ui.category.CategoryActivity
import com.cyuan.bimibimi.ui.download.DownloadActivity
import com.cyuan.bimibimi.ui.setting.SettingActivity
import com.cyuan.bimibimi.ui.theme.ChooseThemeActivity
import com.google.android.material.snackbar.Snackbar
import skin.support.widget.SkinCompatSupportable
import zmovie.com.dlan.DlnaLib

class MainActivity : BaseActivity<ActivityMainBinding>(), SkinCompatSupportable{

    private lateinit var switchModeBtn: ImageView
    private lateinit var navController: NavController
    private lateinit var homeItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        DensityUtils.setDensity(application, this)
        setContentView(binding.root)
        DlnaLib.initDlna(application)

        if(!PermissionsMgr.isAllPermissionReady(this)) {
            PermissionsMgr.requestAllPermissionsAppNeed(this, {}, {
                Toast.makeText(this, "为了保证程序正常运行请打开读写外部存储权限", Toast.LENGTH_SHORT).show()
            })
        }

        navController = Navigation.findNavController(this, R.id.fragment)

        homeItem = binding.mNavigationView.menu.findItem(R.id.homeFragment)
        binding.mNavigationView.setNavigationItemSelectedListener {
            closeDrawer()
            when (it.itemId) {
                in listOf(R.id.homeFragment, R.id.favoriteFragment, R.id.historyFragment, R.id.dailyUpdateFragment) -> {
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
                R.id.item_theme -> {
                    startActivity(Intent(this@MainActivity, ChooseThemeActivity::class.java))
                }
                R.id.item_settings -> {
                    startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                }
                R.id.item_download -> {
                    startActivity(Intent(this@MainActivity, DownloadActivity::class.java))
                }
                R.id.item_category -> {
                    startActivity(Intent(this@MainActivity, CategoryActivity::class.java))
                }
            }
            true
        }
        switchModeBtn = binding.mNavigationView.getHeaderView(0).findViewById<ImageView>(R.id.iv_head_switch_mode)
        val isNight: Boolean = SharedUtil.read(Constants.IS_NIGHT_MODE_KEY, false)
        if (isNight) {
            switchModeBtn.setImageResource(R.drawable.ic_switch_daily)
        } else {
            switchModeBtn.setImageResource(R.drawable.ic_switch_night)
        }
        switchModeBtn.setOnClickListener {
            switchNightMode()
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    /**
     * 再按一次退出
     */
    private var mExitTime: Long = 0
    override fun onBackPressed() {
//        val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
//        val homeFragment = navHostFragment.childFragmentManager.fragments[0] as HomeFragment
        if (System.currentTimeMillis() - mExitTime > 2000) run {
            Snackbar.make(binding.content, "再按一次退出", Snackbar.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

    /**
     * 日夜间模式切换
     */
    private fun switchNightMode() {
        val isNight: Boolean = SharedUtil.read(Constants.IS_NIGHT_MODE_KEY, false)
        if (isNight) { // 日间模式
            val skinName = SharedUtil.read(Constants.SKIN_NAME_BEFORE_NIGHT_MODE, "cyan")
            SupportSkinHelper.setSkin(skinName)
            switchModeBtn.setImageResource(R.drawable.ic_switch_daily)
        } else { // 夜间模式
            SupportSkinHelper.setSkin("night")
            switchModeBtn.setImageResource(R.drawable.ic_switch_night)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        PermissionsMgr.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)

        val fragments = this.supportFragmentManager.fragments
        if (fragments.isNotEmpty()) {
            val navHostFragment = fragments.first() as NavHostFragment
            if (navHostFragment.isAdded) {
                navHostFragment.childFragmentManager.fragments.forEach {
                    if (it is SkinCompatSupportable) {
                        it.applySkin()
                    }
                }
            }
        }
    }

}
