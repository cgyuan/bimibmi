package com.cyuan.bimibimi.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.databinding.FragmentHistoryBinding
import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.ui.home.adapter.HistoryAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.HistoryViewModelFactory
import com.cyuan.bimibimi.widget.MessageDialog
import kotlinx.android.synthetic.main.fragment_history.*
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
        val binding = DataBindingUtil.inflate<FragmentHistoryBinding>(
            inflater,
            R.layout.fragment_history,
            container,
            false
        )
        binding.viewModel = viewModel
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
        emptyView.setEmptyMessageId(R.string.history_empty)
        recyclerView.adapter = adapter

        viewModel.movies.observe(this, Observer {
            if (it != null) {
                if (it.isEmpty()) {
                    viewModel.viewState.postValue(Constants.ViewState.EMPTY)
                } else {
                    viewModel.viewState.postValue(Constants.ViewState.DONE)
                }
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