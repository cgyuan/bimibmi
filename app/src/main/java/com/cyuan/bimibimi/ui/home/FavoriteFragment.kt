package com.cyuan.bimibimi.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.GlobalUtil
import com.cyuan.bimibimi.databinding.FragmentFavoriteBinding
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.BaseFragment
import com.cyuan.bimibimi.ui.home.adapter.FavoriteMovieAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModelFactory
import com.cyuan.bimibimi.widget.MessageDialog

class FavoriteFragment : BaseFragment(), View.OnLongClickListener {

    private lateinit var binding: FragmentFavoriteBinding
    private val adapter by lazy { FavoriteMovieAdapter(requireContext(), this) }

    private val viewModel by viewModels<FavoriteMovieViewModel> {
        FavoriteMovieViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentFavoriteBinding>(
            inflater,
            R.layout.fragment_favorite,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun reload() {
        viewModel.host.value = GlobalUtil.host
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.toolbarLayout.mToolbar.title = getString(R.string.favorite_title)
        binding.toolbarLayout.mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        binding.toolbarLayout.mToolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        binding.recyclerView.adapter = adapter
        val lm = binding.recyclerView.layoutManager as GridLayoutManager
        lm.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (adapter.footerLayoutId != null && adapter.itemCount - 1 == position) {
                    return 2
                }
                return 1
            }

        }

        viewModel.movies.observe(viewLifecycleOwner, Observer {
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

    override fun onLongClick(v: View?): Boolean {
        MessageDialog.Builder(activity)
            .setMessage(R.string.delete_favorite_msg)
            .setListener(object : MessageDialog.OnListener {
                override fun confirm(dialog: Dialog?) {
                    val favoriteMovie = v!!.tag as FavoriteMovie
                    val movie = Movie()
                    movie.href = favoriteMovie.href
                    viewModel.removeMovie(movie)
                }

                override fun cancel(dialog: Dialog?) {}

            }).show()
        return true
    }
}