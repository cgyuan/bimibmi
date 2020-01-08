package com.cyuan.bimibimi.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.VirtualLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.core.extension.logDebug
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.cyuan.bimibimi.ui.base.InfiniteScrollListener
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment(private var category: String) : Fragment() {

    private lateinit var categoryAdapter: CategoryGridHelperAdapter
    private val movieList = mutableListOf<Movie>()
//    private lateinit var category: String
    private var currentPage = 1

    /**
     * 判断是否正在加载更多。
     */
    private var isLoading = false

    var isLoadFailed = false

    /**
     * 判断是否还有更多数据。
     *
     * @return 当服务器端没有更多数据时，此值为true，否则此值为false。
     */
    var isNoMoreData = false
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        logDebug("CategoryFragment init: ${this}")

//        category = arguments?.getString(PlayerKeys.MOVIE_CATEGORY)!!

        val layoutManager = VirtualLayoutManager(context!!)

        recyclerView.layoutManager = layoutManager

        val viewPool = RecyclerView.RecycledViewPool()
        recyclerView.setRecycledViewPool(viewPool)
        viewPool.setMaxRecycledViews(0, 10)

        val adapters = DelegateAdapter(layoutManager, true)

        categoryAdapter = CategoryGridHelperAdapter(
            this,
            movieList,
            R.layout.home_movie_card_layout,
            R.layout.loading_footer
        )
        adapters.addAdapter(categoryAdapter)
        recyclerView.adapter = adapters
    }

    override fun onResume() {
        super.onResume()
        if (currentPage == 1) {
            loadData()
            recyclerView.addOnScrollListener(object : InfiniteScrollListener(recyclerView.layoutManager!!) {
                override fun onLoadMore() {
                    currentPage += 1
                    loadData()
                }

                override fun isDataLoading() = isLoading

                override fun isNoMoreData() = isNoMoreData

            })
        }
    }

    fun loadData() {
        isLoading = true
        isLoadFailed = false
        HtmlDataParser.parseCategoryMovie("$category-$currentPage", object :
            ParseResultCallback<List<Movie>> {
            override fun onSuccess(data: List<Movie>) {
                if (data.isEmpty()) {
                    isNoMoreData = true
                    categoryAdapter.notifyItemChanged(categoryAdapter.itemCount - 1)
                    return
                }
                isLoading = false
                val itemCount = categoryAdapter.itemCount
                movieList.addAll(data)
                categoryAdapter.notifyItemRangeInserted(itemCount, data.size)
            }

            override fun onFail(msg: String) {
                isLoading = false
                isLoadFailed = true
                categoryAdapter.notifyItemChanged(categoryAdapter.itemCount - 1)
                Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show()
            }

        })
    }
}