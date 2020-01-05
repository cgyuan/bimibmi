package com.cyuan.bimibimi.ui.download

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.model.ITask
import com.cyuan.bimibimi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import skin.support.widget.SkinCompatSupportable

class DownloadActivity : BaseActivity(), SkinCompatSupportable, ITask {

    var mTabTitles = arrayOf("正在下载", "下载完成")
    private var mDownloadingTaskFragment: DownloadingTaskFragment? = null
    lateinit var mDownloadHelper: DownloadHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        mToolbar.run {
            setSupportActionBar(this)
            title = "缓存管理"
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { finish() }
        }

        mDownloadHelper = DownloadHelper.getInstance(this, this)
        mDownloadHelper.setITask(this)
        mDownloadHelper.initDownloadLiveData(this)

        mViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return if (position == 0) {
                    mDownloadingTaskFragment = DownloadingTaskFragment()
                    mDownloadingTaskFragment!!
                } else {
                    DownloadedTaskFragment()
                }
            }

            override fun getCount() = mTabTitles.size

            override fun getPageTitle(position: Int) = mTabTitles[position]

        }

        mTabLayout.setupWithViewPager(mViewPager)
    }

    override fun onResume() {
        super.onResume()
        mToolbar.title = "缓存管理"
    }

    override fun onDestroy() {
        mDownloadHelper.setITask(null)
        super.onDestroy()
    }

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)
    }

    override fun updateIngTask(taskInfos: MutableList<DownloadTaskInfo>?) {
        mDownloadingTaskFragment?.refresh(taskInfos)
    }

    override fun repeatAdd(s: String?) {

    }
}