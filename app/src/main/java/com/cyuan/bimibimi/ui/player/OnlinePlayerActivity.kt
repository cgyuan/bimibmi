package com.cyuan.bimibimi.ui.player

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.Constants
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.AppDatabase
import com.cyuan.bimibimi.db.repository.HistoryRepository
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.model.History
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.cyuan.bimibimi.ui.player.manager.PIPManager
import com.cyuan.bimibimi.ui.player.manager.WindowPermissionCheck
import com.dueeeke.videoplayer.controller.MediaPlayerControl
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.AbstractPlayer
import com.dueeeke.videoplayer.player.AndroidMediaPlayerFactory
import com.dueeeke.videoplayer.player.PlayerFactory
import com.dueeeke.videoplayer.player.VideoView
import kotlinx.android.synthetic.main.activity_player_main.*
import zmovie.com.dlan.DlnaPresenter
import java.util.*

class OnlinePlayerActivity : AppCompatActivity() {

    private var dataSourceIndex: Int = 0
    private lateinit var movieCover: String
    private lateinit var movieDetailHref: String
    private var playPosition: Long = 0
    private var episodeIndex: Int = 0
    private var episodeList: ArrayList<Episode>? = null
    private lateinit var episodeName: String
    private lateinit var movieTitle: String
    private lateinit var currentUrl: String
    private lateinit var dlnaPresenter: DlnaPresenter
    private lateinit var playerFactory: PlayerFactory<out AbstractPlayer>
    private lateinit var mVideoView: VideoView<out AbstractPlayer>
    private lateinit var mPIPManager: PIPManager
    private lateinit var controller: CustomVideoController<out MediaPlayerControl>
    private val stateChangeListener = object: CustomVideoController.OnStateChangeListener {
        override fun onAirPlay() {
            dlnaPresenter.showDialogTip(this@OnlinePlayerActivity, currentUrl, "【${movieTitle}】$episodeName")
        }

        override fun onPic2Pic() {
            startFloatWindow()
        }

        override fun onLocalCast() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val episodeItemClickListener = CustomVideoController.OnItemClickedListener { position ->
        controller.showSingleBackBtn()
        playByEpisodeIndex(position)
    }

    fun playByEpisodeIndex(position: Int) {
        val episode = episodeList!![position]
        HtmlDataParser.parseVideoSource(
            this@OnlinePlayerActivity,
            episode,
            object : ParseResultCallback<String> {
                override fun onSuccess(url: String) {
                    this@OnlinePlayerActivity.currentUrl = url
                    episodeName = episode.title
                    mVideoView.stopPlayback()
                    mVideoView.release()

                    mVideoView.setUrl(url)
                    controller.setTitle("【${movieTitle}】$episodeName")
                    mVideoView.start()
                    controller.disableMirrorFlip()
                }

                override fun onFail(msg: String) {
                    Toast.makeText(this@OnlinePlayerActivity, msg, Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_player_main)
        controller = CustomVideoController(this)

        controller.setOnStateChangeListener(stateChangeListener)
        controller.setOnItemClickListener(episodeItemClickListener)

        mPIPManager = PIPManager.getInstance()
        mVideoView = mPIPManager.videoView

        mVideoView.setVideoController(controller)

        currentUrl = intent.getStringExtra(PlayerKeys.URL)!!
        movieTitle = intent.getStringExtra(PlayerKeys.MOVIE_TITLE)!!
        episodeName = intent.getStringExtra(PlayerKeys.EPISODE_NAME)!!
        dataSourceIndex = intent.getIntExtra(PlayerKeys.DATA_SOURCE_INDEX, 0)
        episodeIndex = intent.getIntExtra(PlayerKeys.EPISODE_INDEX, 0)
        episodeList = intent.getParcelableArrayListExtra(PlayerKeys.EPISODE_LIST)
        movieDetailHref = intent.getStringExtra(PlayerKeys.MOVIE_DETAIL_HREF)!!
        movieCover = intent.getStringExtra(PlayerKeys.MOVIE_COVER) ?: ""
        playPosition = intent.getLongExtra(PlayerKeys.PLAY_POSITION, 0)

        if (!episodeList.isNullOrEmpty()) {
            controller.configPlayList(episodeList, episodeIndex)
        }

        val player = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(Constants.SET_PLAYER, Constants.Player.EXO_PLAYER)

        when (player) {
            Constants.Player.MEDIA_PLAYER -> {
                playerFactory = AndroidMediaPlayerFactory.create()
            }
            Constants.Player.IJK_PLAYER -> {
                playerFactory = IjkPlayerFactory.create()
            }
            Constants.Player.EXO_PLAYER -> {
                playerFactory = ExoMediaPlayerFactory.create()
            }
        }

        if (mPIPManager.isStartFloatWindow) {
            mPIPManager.stopFloatWindow()
            controller.setPlayerState(mVideoView.currentPlayerState)
            controller.setPlayState(mVideoView.currentPlayState)
        } else {
            mVideoView.setUrl(currentUrl)
            controller.setTitle("【${movieTitle}】$episodeName")
        }
        playView.addView(mVideoView)
        mVideoView.setPlayerFactory(playerFactory)
        mVideoView.startFullScreen()
        mVideoView.start()
        mVideoView.seekTo(playPosition)
        controller.showSingleBackBtn()

        initDLNA()
    }

    private fun initDLNA() {
        dlnaPresenter = DlnaPresenter(App.getContext())
        dlnaPresenter.initService()
    }

    /**
     * 小窗播放
     */
    fun startFloatWindow() {
        if (WindowPermissionCheck.checkPermission(this)) {
            controller.stopFullScreen()
            mPIPManager.startFloatWindow()
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        mPIPManager.resume()
    }

    override fun onPause() {
        super.onPause()
        if (!controller.isAllowPlayBackground) {
            mPIPManager.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        val historyRepository = HistoryRepository
            .getInstance(AppDatabase.instance.historyDao())
        if (mVideoView.duration > 0) {
            val history = History(movieDetailHref, currentUrl,
                movieTitle, dataSourceIndex, episodeName, episodeIndex, mVideoView.currentPosition,
                mVideoView.duration, movieCover, "", Calendar.getInstance())
            historyRepository.saveHistory(history)
        }
    }

    override fun onDestroy() {
        mPIPManager.reset()
        dlnaPresenter.removeEventRegister()
        super.onDestroy()
    }

}