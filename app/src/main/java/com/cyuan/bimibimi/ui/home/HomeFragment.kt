package com.cyuan.bimibimi.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.databinding.FragmentHomeBinding
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.repository.OnlineMovieRepository
import com.cyuan.bimibimi.ui.base.BaseFragment
import com.cyuan.bimibimi.ui.base.CommonGridHelperAdapter
import com.cyuan.bimibimi.ui.base.bindEmptyViewCallback
import com.cyuan.bimibimi.ui.download.DownloadActivity
import com.cyuan.bimibimi.ui.home.adapter.HomeBannerAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.HomeViewModel
import com.cyuan.bimibimi.ui.search.SearchActivity
import com.miguelcatalan.materialsearchview.MaterialSearchView


class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private var bannerAdapter: HomeBannerAdapter? = null
    private lateinit var adapters: DelegateAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomeViewModel(OnlineMovieRepository.instance) as T
            }

        }).get(HomeViewModel::class.java)
        binding.activity = activity as MainActivity
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindEmptyViewCallback(this)
        binding.emptyView.bind(binding.recyclerView)
        setHasOptionsMenu(true)
        binding.mToolbar.title = ""
        (activity as MainActivity).setSupportActionBar(binding.mToolbar)
        initSearchView()
        initRecyclerView()

        viewModel.fetchHomeData()

        viewModel.bannerList.observe(viewLifecycleOwner, Observer<List<Movie>> { bannerList ->
            saveHost = GlobalUtil.host
            adapters.clear()
            val bannerImgList = bannerList?.map {
                it.cover
            }
            bannerAdapter = HomeBannerAdapter(requireContext(), bannerImgList!!, bannerList)
            adapters.addAdapter(bannerAdapter)
        })

        viewModel.sectionList.observe(viewLifecycleOwner, Observer { sectionList ->
            for (section in sectionList) {
                adapters.addAdapter(
                    CommonGridHelperAdapter(
                        requireContext(), section.list,
                        R.layout.home_movie_card_layout,
                        R.layout.hom_section_header_layout,
                        section.title, section.moreLink
                    )
                )
            }
            binding.recyclerView.adapter = adapters
            adapters.notifyDataSetChanged()
        })
    }

    override fun onStop() {
        bannerAdapter?.stopAutoPlay()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        bannerAdapter?.startAutoPlay()
    }

    override fun reload() {
        viewModel.fetchHomeData()
    }

    private fun initRecyclerView() {
        val layoutManager = VirtualLayoutManager(requireContext())

        binding.recyclerView.layoutManager = layoutManager

        val viewPool = RecyclerView.RecycledViewPool()
        binding.recyclerView.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 10)

        adapters = DelegateAdapter(layoutManager, false)
    }

    private fun initSearchView() {
        binding.searchView.setVoiceSearch(false)
        binding.searchView.setEllipsize(true)
        binding.searchView.setCursorDrawable(R.drawable.custom_cursor)
        binding.searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                SearchActivity.launch(context!!, query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        binding.searchView.setMenuItem(menu.findItem(R.id.id_action_search))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_action_download) {
            startActivity(Intent(activity, DownloadActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}