package com.cyuan.bimibimi.ui.detail

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.utils.ColorHelper.colorBurn
import com.cyuan.bimibimi.core.utils.GlideRoundTransform
import com.cyuan.bimibimi.core.utils.ShimmerUtils
import com.cyuan.bimibimi.databinding.ActivityMovieDetailBinding
import com.cyuan.bimibimi.db.repository.FavoriteMovieRepository
import com.cyuan.bimibimi.db.repository.RepositoryProvider
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.ui.base.UICallback
import com.cyuan.bimibimi.ui.base.bindEmptyViewCallback
import com.cyuan.bimibimi.ui.detail.adapter.RecommendMovieAdapter
import com.cyuan.bimibimi.ui.download.DownloadActivity
import com.cyuan.bimibimi.widget.FocusLayoutManager
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.content_online_detail_page.*


class MovieDetailActivity : AppCompatActivity(), UICallback {

    private lateinit var url: String
    private lateinit var favoriteMovieRepository: FavoriteMovieRepository
    private lateinit var movie: Movie

    private val viewModel by viewModels<MovieDetailViewModel> {
        MovieDetailViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMovieDetailBinding>(
            this,
            R.layout.activity_movie_detail
        )
        bindEmptyViewCallback(this)
        emptyView.bind(movieDetail)
        movie = intent.getParcelableExtra(PlayerKeys.MOVIE)!!
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel

        detail_veilLayout_body.shimmer = ShimmerUtils.getGrayShimmer(this)

        root.setBackgroundColor(Color.rgb(110, 110, 100))
        mToolbar.setBackgroundColor(Color.rgb(110, 110, 100))
        setSupportActionBar(mToolbar)

        initThemeColor()

        url = Constants.BIMIBIMI_INDEX + movie.href

        viewModel.fetchMovieDetail(url)

        viewModel.movieDetail.observe(this, Observer { movieDetail ->
            val requestOptions = RequestOptions()
                .transform(GlideRoundTransform(this@MovieDetailActivity, 4))
            movieDetail.href = movie.href
            movieDetail.title = movie.title

            mvTitle.text = movieDetail.title
            headDesc.text = movieDetail.headers
            toolbarTitle.text = movieDetail.title

            //加入圆角变换
            Glide.with(this@MovieDetailActivity)
                .load(movieDetail.cover)
                .placeholder(R.drawable.ic_default_grey)
                .apply(requestOptions)
                .into(toolbarIcon)

            descView.setContent(movieDetail.intro)

            recommendListRv.layoutManager = FocusLayoutManager()
            recommendListRv.adapter = RecommendMovieAdapter(
                this@MovieDetailActivity,
                movieDetail.recommendList
            )

            viewContainer.removeView(recommendListVeil)
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                detail_veilLayout_body.veil()
            } else {
                detail_veilLayout_body.unVeil()
            }
        })

        scroll_content.scrollTo(0, 0)
        scroll_content.postDelayed({
            scrollToTop()
        }, 5)
    }

    private fun scrollToTop() {
        if (scroll_content.scrollY != 0) {
            scroll_content.postDelayed({
                scroll_content.scrollTo(0, 0)
            }, 5)
        }
    }

    override fun reload() {
        viewModel.fetchMovieDetail(url)
    }

    private fun initThemeColor() {
        Glide.with(this).asBitmap().load(movie.cover).into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                getBmpThemeColor(resource)
            }
        })

        scroll_content.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY >= 300) {
                if (!toolbarIcon.isShown) {
                    toolbarIcon.visibility = View.VISIBLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        app_bar.elevation = 8f
                    }
                    val animation = AnimationUtils.loadAnimation(this@MovieDetailActivity, R.anim.anim_in)
                    toolbarIcon.startAnimation(animation)
                }
            } else {
                if (toolbarIcon.isShown) {
                    val animation = AnimationUtils.loadAnimation(this@MovieDetailActivity, R.anim.anim_out)
                    toolbarIcon.startAnimation(animation)
                    toolbarIcon.visibility = View.INVISIBLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        app_bar.elevation = 0f
                    }
                }
            }
        }
    }

    private fun getBmpThemeColor(bitmap: Bitmap) {
        // Palette
        val builder = Palette.from(bitmap)
        builder.generate { it ->
            // 获取到充满活力的这种色调
            val vibrant = it!!.mutedSwatch
            //根据调色板Palette获取到图片中的颜色设置到toolbar和tab中背景，标题等，使整个UI界面颜色统一
            vibrant?.let {
                val colorBurn = colorBurn(vibrant.rgb)
                val colorAnim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ValueAnimator.ofArgb(Color.rgb(110, 110, 100), colorBurn)
                } else {
                    val anim = ValueAnimator()
                    anim.setIntValues(Color.rgb(110, 110, 100), colorBurn)
                    anim.setEvaluator(ArgbEvaluator())
                    anim
                }
                colorAnim.addUpdateListener {
                    root.setBackgroundColor(it.animatedValue as Int)
                    mToolbar.setBackgroundColor(it.animatedValue as Int)
                }
                colorAnim.duration = 300
                colorAnim.repeatMode = ValueAnimator.RESTART
                colorAnim.start()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.statusBarColor = colorBurn(vibrant.rgb)
                    window.navigationBarColor = colorBurn(vibrant.rgb)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cat_topappbar_menu, menu)
        val item = menu!!.findItem(R.id.favorite)
        favoriteMovieRepository = RepositoryProvider.providerFavoriteMovieRepository()
        val isFavorMovie = favoriteMovieRepository.isFavorite(movie)
        item.setIcon(if (isFavorMovie) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_border_black_24dp)
        if (isFavorMovie) {
            setFavoriteIconTint(item, R.color.red)
        } else {
            setFavoriteIconTint(item, R.color.white)
        }
        return true
    }

    private fun setFavoriteIconTint(item: MenuItem, @ColorRes colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            item.icon.setTint(resources.getColor(colorId))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.favorite) {
            toggleFavor(item)
        } else if (item.itemId == R.id.download) {
            startActivity(Intent(this, DownloadActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleFavor(item: MenuItem) {
        val isFavorMovie = favoriteMovieRepository.isFavorite(movie)
        if (isFavorMovie) {
            favoriteMovieRepository.removeMovie(movie)
            item.setIcon(R.drawable.ic_favorite_border_black_24dp)
            setFavoriteIconTint(item, R.color.white)
            Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show()
        } else {
            favoriteMovieRepository.addMovie(movie)
            item.setIcon(R.drawable.ic_favorite_black_24dp)
            setFavoriteIconTint(item, R.color.red)
            Toast.makeText(this, "已添加收藏", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        webview.destroy()
        super.onDestroy()
    }

}