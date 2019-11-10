package com.cyuan.bimibimi.ui.home

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.core.utils.ColorHelper.colorBurn
import com.cyuan.bimibimi.core.utils.GlideRoundTransform
import com.cyuan.bimibimi.extension.logWarn
import com.cyuan.bimibimi.model.Movie
import com.cyuan.bimibimi.model.MovieDetail
import com.cyuan.bimibimi.network.Callback
import com.cyuan.bimibimi.network.StringRequest
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.content_online_detail_page.*
import kotlinx.android.synthetic.main.play_all_list_layout.*


class MovieDetailActivity : AppCompatActivity() {

    private lateinit var bottomSheetAdapter: OnlinePlayListAdapter
    private lateinit var movieDetail: MovieDetail
    private lateinit var bottomSheetPlayList: RecyclerView
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        movie = intent.getParcelableExtra("movie")!!
        root.setBackgroundColor(Color.rgb(110, 110, 100))
        toolbar.setBackgroundColor(Color.rgb(110, 110, 100))
        setSupportActionBar(toolbar)

        backIcon.setOnClickListener {
            finish()
        }

        val bottomSheetDialog = BottomSheetDialog(this)
        val contentView = View.inflate(this, R.layout.play_all_list_layout, null)
        bottomSheetDialog.setContentView(contentView)
        val closeBtn = contentView.findViewById<ImageView>(R.id.close)
        bottomSheetPlayList = contentView.findViewById(R.id.playListRv)
        bottomSheetPlayList.layoutManager = GridLayoutManager(this@MovieDetailActivity, 4, GridLayoutManager.VERTICAL, false)

        bottomSheetAdapter = OnlinePlayListAdapter(
            this@MovieDetailActivity,
            mutableListOf(),
            R.drawable.bottom_sheet_play_bt_shape
        )
        bottomSheetPlayList.adapter = bottomSheetAdapter
        closeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        initThemeColor()

        StringRequest().url(Constants.BIMIBIMI_INDEX + movie.href).listen(object : Callback {
            override fun onFailure(e: Exception) {
                logWarn("load movie detail fail")
            }

            override fun onResponseString(response: String) {
                movieDetail = HtmlDataParser.parseMovieDetail(response)

                val requestOptions = RequestOptions()
                    .transform(GlideRoundTransform(this@MovieDetailActivity, 4))

                movieDetail.title = movie.title

                mvTitle.text = movieDetail.title
                headDesc.text = movieDetail.headers
                toolbarTitle.text = movieDetail.title

                //加入圆角变换
                Glide.with(this@MovieDetailActivity)
                    .load(movieDetail.cover)
                    .apply(requestOptions)
                    .into(toolbarIcon)

                Glide.with(this@MovieDetailActivity).load(movieDetail.cover).into(detailPoster)

                descView.setContent(movieDetail.intro)

                for (i in 0 until movieDetail.dataSources.size) {
//                    dataSourceContainer
                    val view = LayoutInflater.from(this@MovieDetailActivity).inflate(R.layout.online_detail_data_source_hold_layout, dataSourceContainer, false)
                    val dataSourceLabel = view.findViewById<TextView>(R.id.dataSourceLabel)
                    val btnViewALl = view.findViewById<TextView>(R.id.viewAll)
                    val episodesRV = view.findViewById<RecyclerView>(R.id.episodesRecyclerView)

                    dataSourceLabel.text = getString(R.string.data_source_label, movieDetail.dataSources[i].name)
                    episodesRV.layoutManager =
                        LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
                    episodesRV.adapter = OnlinePlayListAdapter(this@MovieDetailActivity, movieDetail.dataSources[i].episodes)

                    btnViewALl.setOnClickListener {
                        bottomSheetDialog.show()
                        bottomSheetAdapter.refreshData(movieDetail.dataSources[i].episodes)
                    }
                    dataSourceContainer.addView(view)
                }
            }
        })
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
            vibrant!!.let {
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
                    toolbar.setBackgroundColor(it.animatedValue as Int)
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
        return true
    }
}