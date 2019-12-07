package com.cyuan.bimibimi.core.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object PermissionsMgr {
    
    private var TAG = "PermissionsMgr"
    //回调函数
    private var succCallback:MutableMap<Int,(()->Unit)> = mutableMapOf()
    private var failCallback:MutableMap<Int,(()->Unit)> = mutableMapOf()

    private var allPermissions: Set<String> = setOf(
        //文件权限
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun check(activity: Activity, permission: String): Boolean {
        val context = activity.applicationContext
        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun request(activity: Activity, permissions: Set<String>, succ: () -> Unit, fail: () -> Unit) {
        val context = activity.applicationContext
        context.getSystemService(AppCompatActivity.LOCATION_SERVICE)

        var requestCodeStr = ""
        val ps = permissions.toTypedArray().sortedArray()
        ps.forEach {
            requestCodeStr += it
        }
        var requestCode = requestCodeStr.hashCode()
        requestCode = requestCode.and(0xFFFF)
        if(succCallback.contains(requestCode)){
            Log.e(TAG, "request permission,requestCode=$requestCode")
            return
        }
        succCallback[requestCode] = succ
        failCallback[requestCode] = fail
        val permissionArray = ps.asList().toTypedArray()
        Log.e(TAG, "request,requestCode=${requestCode}")
        ActivityCompat.requestPermissions(activity, permissionArray, requestCode)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.e(TAG, "onRequestPermissionsResult,requestCode=${requestCode}")
        Log.e(TAG, permissions.toString())
        Log.e(TAG, grantResults.toString())
        var isAllPermission = true
        permissions.forEachIndexed { index, s ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "$s been grant")
            }
            if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "$s been denied")
                isAllPermission = false
            }
        }
        if(isAllPermission){
            val succ = succCallback[requestCode]
            succ?.invoke()
        }else{
            val fail = failCallback[requestCode]
            fail?.invoke()
        }
        succCallback.remove(requestCode)
        failCallback.remove(requestCode)

    }

    fun isAllPermissionReady(activity: Activity): Boolean {
        for (p in allPermissions) {
            if (!check(activity, p)) {
                return false
            }
        }
        return true
    }

    fun requestAllPermissionsAppNeed(activity: Activity, succ: () -> Unit, fail: () -> Unit) {
        request(
            activity,
            allPermissions,
            succ,
            fail
        )
    }

}