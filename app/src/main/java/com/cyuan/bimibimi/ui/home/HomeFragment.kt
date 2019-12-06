package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.databinding.FragmentHomeBinding
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.repository.OnlineMovieRepository
import com.cyuan.bimibimi.ui.base.CommonGridHelperAdapter
import com.cyuan.bimibimi.ui.base.UICallback
import com.cyuan.bimibimi.ui.base.bindEmptyViewCallback
import com.cyuan.bimibimi.ui.home.adapter.HomeBannerAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.HomeViewModel
import com.cyuan.bimibimi.ui.search.SearchActivity
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() , UICallback {

    private lateinit var adapters: DelegateAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater)
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
        emptyView.bind(recyclerView)
        setHasOptionsMenu(true)
        toolbar.title = ""
        (activity as MainActivity).setSupportActionBar(toolbar)
        initSearchView()
        initRecyclerView()

        viewModel.fetchHomeData()

        viewModel.bannerList.observe(this, Observer<List<Movie>> { bannerList ->
            val bannerImgList = bannerList?.map {
                it.cover
            }
            val bannerAdapter = HomeBannerAdapter(context!!, bannerImgList!!, bannerList!!)
            adapters.addAdapter(bannerAdapter)
        })

        viewModel.sectionList.observe(this, Observer { sectionList ->
            for (section in sectionList) {
                adapters.addAdapter(
                    CommonGridHelperAdapter(
                        context!!, section.list,
                        R.layout.movie_card_item_layout,
                        R.layout.hom_section_header_layout,
                        section.title, section.moreLink
                    )
                )
            }
            recyclerView.adapter = adapters
        })
    }

    override fun reload() {
        viewModel.fetchHomeData()
    }

    private fun initRecyclerView() {
        val layoutManager = VirtualLayoutManager(context!!)

        recyclerView.layoutManager = layoutManager

        val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 10)

        adapters = DelegateAdapter(layoutManager, false)
    }

    private fun initSearchView() {
        searchView.setVoiceSearch(false)
        searchView.setEllipsize(true)
        searchView.setCursorDrawable(R.drawable.custom_cursor)
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
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
        searchView.setMenuItem(menu.findItem(R.id.id_action_search))
    }
}