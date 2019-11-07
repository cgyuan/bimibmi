package com.cyuan.bimibimi

import android.app.Application
import com.cyuan.bimibimi.core.App
import zmovie.com.dlan.DlanLib

class BimibimiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        App.initialize(this)
        DlanLib.initDlan(this)
    }
}