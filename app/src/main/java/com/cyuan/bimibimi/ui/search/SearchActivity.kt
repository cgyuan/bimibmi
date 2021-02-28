package com.cyuan.bimibimi.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.extension.hideSoftKeyboard
import com.cyuan.bimibimi.core.utils.SupportSkinHelper
import com.cyuan.bimibimi.databinding.ActivitySearchBinding
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.parser.DataParserAdapter
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.cyuan.bimibimi.ui.base.BaseActivity
import com.kotlin.base.widgets.DefaultTextWatcher
import skin.support.widget.SkinCompatSupportable

class SearchActivity: BaseActivity<ActivitySearchBinding>(), SkinCompatSupportable {

    private lateinit var searchKeyWord: String
    private lateinit var searchAdapter: SearchGridHelperAdapter
    private val movieList = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchKeyWord = intent.getStringExtra(Constants.Search.KEYWORD)!!

        initSearchBar()

        initRecyclerView()

        loadData()
    }

    private fun initRecyclerView() {
        val layoutManager = VirtualLayoutManager(this)

        binding.recyclerView.layoutManager = layoutManager

        val viewPool = RecyclerView.RecycledViewPool()
        binding.recyclerView.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 10)

        val adapters = DelegateAdapter(layoutManager, true)

        searchAdapter = SearchGridHelperAdapter(
            this,
            movieList,
            R.layout.movie_card_item_layout
        )
        adapters.addAdapter(searchAdapter)
        binding.recyclerView.adapter = adapters
    }

    private fun initSearchBar() {
        binding.search.searchEdit.setText(searchKeyWord)
        binding.search.searchTextClear.visibility = View.VISIBLE
        binding.search.searchEdit.addTextChangedListener(object : DefaultTextWatcher() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchKeyWord = s.toString()
                if (TextUtils.isEmpty(s)) {
                    binding.search.searchBtn.isEnabled = false
                    binding.search.searchTextClear.visibility = View.INVISIBLE
                } else {
                    binding.search.searchBtn.isEnabled = true
                    binding.search.searchTextClear.visibility = View.VISIBLE
                }
            }
        })
        binding.search.searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyboard()
                loadData()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.search.searchTextClear.setOnClickListener {
            binding.search.searchEdit.setText("")
        }
        binding.search.searchBack.setOnClickListener {
            finish()
        }
        binding.search.searchBtn.setOnClickListener {
            hideSoftKeyboard()
            loadData()
        }
    }

    private fun loadData() {
        DataParserAdapter.parseSearch(searchKeyWord, object : ParseResultCallback<List<Movie>> {
            override fun onSuccess(data: List<Movie>) {
                movieList.clear()
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

    override fun applySkin() {
        SupportSkinHelper.tintStatusBar(this)
    }
}