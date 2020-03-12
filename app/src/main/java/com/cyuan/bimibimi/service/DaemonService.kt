package com.cyuan.bimibimi.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder

class DaemonService : Service() {

    class LocalBinder: Binder()


    override fun onBind(intent: Intent?): IBinder? {
        return LocalBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        bindService(Intent(this, JettyResourceService::class.java), MyConnection(), Context.BIND_AUTO_CREATE)
        return super.onStartCommand(intent, flags, startId)
    }


    internal inner class MyConnection: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bindService(Intent(this@DaemonService, JettyResourceService::class.java), MyConnection(), Context.BIND_AUTO_CREATE)
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
    }
}