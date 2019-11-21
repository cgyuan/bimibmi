package com.cyuan.bimibimi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.databinding.FragmentFavoriteBinding
import com.cyuan.bimibimi.ui.home.adapter.HistoryAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModelFactory
import com.cyuan.bimibimi.widget.GridDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class HistoryFragment : Fragment() {

    private val adapter by lazy { HistoryAdapter() }

    private val viewModel by viewModels<HistoryViewModel> {
        HistoryViewModelFactory()
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
        toolbar.title = "历史记录"
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(GridDividerItemDecoration(context!!, dp2px(15F), dp2px(15F), isNeedSpace = true, isLastRowNeedSpace = true))

        viewModel.movies.observe(this, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
    }
}