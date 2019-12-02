package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.databinding.FragmentDailyUpdateBinding
import com.cyuan.bimibimi.ui.base.UICallback
import com.cyuan.bimibimi.ui.base.bindEmptyViewCallback
import com.cyuan.bimibimi.ui.home.adapter.DailySectionAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.DailyUpdateViewModelFactory
import com.cyuan.bimibimi.ui.home.viewmodel.DailyUpdateViewModel
import com.cyuan.bimibimi.widget.TopSmoothScroller
import kotlinx.android.synthetic.main.fragment_daily_update.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.emptyView
import kotlinx.android.synthetic.main.fragment_home.recyclerView
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.adapter.TabAdapter
import q.rorbin.verticaltablayout.widget.ITabView
import q.rorbin.verticaltablayout.widget.TabView


class DailyUpdateFragment : Fragment() , UICallback {

    private lateinit var linearLayoutManager: LinearLayoutManager
    private val dayOfWeekList = listOf("一", "二", "三", "四", "五", "六", "日")
    private lateinit var topSmoothScroller: TopSmoothScroller
    private var mSuspensionHeight: Int = 0
    private var mCurrentPosition = 0
    private var isClickedTab = false

    private val viewModel by viewModels<DailyUpdateViewModel> {
        DailyUpdateViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDailyUpdateBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindEmptyViewCallback(this)
        emptyView.bind(recyclerView)
        setHasOptionsMenu(true)
        toolbar.title = "新番播放表"
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        viewModel.fetchDailyUpdateMovie()

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        topSmoothScroller = TopSmoothScroller(activity!!)

        viewModel.dailyUpdateList.observe(this, Observer {
            recyclerView.adapter = DailySectionAdapter(context!!, dayOfWeekList, it)
        })

        tabLayout.setTabAdapter(object: TabAdapter {
            override fun getIcon(position: Int) = null

            override fun getBadge(position: Int) = null

            override fun getBackground(position: Int) = -1

            override fun getTitle(position: Int): ITabView.TabTitle {
                return ITabView.TabTitle.Builder()
                    .setContent(dayOfWeekList[position])
                    .setTextColor(ContextCompat.getColor(context!!, R.color.white),
                        ContextCompat.getColor(context!!, R.color.gray_light_30))
                    .build()
            }

            override fun getCount()  = dayOfWeekList.size

        })

        tabLayout.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabView?, position: Int) {}

            override fun onTabSelected(tab: TabView?, position: Int) {
                topSmoothScroller.targetPosition = position
                linearLayoutManager.startSmoothScroll(topSmoothScroller)
                isClickedTab = true
            }

        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mSuspensionHeight = mSuspensionBar.height
                if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isClickedTab = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isClickedTab) {
                    tabLayout.setTabSelected(linearLayoutManager.findFirstVisibleItemPosition(), false)
                }

                //找到下一个itemView
                val view = linearLayoutManager.findViewByPosition(mCurrentPosition + 1)
                view?.let {
                    if (view.top in 1..mSuspensionHeight) {
                        //需要对悬浮条进行移动
                        mSuspensionBar.y = (-(mSuspensionHeight - view.top)).toFloat()
                    } else {
                        //保持在原来的位置
                        mSuspensionBar.y = 0f
                    }
                }

                if (mCurrentPosition != linearLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPosition = linearLayoutManager.findFirstVisibleItemPosition()
                    updateSuspensionBar()
                }
            }
        })
        updateSuspensionBar()
    }

    private fun updateSuspensionBar() {
        mSuspensionBar.text = "星期${dayOfWeekList[mCurrentPosition]}"
    }


    override fun reload() {
        viewModel.fetchDailyUpdateMovie()
    }

}