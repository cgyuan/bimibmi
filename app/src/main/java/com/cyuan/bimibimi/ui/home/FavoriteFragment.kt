package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.databinding.FragmentFavoriteBinding
import com.cyuan.bimibimi.ui.home.adapter.FavoriteMovieAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModelFactory
import com.cyuan.bimibimi.widget.GridDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class FavoriteFragment : Fragment() {

    private val adapter by lazy { FavoriteMovieAdapter() }

    private val viewModel by viewModels<FavoriteMovieViewModel> {
        FavoriteMovieViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFavoriteBinding>(
            inflater,
            R.layout.fragment_favorite,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.title = "我的收藏"
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.addItemDecoration(GridDividerItemDecoration(context!!, dp2px(15F), dp2px(15F), isNeedSpace = true, isLastRowNeedSpace = true, color = resources.getColor(R.color.window_background)))

        viewModel.movies.observe(this, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
    }
}