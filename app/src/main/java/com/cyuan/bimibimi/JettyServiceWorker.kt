package com.cyuan.bimibimi

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import zmovie.com.dlan.JettyResourceService

class JettyServiceWorker(
    val context: Context,
    workerParams: WorkerParameters
): Worker(context, workerParams) {
    override fun doWork(): Result {
        context.startService(Intent(context, JettyResourceService::class.java))
        return Result.success()
    }
}