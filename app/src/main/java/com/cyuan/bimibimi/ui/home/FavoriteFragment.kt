package com.cyuan.bimibimi.ui.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.databinding.FragmentFavoriteBinding
import com.cyuan.bimibimi.db.repository.RepositoryProvider
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.home.adapter.FavoriteMovieAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModelFactory
import com.cyuan.bimibimi.widget.MessageDialog
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class FavoriteFragment : Fragment(), View.OnLongClickListener {

    private val adapter by lazy { FavoriteMovieAdapter(this) }

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mToolbar.title = "我的收藏"
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer)
        mToolbar.setNavigationOnClickListener {
            (activity as MainActivity).openDrawer()
        }

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

    override fun onLongClick(v: View?): Boolean {
        MessageDialog.Builder(activity)
            .setMessage(R.string.delete_favorite_msg)
            .setListener(object : MessageDialog.OnListener {
                override fun confirm(dialog: Dialog?) {
                    val favoriteMovie = v!!.tag as FavoriteMovie
                    val repository = RepositoryProvider.providerFavoriteMovieRepository()
                    val movie = Movie()
                    movie.href = favoriteMovie.href
                    repository.removeMovie(movie)
                }

                override fun cancel(dialog: Dialog?) {}

            }).show()
        return true
    }
}