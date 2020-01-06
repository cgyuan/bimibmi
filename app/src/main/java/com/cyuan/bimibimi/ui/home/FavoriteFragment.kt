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
import com.cyuan.bimibimi.core.extension.dp2px
import com.cyuan.bimibimi.databinding.FragmentFavoriteBinding
import com.cyuan.bimibimi.db.repository.RepositoryProvider
import com.cyuan.bimibimi.model.FavoriteMovie
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.home.adapter.FavoriteMovieAdapter
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModel
import com.cyuan.bimibimi.ui.home.viewmodel.FavoriteMovieViewModelFactory
import com.cyuan.bimibimi.widget.GridDividerItemDecoration
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
        recyclerView.addItemDecoration(
            GridDividerItemDecoration(
                context!!,
                dp2px(15F),
                dp2px(15F),
                isNeedSpace = true,
                isLastRowNeedSpace = true,
                color = resources.getColor(R.color.window_background)
            )
        )

        viewModel.movies.observe(this, Observer {
            if (it != null) {
                adapter.submitList(it)
                //TODO 前面UI刷新会导致卡片变形，边距错乱，使用全局刷新以避免，待后续优化
                adapter.notifyDataSetChanged()
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