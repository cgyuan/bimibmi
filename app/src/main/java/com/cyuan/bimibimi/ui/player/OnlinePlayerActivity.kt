package com.cyuan.bimibimi.ui.player

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.constant.PlayerKeys
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.model.Episode
import com.cyuan.bimibimi.parser.HtmlDataParser
import com.cyuan.bimibimi.parser.ParseResultCallback
import com.cyuan.bimibimi.ui.player.CustomController.OnstateChangeListener
import com.cyuan.bimibimi.ui.player.manager.PIPManager
import com.cyuan.bimibimi.ui.player.manager.WindowPermissionCheck
import com.dueeeke.videoplayer.player.AbstractPlayer
import com.dueeeke.videoplayer.player.IjkPlayer
import com.dueeeke.videoplayer.player.IjkVideoView
import com.dueeeke.videoplayer.player.PlayerConfig
import kotlinx.android.synthetic.main.activity_player_main.*
import zmovie.com.dlan.DlnaPresenter

class OnlinePlayerActivity : AppCompatActivity() {

    private var episodeIndex: Int = 0
    private lateinit var episodeList: ArrayList<Episode>
    private lateinit var episodeName: String
    private lateinit var movieTitle: String
    private lateinit var url: String
    private lateinit var dlnaPresenter: DlnaPresenter
    private lateinit var player: AbstractPlayer
    private lateinit var ijkVideoView: IjkVideoView
    private lateinit var mPIPManager: PIPManager
    private lateinit var controller: CustomController
    private val stateChangeListener = object: OnstateChangeListener {
        override fun onAirPlay() {
            dlnaPresenter.showDialogTip(this@OnlinePlayerActivity, url, "【${movieTitle}】$episodeName")
        }

        override fun onPic2Pic() {
            startFloatWindow()
        }

        override fun onLocalCast() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private val episodeItemClickListener = CustomController.OnItemClickedListener { position ->
        val episode = episodeList[position]
        HtmlDataParser.parseVideoSource(this@OnlinePlayerActivity, episode, object : ParseResultCallback<String> {
            override fun onSuccess(url: String) {
                this@OnlinePlayerActivity.url = url
                episodeName = episode.title
                ijkVideoView.stopPlayback()
                ijkVideoView.release()
                ijkVideoView.setUrl(url)
                ijkVideoView.title = "【${movieTitle}】$episodeName"
                controller.setTitle("【${movieTitle}】$episodeName")
                ijkVideoView.start()
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
        controller = CustomController(this)

        controller.setOnstateChangeListener(stateChangeListener)
        controller.setOnItemClickListener(episodeItemClickListener)

        mPIPManager = PIPManager.getInstance()
        ijkVideoView = mPIPManager.ijkVideoView

        ijkVideoView.setVideoController(controller)

        url = intent.getStringExtra(PlayerKeys.URL)!!
        movieTitle = intent.getStringExtra(PlayerKeys.MOVIE_TITLE)!!
        episodeName = intent.getStringExtra(PlayerKeys.EPISODE_NAME)!!
        episodeIndex = intent.getIntExtra(PlayerKeys.EPISODE_INDEX, 0)
        episodeList = intent.getParcelableArrayListExtra(PlayerKeys.EPISODE_LIST)!!

        if (episodeList.size > 1) {
            controller.configPlayList(episodeList, episodeIndex)
        }

        if (url.endsWith("m3u8")) {
            player = AndroidMediaPlayer(this)
        } else {
            player = IjkPlayer(this)
        }

        if (mPIPManager.isStartFloatWindow) {
            mPIPManager.stopFloatWindow()
            controller.setPlayerState(ijkVideoView.currentPlayerState)
            controller.setPlayState(ijkVideoView.currentPlayState)
        } else {
            ijkVideoView.setUrl(url)
            ijkVideoView.title = "【${movieTitle}】$episodeName"
            controller.setTitle("【${movieTitle}】$episodeName")
            val playerConfig = PlayerConfig.Builder()
                //启用边播边缓存功能
                //  .autoRotate() //启用重力感应自动进入/退出全屏功能
                //                .enableMediaCodec()//启动硬解码，启用后可能导致视频黑屏，音画不同步
                .savingProgress() //保存播放进度
                .disableAudioFocus() //关闭AudioFocusChange监听
                .setCustomMediaPlayer(player)
                .build()
            ijkVideoView.setPlayerConfig(playerConfig)
        }
        playView.addView(ijkVideoView)
        ijkVideoView.startFullScreen()
        ijkVideoView.start()

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
        mPIPManager.pause()
    }

    override fun onDestroy() {
        mPIPManager.reset()
        super.onDestroy()
    }

}