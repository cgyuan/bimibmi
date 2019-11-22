package com.cyuan.bimibimi.network.utils

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.logWarn
import com.cyuan.bimibimi.core.utils.GlobalUtil.getApplicationMetaData
import com.cyuan.bimibimi.core.utils.SharedUtil
import com.cyuan.bimibimi.core.utils.SignUtil
import java.util.*

/**
 * 获取各项基础数据的工具类。
 *
 */
object Utility {

    private val TAG = "Utility"

    private var deviceSerial: String? = null

    /**
     * 获取设备的品牌和型号，如果无法获取到，则返回Unknown。
     * @return 会以此格式返回数据：品牌 型号。
     */
    val deviceName: String
        get() {
            var deviceName = Build.BRAND + " " + Build.MODEL
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = "unknown"
            }
            return deviceName
        }

    /**
     * 获取当前App的版本号。
     * @return 当前App的版本号。
     */
    val appVersion: String
        get() {
            var version = ""
            try {
                val packageManager = App.getContext().packageManager
                val packInfo = packageManager.getPackageInfo(App.getPackageName(), 0)
                version = packInfo.versionName
            } catch (e: Exception) {
                logWarn("getAppVersion", e.message, e)
            }

            if (TextUtils.isEmpty(version)) {
                version = "unknown"
            }
            return version
        }

    /**
     * 获取App网络请求验证参数，用于辨识是不是官方渠道的App。
     */
    val appSign: String
        get() {
            return MD5.encrypt(SignUtil.getAppSignature() + appVersion)
        }

    /**
     * 获取设备的序列号。如果无法获取到设备的序列号，则会生成一个随机的UUID来作为设备的序列号，UUID生成之后会存入缓存，
     * 下次获取设备序列号的时候会优先从缓存中读取。
     * @return 设备的序列号。
     */
    @SuppressLint("HardwareIds")
    fun getDeviceSerial(): String {
        if (deviceSerial == null) {
            var deviceId: String? = null
            val appChannel =  getApplicationMetaData("APP_CHANNEL")
            if ("google" != appChannel || "samsung" != appChannel) {
                try {
                    deviceId = Settings.Secure.getString(App.getContext().contentResolver, Settings.Secure.ANDROID_ID)
                } catch (e: Exception) {
                    logWarn(TAG, "get android_id with error", e)
                }
                if (!TextUtils.isEmpty(deviceId) && deviceId!!.length < 255) {
                    deviceSerial = deviceId
                    return deviceSerial.toString()
                }
            }
            var uuid = SharedUtil.read(Constants.UUID, "")
            if (!TextUtils.isEmpty(uuid)) {
                deviceSerial = uuid
                return deviceSerial.toString()
            }
            uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase()
            SharedUtil.save(Constants.UUID, uuid)
            deviceSerial = uuid
            return deviceSerial.toString()
        } else {
            return deviceSerial.toString()
        }
    }

}
