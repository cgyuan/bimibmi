package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.Section
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.cyuan.bimibimi.ui.home.adapter.HomeBannerAdapter
import com.cyuan.bimibimi.ui.home.adapter.HomeGridHelperAdapter
import com.cyuan.bimibimi.ui.search.SearchActivity
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        toolbar.title = ""
        (activity as MainActivity).setSupportActionBar(toolbar)
        initSearchView()

        HtmlDataParser.parseHomePage(object : ParseResultCallback<Pair<MutableList<Movie>, List<Section>>> {
            override fun onSuccess(data: Pair<MutableList<Movie>, List<Section>>) {
                val layoutManager = VirtualLayoutManager(context!!)

                recyclerView.layoutManager = layoutManager

                val viewPool = RecyclerView.RecycledViewPool()
                recyclerView.setRecycledViewPool(viewPool)
                viewPool.setMaxRecycledViews(0, 10)

                val adapters = DelegateAdapter(layoutManager, false)


                val bannerList = data.first.map {
                    it.cover
                }

                val bannerAdapter = HomeBannerAdapter(context!!, bannerList, data.first)
                adapters.addAdapter(bannerAdapter)


                for (section in data.second) {
                    adapters.addAdapter(
                        HomeGridHelperAdapter(context!!, section.list,
                        R.layout.home_section_item_layout,
                        R.layout.hom_section_header_layout,
                        section.title, section.moreLink)
                    )
                    //                    adapters.addAdapter(GridHelperAdapter(this@MainActivity, section))
                }
                recyclerView.adapter = adapters
            }

            override fun onFail(msg: String) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

        })
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