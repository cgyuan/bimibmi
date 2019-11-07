package com.cyuan.bimibimi.ui.player

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cyuan.bimibimi.R
import com.cyuan.bimibimi.core.App
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

    private lateinit var url: String
    private lateinit var dlnaPresenter: DlnaPresenter
    private lateinit var player: AbstractPlayer
    private lateinit var ijkVideoView: IjkVideoView
    private lateinit var mPIPManager: PIPManager
    private lateinit var controller: CustomController
    private val stateChangeListener = object: OnstateChangeListener {
        override fun onAirPlay() {
            dlnaPresenter.showDialogTip(this@OnlinePlayerActivity, url, "hello")
        }

        override fun onPic2Pic() {
            startFloatWindow()
        }

        override fun onLocalCast() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

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

        mPIPManager = PIPManager.getInstance()
        ijkVideoView = mPIPManager.ijkVideoView

        ijkVideoView.setVideoController(controller)

        url = intent.getStringExtra("url")

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
            ijkVideoView.title = "hello"
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