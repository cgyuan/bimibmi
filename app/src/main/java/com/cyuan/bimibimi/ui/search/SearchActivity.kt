package com.cyuan.bimibimi.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.extension.hideSoftKeyboard
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.kotlin.base.widgets.DefaultTextWatcher
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.search_input_layout.*

class SearchActivity: AppCompatActivity() {

    private lateinit var searchKeyWord: String
    private lateinit var searchAdapter: SearchGridHelperAdapter
    private val movieList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchKeyWord = intent.getStringExtra(Constants.Search.KEYWORD)!!

        initSearchBar()

        initRecyclerView()

        loadData()
    }

    private fun initRecyclerView() {
        val layoutManager = VirtualLayoutManager(this)

        recyclerView.layoutManager = layoutManager

        val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 10)

        val adapters = DelegateAdapter(layoutManager, true)

        searchAdapter = SearchGridHelperAdapter(
            this,
            movieList,
            R.layout.home_section_item_layout
        )
        adapters.addAdapter(searchAdapter)
        recyclerView.adapter = adapters
    }

    private fun initSearchBar() {
        searchEdit.setText(searchKeyWord)
        searchTextClear.visibility = View.VISIBLE
        searchEdit.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchKeyWord = s.toString()
                if (TextUtils.isEmpty(s)) {
                    searchBtn.isEnabled = false
                    searchTextClear.visibility = View.INVISIBLE
                } else {
                    searchBtn.isEnabled = true
                    searchTextClear.visibility = View.VISIBLE
                }
            }
        })
        searchTextClear.setOnClickListener {
            searchEdit.setText("")
        }
        searchBack.setOnClickListener {
            finish()
        }
        searchBtn.setOnClickListener {
            movieList.clear()
            searchAdapter.notifyDataSetChanged()
            hideSoftKeyboard()
            loadData()
        }
    }

    private fun loadData() {
        HtmlDataParser.parseSearch(searchKeyWord, object : ParseResultCallback<List<Movie>> {
            override fun onSuccess(data: List<Movie>) {
                movieList.addAll(data)
                searchAdapter.notifyDataSetChanged()
            }

            override fun onFail(msg: String) {
                Toast.makeText(this@SearchActivity, msg, Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {
        fun launch(context: Context, keyWord: String) {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constants.Search.KEYWORD, keyWord)
            context.startActivity(intent)
        }
    }
}