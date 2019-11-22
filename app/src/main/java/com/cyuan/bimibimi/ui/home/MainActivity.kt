package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.cyuan.bimibimi.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var navController: NavController
    private lateinit var homeItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.fragment)

        homeItem = navigationView.menu.findItem(R.id.homeFragment)
        navigationView.setNavigationItemSelectedListener {
            closeDrawer()
            if (it.itemId in listOf(R.id.homeFragment, R.id.favoriteFragment, R.id.historyFragment)) {
                NavigationUI.onNavDestinationSelected(it, navController)
            }
            true
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
        if (System.currentTimeMillis() - mExitTime > 2000) run {
            Snackbar.make(content, "再按一次退出", Snackbar.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            finish()
        }
    }

}
