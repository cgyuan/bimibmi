package com.cyuan.bimibimi.ui.setting

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.checkItem
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.CacheUtils
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.widget.MessageDialog
import com.tencent.bugly.beta.Beta

class SettingFragment: PreferenceFragmentCompat() {
    private lateinit var mClearCacheItem: Preference
    private lateinit var mCheckUpdateItem: Preference
    private lateinit var mSelectPlayerItem: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting_preference)


        mClearCacheItem = findPreference("clearCache")!!
        mCheckUpdateItem = findPreference("checkUpdate")!!
        mSelectPlayerItem = findPreference("selectPlayer")!!

        mClearCacheItem.summary = CacheUtils.getTotalCacheSize(activity)
        mCheckUpdateItem.summary = GlobalUtil.appVersionName
        mSelectPlayerItem.summary = mSelectPlayerItem.sharedPreferences.getString(Constants.SET_PLAYER, Constants.Player.EXO_PLAYER)

        mClearCacheItem.setOnPreferenceClickListener {
            MessageDialog.Builder(activity)
                .setMessage("确定清除缓存吗？")
                .setListener(object : MessageDialog.OnListener {
                    override fun confirm(dialog: Dialog?) {
                        CacheUtils.clearAllCache(activity)
                        Toast.makeText(activity, "清理成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun cancel(dialog: Dialog?) {}
                }).show()
            true
        }
        mCheckUpdateItem.setOnPreferenceClickListener {
            Beta.checkUpgrade()
            true
        }
        mSelectPlayerItem.setOnPreferenceClickListener {
            val dlg =
                MaterialDialog(context!!).listItemsSingleChoice(R.array.arr_player) { dialog, index, text ->
                    mSelectPlayerItem.sharedPreferences
                        .edit()
                        .putString(Constants.SET_PLAYER, text.toString())
                        .apply()
                    mSelectPlayerItem.summary = text
                }
            val index = resources.getStringArray(R.array.arr_player).indexOf(mSelectPlayerItem.summary)
            dlg.checkItem(index)
            dlg.show()
            true
        }
    }
}