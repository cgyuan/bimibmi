package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var switchModeBtn: ImageView
    private lateinit var navController: NavController
    private lateinit var homeItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.fragment)

        homeItem = navigationView.menu.findItem(R.id.homeFragment)
        navigationView.setNavigationItemSelectedListener {
            closeDrawer()
            if (it.itemId in listOf(R.id.homeFragment, R.id.favoriteFragment, R.id.historyFragment, R.id.dailyUpdateFragment)) {
                NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
        }
        switchModeBtn = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.iv_head_switch_mode)
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
        drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    /**
     * 再按一次退出
     */
    private var mExitTime: Long = 0
    override fun onBackPressed() {
//        val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
//        val homeFragment = navHostFragment.childFragmentManager.fragments[0] as HomeFragment
        if (System.currentTimeMillis() - mExitTime > 2000) run {
            Snackbar.make(content, "再按一次退出", Snackbar.LENGTH_SHORT).show()
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
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            SharedUtil.save(Constants.IS_NIGHT_MODE_KEY, false)
            switchModeBtn.setImageResource(R.drawable.ic_switch_daily)
        } else { // 夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            SharedUtil.save(Constants.IS_NIGHT_MODE_KEY, true)
            switchModeBtn.setImageResource(R.drawable.ic_switch_night)
        }
    }

}
