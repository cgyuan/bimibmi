package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.request.StringRequest
import com.cyuan.bimibimi.parser.HtmlDataParser
import kotlinx.android.synthetic.main.activity_main.*
import com.alibaba.android.vlayout.DelegateAdapter
import com.cyuan.bimibimi.ui.home.adapter.GridHelperAdapter
import com.cyuan.bimibimi.ui.home.adapter.HomeBannerAdapter
import com.cyuan.bimibimi.ui.home.adapter.HomeGridHelperAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StringRequest().listen(object : Callback{
            override fun onFailure(e: Exception) {
                println("fail to load data")
            }

            override fun onResponseString(response: String) {
                println(response)


                val layoutManager = VirtualLayoutManager(this@MainActivity)

                recyclerView.layoutManager = layoutManager

                val viewPool = RecyclerView.RecycledViewPool()
                recyclerView.setRecycledViewPool(viewPool)
                viewPool.setMaxRecycledViews(0, 10)

                val adapters = DelegateAdapter(layoutManager, false)


                val data = HtmlDataParser.parseBanner(response)
                val bannerList = data.first.map {
                    it.cover
                }

                val bannerAdapter = HomeBannerAdapter(this@MainActivity, bannerList, data.first)
                adapters.addAdapter(bannerAdapter)


                for (section in data.second) {
                    adapters.addAdapter(HomeGridHelperAdapter(this@MainActivity, section.list,
                        R.layout.home_section_item_layout,
                        R.layout.hom_section_header_layout,
                        section.title, section.moreLink))
            //                    adapters.addAdapter(GridHelperAdapter(this@MainActivity, section))
                }
                recyclerView.adapter = adapters
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cat_topappbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
