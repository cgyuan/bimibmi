package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyuan.bimibimi.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment()).commitAllowingStateLoss()
    }
}
