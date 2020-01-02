package com.cyuan.bimibimi.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.databinding.FragmentFavoriteBinding
import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.ui.home.adapter.HistoryAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModelFactory
import com.cyuan.bimibimi.widget.GridDividerItemDecoration
import com.cyuan.bimibimi.widget.MessageDialog
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class HistoryFragment : Fragment(), View.OnLongClickListener {

    private val adapter by lazy { HistoryAdapter(viewModel, this) }

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
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as MainActivity).setSupportActionBar(mToolbar)
        mToolbar.title = "历史记录"
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        mToolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(GridDividerItemDecoration(context!!, dp2px(15F), dp2px(15F), isNeedSpace = true, isLastRowNeedSpace = true, color = resources.getColor(R.color.window_background)))

        viewModel.movies.observe(this, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete) {
            MessageDialog.Builder(activity)
                .setMessage(R.string.delete_history_msg)
                .setListener(object : MessageDialog.OnListener {
                    override fun confirm(dialog: Dialog?) {
                        val repository = HistoryRepository.getInstance(AppDatabase.instance.historyDao())
                        repository.clearAll()
                    }

                    override fun cancel(dialog: Dialog?) {}

                }).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLongClick(v: View?): Boolean {
        MessageDialog.Builder(activity)
            .setMessage(R.string.delete_history_msg)
            .setListener(object : MessageDialog.OnListener {
                override fun confirm(dialog: Dialog?) {
                    val history = v!!.tag as History
                    val repository = HistoryRepository.getInstance(AppDatabase.instance.historyDao())
                    repository.removeHistory(history)
                }

                override fun cancel(dialog: Dialog?) {}

            }).show()
        return true
    }
}