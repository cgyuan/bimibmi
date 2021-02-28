package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.databinding.FragmentDailyUpdateBinding
import com.cyuan.bimibimi.ui.base.BaseFragment
import com.cyuan.bimibimi.ui.base.bindEmptyViewCallback
import com.cyuan.bimibimi.ui.home.adapter.DailySectionAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.DailyUpdateViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.DailyUpdateViewModelFactory
import com.cyuan.bimibimi.widget.TopSmoothScroller
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.adapter.TabAdapter
import q.rorbin.verticaltablayout.widget.ITabView
import q.rorbin.verticaltablayout.widget.TabView
import skin.support.widget.SkinCompatSupportable


class DailyUpdateFragment : BaseFragment(), SkinCompatSupportable {

    private lateinit var binding: FragmentDailyUpdateBinding
    private lateinit var layoutManager: GridLayoutManager
    private val dayOfWeekList = listOf("一", "二", "三", "四", "五", "六", "日")
    private lateinit var topSmoothScroller: TopSmoothScroller
    private var mSuspensionHeight: Int = 0
    private var mCurrentPosition = 0
    private var isClickedTab = false

    private val sectionOffsetPos: IntArray = IntArray(dayOfWeekList.size + 1)

    private val viewModel by viewModels<DailyUpdateViewModel> {
        DailyUpdateViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDailyUpdateBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sectionOffsetPos[0] = 0
        bindEmptyViewCallback(this)
        binding.emptyView.bind(binding.recyclerView)
        setHasOptionsMenu(true)
        binding.toolbarLayout.mToolbar.title = "新番播放表"
        binding.toolbarLayout.mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        binding.toolbarLayout.mToolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        viewModel.fetchDailyUpdateMovie()

        layoutManager = GridLayoutManager(activity, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if(binding.recyclerView.adapter?.getItemViewType(position) == DailySectionAdapter.VIEW_TYPE_TOP_BAR) {
                    return 2
                }
                return 1
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        topSmoothScroller = TopSmoothScroller(requireActivity())

        viewModel.dailyUpdateList.observe(viewLifecycleOwner, Observer {
            binding.recyclerView.adapter = DailySectionAdapter(requireContext(), dayOfWeekList, it)
            it.forEachIndexed { index, list ->
                sectionOffsetPos[index + 1] = sectionOffsetPos[index] + list.size + 1
            }
        })

        binding.mTabLayout.setTabAdapter(object: TabAdapter {
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

        binding.mTabLayout.addOnTabSelectedListener(object : VerticalTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabView?, position: Int) {}

            override fun onTabSelected(tab: TabView?, position: Int) {
                topSmoothScroller.targetPosition = sectionOffsetPos[position]
                layoutManager.startSmoothScroll(topSmoothScroller)
                isClickedTab = true
            }

        })

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mSuspensionHeight = binding.mSuspensionBar.height
                if (newState == RecyclerView.SCROLL_STATE_IDLE ||
                        newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isClickedTab = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val tabIndex = getTabIndex(firstVisibleItemPosition)
                if (!isClickedTab) {
                    binding.mTabLayout.setTabSelected(tabIndex, false)
                }

                if (layoutManager is GridLayoutManager) {
                    val viewList = mutableListOf<View?>()
                    //找到下几个个itemView（因为是GridLayoutManager）
                    for (i in 1..layoutManager.spanCount) {
                        viewList.add(layoutManager.findViewByPosition(firstVisibleItemPosition + i))
                    }

                    if(viewList.any { view -> view is TextView }) {
                        val view = viewList.filterIsInstance<TextView>()[0]
                        if (view.top in 1..mSuspensionHeight) {
                            //需要对悬浮条进行移动
                            binding.mSuspensionBar.y = (-(mSuspensionHeight - view.top)).toFloat()
                        }
                    } else {
                        //保持在原来的位置
                        binding.mSuspensionBar.y = 0f
                    }
                }

                if (mCurrentPosition != tabIndex) {
                    mCurrentPosition = tabIndex
                    updateSuspensionBar()
                }
            }
        })
        updateSuspensionBar()
        applySkin()
    }

    private fun getTabIndex(firstVisibleItemPosition: Int): Int {
        sectionOffsetPos.forEachIndexed { index, value ->
            if (firstVisibleItemPosition >= value && firstVisibleItemPosition < sectionOffsetPos[index + 1]) {
                return index
            }
        }
        return 0
    }

    private fun updateSuspensionBar() {
        binding.mSuspensionBar.text = "星期${dayOfWeekList[mCurrentPosition]}"
    }


    override fun reload() {
        viewModel.fetchDailyUpdateMovie()
    }

    override fun applySkin() {
        SupportSkinHelper.tintVerticalTabLayout(requireActivity(), binding.mTabLayout)
    }

}