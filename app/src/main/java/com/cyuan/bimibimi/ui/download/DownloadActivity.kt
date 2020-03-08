package com.cyuan.bimibimi.ui.download

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.FileUtils
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.model.DownloadTaskInfo
import com.cyuan.bimibimi.model.ITask
import com.cyuan.bimibimi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_download.*
import kotlinx.android.synthetic.main.download_header_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import skin.support.widget.SkinCompatSupportable

class DownloadActivity : BaseActivity(), SkinCompatSupportable, ITask {

    var mTabTitles = arrayOf("正在下载", "下载完成")
    private var mDownloadingTaskFragment: DownloadingTaskFragment? = null
    private var mDownloadedTaskFragment: DownloadedTaskFragment? = null
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

        mDownloadHelper = DownloadHelper.getInstance(this.application, this)
        mDownloadHelper.setITask(this)
        mDownloadHelper.initDownloadLiveData(this)

        mViewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                return if (position == 0) {
                    mDownloadingTaskFragment = DownloadingTaskFragment()
                    mDownloadingTaskFragment!!
                } else {
                    mDownloadedTaskFragment = DownloadedTaskFragment()
                    mDownloadedTaskFragment!!
                }
            }

            override fun getCount() = mTabTitles.size

            override fun getPageTitle(position: Int) = mTabTitles[position]

        }

        mDownloadPathTv.text = "当前文件下载路径:${FileUtils.cachePath}"

        mTabLayout.setupWithViewPager(mViewPager)
    }

    override fun onResume() {
        super.onResume()
        mToolbar.title = "缓存管理"
        mMemoryStatusTv.text = "已下载文件${FileUtils.cacheSize}，机身剩余可用${FileUtils.spaceSize[0]}"
    }

    override fun onDestroy() {
        mDownloadHelper.setITask(null)
        super.onDestroy()
    }

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)
    }

    override fun updateIngTask(taskInfos: MutableList<DownloadTaskInfo>?) {
        val size = taskInfos?.size ?: 0
        val statusText = if (size > 0) {
            "正在下载${size}个文件"
        } else {
            "暂无下载任务"
        }
        mTaskStatusTv.text = statusText
        mDownloadingTaskFragment?.refresh(taskInfos)
    }

    override fun updateDoneTask(taskInfos: MutableList<DownloadTaskInfo>?) {
        mDownloadedTaskFragment?.refresh(taskInfos)
        mMemoryStatusTv.text = "已下载文件${FileUtils.cacheSize}，机身剩余可用${FileUtils.spaceSize[0]}"
    }

    override fun repeatAdd(s: String?) {

    }
}