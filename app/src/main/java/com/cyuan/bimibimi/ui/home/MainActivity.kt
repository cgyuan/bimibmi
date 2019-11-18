package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.cyuan.bimibimi.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var homeItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = Navigation.findNavController(this, R.id.fragment)

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

    override fun onBackPressed() {
        homeItem.isChecked = true
        super.onBackPressed()
    }

}
