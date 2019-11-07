package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.extension.dp2px
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.StringRequest
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.leochuan.CarouselLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StringRequest().listen(object : Callback{
            override fun onFailure(e: Exception) {
                println("fail to load data")
            }

            override fun onResponseString(response: String) {

                val list = HtmlDataParser.parseBanner(response)

                val carouselLayoutManager = CarouselLayoutManager(baseContext, dp2px(100f))
                carouselLayoutManager.itemSpace = dp2px(80f)
                carouselLayoutManager.moveSpeed = 0.3f
                recycler.layoutManager = carouselLayoutManager
                val bannerAdapter = BannerAdapter(this@MainActivity, list)
                recycler.adapter = bannerAdapter
                bannerAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cat_topappbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
