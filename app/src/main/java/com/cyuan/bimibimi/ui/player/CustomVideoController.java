package com.cyuan.bimibimi.ui.player;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyuan.bimibimi.R;
import com.cyuan.bimibimi.constant.Constants;
import com.cyuan.bimibimi.core.utils.ImageUtils;
import com.cyuan.bimibimi.core.utils.SharedUtil;
import com.cyuan.bimibimi.model.Episode;
import com.dueeeke.videocontroller.BatteryReceiver;
import com.dueeeke.videocontroller.CenterView;
import com.dueeeke.videocontroller.CutoutUtil;
import com.dueeeke.videocontroller.MarqueeTextView;
import com.dueeeke.videocontroller.StatusView;
import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.List;

import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;


public class CustomVideoController<T extends MediaPlayerControl> extends GestureVideoController<T>
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, GestureVideoController.GestureListener {
    private OnlinePlayerActivity mActivity;
    protected TextView mTotalTime, mCurrTime;
//    protected ImageView mFullScreenButton;
    protected RelativeLayout mBottomContainer;
    protected LinearLayout mTopContainer;
    protected SeekBar mVideoProgress;
    protected ImageView mBackButton;
    protected ImageView mLockButton;
    protected MarqueeTextView mTitle;
    private boolean mIsLive;
    private boolean mIsDragging;

    private SeekBar mBottomProgress;
    private ImageView mPlayButton;
    private ImageView mStartPlayButton;
    private RelativeLayout mLoadingContainer;
    private ImageView mThumb;
    private LinearLayout mCompleteContainer;
//    private ImageView mStopFullscreen;
    private TextView mSysTime;//系统当前时间
    private ImageView mBatteryLevel;//电量
    private Animation mShowAnim;
    private Animation mHideAnim;
    private BatteryReceiver mBatteryReceiver;

    protected StatusView mStatusView;
    protected CenterView mCenterView;

    /**
     * 是否需要适配刘海屏
     */
    protected boolean mNeedAdaptCutout;
    protected int mPadding;
    private int mCurrentOrientation = -1;

    private ImageView mLoadingProgress;
    private NetSpeedUtil mNetSpeedUtil;
    private TextView mCenterNetSpeedView;
    private TextView mTopNetSpeedView;

    private List<Episode> mPlayList;
    private TextView mChooseEpisodeBtn;
    private ImageView mSmallWindowBtn;
    private ImageView mDlnaCastBtn;
    private TextView mSpeedUpBtn;
    private boolean showChoseBtn = false;
    public static int sCurrentIndex = 0;

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                refreshNetSpeed();
                handler.sendEmptyMessageDelayed(0,1000);
            }

        }
    };
    private DialogLayer anyLayer;
    private RadioGroup mChooseSpeedPanel;
    private Animation mMenuPanelShowAnim;
    private Animation mMenuPanelHideAnim;
    private View mMenuPanel;
    private RadioGroup mScreenAspectGroup;
    private View mPlayBackgroundBtn;
    private View mMirrorFlipBtn;
    private boolean isAllowPlayBackground;
    private boolean isMirrorFlip;
    private RadioGroup mPlayBehaviorGroup;
    private View mScreenShotBtn;
    private AnimationDrawable mLoadingAnimDrawable;
    private View mSingleBackBtn;

    /**
     * 刷新网速
     */
    private void refreshNetSpeed(){
        if (mNetSpeedUtil==null){
            mNetSpeedUtil = new NetSpeedUtil();
        }
        if (mCenterNetSpeedView.isShown()){
            String netSpeeds = mNetSpeedUtil.getNetSpeed(getContext());
            mCenterNetSpeedView.setText(netSpeeds);
        }
        if (mTopNetSpeedView.isShown()){
            String netSpeeds = mNetSpeedUtil.getNetSpeed(getContext());
            mTopNetSpeedView.setText(netSpeeds);
        }
    }

    public void showSingleBackBtn() {
        mSingleBackBtn.setVisibility(VISIBLE);
    }


    public CustomVideoController(@NonNull Context context) {
        this(context, null);
        mActivity = (OnlinePlayerActivity) context;
    }

    public CustomVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_controller_cover;
    }

    @Override
    protected void initView() {
        super.initView();
//        mFullScreenButton = mControllerView.findViewById(R.id.fullscreen);
//        mFullScreenButton.setOnClickListener(this);
        mBottomContainer = mControllerView.findViewById(R.id.cover_player_controller_bottom_container);
        mTopContainer = mControllerView.findViewById(R.id.cover_player_controller_top_container);
        mVideoProgress = mControllerView.findViewById(R.id.cover_player_controller_seek_bar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = mControllerView.findViewById(R.id.cover_player_controller_text_view_total_time);
        mCurrTime = mControllerView.findViewById(R.id.cover_player_controller_text_view_curr_time);
        mBackButton = mControllerView.findViewById(R.id.cover_player_controller_image_view_back_icon);
        mBackButton.setOnClickListener(this);
        mLockButton = mControllerView.findViewById(R.id.video_lock);
        mLockButton.setOnClickListener(this);
        mThumb = mControllerView.findViewById(R.id.thumb);
        mThumb.setOnClickListener(this);
        mPlayButton = mControllerView.findViewById(R.id.cover_player_controller_image_view_play_state);
        mPlayButton.setOnClickListener(this);
        mStartPlayButton = mControllerView.findViewById(R.id.start_play);
        mLoadingContainer = mControllerView.findViewById(R.id.loading_content);
        mLoadingProgress = mControllerView.findViewById(R.id.play_loading);
        mSingleBackBtn = mControllerView.findViewById(R.id.single_back_btn);
        mSingleBackBtn.setOnClickListener(this);
        mLoadingAnimDrawable = (AnimationDrawable) mLoadingProgress.getDrawable();
        mCenterNetSpeedView = mControllerView.findViewById(R.id.net_speed);

        mTopNetSpeedView = mControllerView.findViewById(R.id.net_speed_tv);

        mBottomProgress = mControllerView.findViewById(R.id.cover_bottom_seek_bar);
        ImageView rePlayButton = mControllerView.findViewById(R.id.iv_replay);
        rePlayButton.setOnClickListener(this);
        mCompleteContainer = mControllerView.findViewById(R.id.complete_container);
        mCompleteContainer.setOnClickListener(this);
//        mStopFullscreen = mControllerView.findViewById(R.id.stop_fullscreen);
//        mStopFullscreen.setOnClickListener(this);
        mTitle = mControllerView.findViewById(R.id.cover_player_controller_text_view_video_title);
        mSysTime = mControllerView.findViewById(R.id.sys_time);
        mBatteryLevel = mControllerView.findViewById(R.id.iv_battery);


        mControllerView.findViewById(R.id.setting_more).setOnClickListener(this);
        mPlayBackgroundBtn = mControllerView.findViewById(R.id.play_background_btn);
        mPlayBackgroundBtn.setOnClickListener(this);
        mMirrorFlipBtn = mControllerView.findViewById(R.id.mirror_flip_btn);
        mMirrorFlipBtn.setOnClickListener(this);

        isAllowPlayBackground = SharedUtil.read(Constants.ALLOW_PLAY_IN_BACKGROUND, false);
        mPlayBackgroundBtn.setSelected(isAllowPlayBackground);

        mScreenShotBtn = mControllerView.findViewById(R.id.screen_shot_btn);
        mScreenShotBtn.setOnClickListener(this);


        mChooseEpisodeBtn =  mControllerView.findViewById(R.id.choose_list);
        mChooseEpisodeBtn.setOnClickListener(this);

        mSmallWindowBtn = mControllerView.findViewById(R.id.small_window);
        mSmallWindowBtn.setOnClickListener(this);

        mDlnaCastBtn = mControllerView.findViewById(R.id.dlna_cast);
        mDlnaCastBtn.setOnClickListener(this);

        mSpeedUpBtn = mControllerView.findViewById(R.id.speed_up);
        mSpeedUpBtn.setOnClickListener(this);

        mBatteryReceiver = new BatteryReceiver(mBatteryLevel);

        setGestureListener(this);

        mStatusView = new StatusView(getContext());

        mCenterView = new CenterView(getContext());
        mCenterView.setVisibility(GONE);
        addView(mCenterView);

        initChooseSpeedPanel();
        initMenuPanel();

        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);
        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
        mNetSpeedUtil = new NetSpeedUtil();
    }

    private void initChooseSpeedPanel() {
        mChooseSpeedPanel = mControllerView.findViewById(R.id.choose_speed_panel);
        mChooseSpeedPanel.check(R.id.normal);
        mChooseSpeedPanel.setOnCheckedChangeListener((group, checkedId) -> {
            float speed = Float.parseFloat((String) mChooseSpeedPanel.findViewById(checkedId).getTag());
            mMediaPlayer.setSpeed(speed);
        });
        mMenuPanelShowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_right_in);
        mMenuPanelHideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_right_out);
    }

    private void initMenuPanel() {
        mMenuPanel = mControllerView.findViewById(R.id.settings_menu);

        mScreenAspectGroup = mMenuPanel.findViewById(R.id.screen_aspect_group);
        mScreenAspectGroup.check(R.id.screen_default);

        mScreenAspectGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int scaleType = Integer.parseInt((String) mScreenAspectGroup.findViewById(checkedId).getTag());
            mMediaPlayer.setScreenScaleType(scaleType);
        });

        mPlayBehaviorGroup = mMenuPanel.findViewById(R.id.play_behavior);

        int behavior = SharedUtil.read(Constants.PALY_BEHAVIOR, Constants.PlayBehavior.PAUSE);
        ((RadioButton)mPlayBehaviorGroup.getChildAt(behavior)).setChecked(true);

        mPlayBehaviorGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int playBehavior = Integer.parseInt((String) mPlayBehaviorGroup.findViewById(checkedId).getTag());
            SharedUtil.save(Constants.PALY_BEHAVIOR, playBehavior);
        });
    }

    private void showMenuPanel() {
        mMenuPanel.setVisibility(View.VISIBLE);
        mMenuPanel.startAnimation(mMenuPanelShowAnim);
    }

    private void hideMenuPanel() {
        mMenuPanel.setVisibility(View.GONE);
        mMenuPanel.startAnimation(mMenuPanelHideAnim);
    }

    private void showChooseSpeedPanel() {
        mChooseSpeedPanel.setVisibility(View.VISIBLE);
        mChooseSpeedPanel.startAnimation(mMenuPanelShowAnim);
    }

    private void hideChooseSpeedPanel() {
        mChooseSpeedPanel.setVisibility(View.GONE);
        mChooseSpeedPanel.startAnimation(mMenuPanelHideAnim);
    }

    public void configPlayList(List<Episode> playList, int currentIndex) {
        if (playList.size() > 1) {
            sCurrentIndex = currentIndex;
            this.showChoseBtn = true;
            this.mPlayList = playList;
            mChooseEpisodeBtn.setVisibility(VISIBLE);
            invalidate();
        }
    }

    public void setNeedSpeedVisible(boolean b) {
        int visibility = b ? VISIBLE : INVISIBLE;
        mTopNetSpeedView.setVisibility(visibility);
        mCenterNetSpeedView.setVisibility(visibility);
    }

    public boolean isAllowPlayBackground() {
        return isAllowPlayBackground;
    }

    public void disableMirrorFlip() {
        if (isMirrorFlip) {
            isMirrorFlip = false;
            mMirrorFlipBtn.setSelected(false);
            mMediaPlayer.setMirrorRotation(false);
        }
    }

    public void stopFullScreen() {
        stopFullScreenFromUser();
    }

    public interface OnItemClickedListener {
        void clicked(int position);
    }

    private OnItemClickedListener clickedListener;

    public void setOnItemClickListener(OnItemClickedListener clickedListener) {
        this.clickedListener = clickedListener;
    }


    /**
     * 显示选集对话框
     */
    private void showChooseList() {
        if (anyLayer == null) {
            anyLayer = AnyLayer.dialog(getContext())
                    .contentView(R.layout.play_list_layout)
                    .gravity(Gravity.RIGHT)
                    .contentAnimator(new Layer.AnimatorCreator() {
                        @Override
                        public Animator createInAnimator(View target) {
                            return AnimatorHelper.createRightInAnim(target);
                        }

                        @Override
                        public Animator createOutAnimator(View target) {
                            return AnimatorHelper.createRightOutAnim(target);
                        }
                    });
        }
        anyLayer.show();
        RecyclerView playList = anyLayer.getView(R.id.play_list);
        EpisodeAdapter adapter = new EpisodeAdapter(mPlayList, anyLayer, clickedListener);
        playList.setLayoutManager(new GridLayoutManager(getContext(), 6));
        playList.setAdapter(adapter);

        anyLayer.getView(R.id.chose_root).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && anyLayer != null && anyLayer.isShow()) {
                    float y = event.getRawY();
                    int[] location = new int[2];
                    View view =  anyLayer.getView(R.id.play_list);
                    view.getLocationInWindow(location);
                    if (y>location[1]+view.getHeight()) {
                        anyLayer.dismiss();
                    }
                    return true;
                }
                return true;
            }
        });
    }

    @Override
    public void setMediaPlayer(T mediaPlayer) {
        super.setMediaPlayer(mediaPlayer);
        mStatusView.attachMediaPlayer(mMediaPlayer);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mBatteryReceiver);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        checkCutout();
    }

    /**
     * 检查是否需要适配刘海
     */
    private void checkCutout() {
        mNeedAdaptCutout = CutoutUtil.allowDisplayToCutout(getContext());
        if (mNeedAdaptCutout) {
            mPadding = (int) PlayerUtils.getStatusBarHeight(getContext());
        }
        L.d("needAdaptCutout: " + mNeedAdaptCutout + " padding: " + mPadding);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustView();
    }

    @Override
    public void onOrientationChanged(int orientation) {
        super.onOrientationChanged(orientation);
        adjustView();
    }

    private void adjustView() {
        if (mNeedAdaptCutout) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity == null) {
                return;
            }
            int o = activity.getRequestedOrientation();
            if (o == mCurrentOrientation) {
                return;
            }
            L.d("adjustView");
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                adjustPortrait();
            } else if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                adjustLandscape();
            } else if (o == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                adjustReserveLandscape();
            }
            mCurrentOrientation = o;
        }
    }

    protected void adjustPortrait() {
        mTopContainer.setPadding(0, 0, 0, 0);
        mBottomContainer.setPadding(0, 0, 0, 0);
        mBottomProgress.setPadding(0, 0, 0, 0);
        LayoutParams lblp = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        lblp.setMargins(dp24, 0, dp24, 0);
//        LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
//        sflp.setMargins(0, 0, 0, 0);
    }

    protected void adjustLandscape() {
        mTopContainer.setPadding(mPadding, 0, 0, 0);
        mBottomContainer.setPadding(mPadding, 0, 0, 0);
        mBottomProgress.setPadding(mPadding, 0, 0, 0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        layoutParams.setMargins(dp24 + mPadding, 0, dp24 + mPadding, 0);
//        LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
//        sflp.setMargins(mPadding, 0, 0, 0);
    }

    protected void adjustReserveLandscape() {
        mTopContainer.setPadding(0, 0, mPadding, 0);
        mBottomContainer.setPadding(0, 0, mPadding, 0);
        mBottomProgress.setPadding(0, 0, mPadding, 0);
        LayoutParams layoutParams = (LayoutParams) mLockButton.getLayoutParams();
        int dp24 = PlayerUtils.dp2px(getContext(), 24);
        layoutParams.setMargins(dp24, 0, dp24, 0);
//        LayoutParams sflp = (LayoutParams) mStopFullscreen.getLayoutParams();
//        sflp.setMargins(0, 0, 0, 0);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fullscreen || i == R.id.cover_player_controller_image_view_back_icon || i == R.id.stop_fullscreen
            || i == R.id.single_back_btn) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity != null) {
                activity.finish();
            }
//            doStartStopFullScreen();
        } else if (i == R.id.video_lock) {
            doLockUnlock();
        } else if (i == R.id.cover_player_controller_image_view_play_state || i == R.id.thumb) {
            doPauseResume();
        } else if (i == R.id.iv_replay || i == R.id.iv_refresh) {
            mMediaPlayer.replay(true);
        } else if (i == R.id.choose_list) {
            showChooseList();
        } else if (i == R.id.small_window) {
            if (mStateListener != null) {
                mStateListener.onPic2Pic();
            }
        } else if (i == R.id.dlna_cast) {
            if (mStateListener != null) {
                mStateListener.onAirPlay();
            }
        } else if (i == R.id.speed_up) {
            showChooseSpeedPanel();
            hide();
        } else if (i == R.id.setting_more) {
            showMenuPanel();
            hide();
        } else if (i == R.id.play_background_btn) {
            isAllowPlayBackground = !isAllowPlayBackground;
            SharedUtil.save(Constants.ALLOW_PLAY_IN_BACKGROUND, isAllowPlayBackground);
            mPlayBackgroundBtn.setSelected(isAllowPlayBackground);
        } else if (i == R.id.mirror_flip_btn) {
            isMirrorFlip = !isMirrorFlip;
            mMediaPlayer.setMirrorRotation(isMirrorFlip);
            mMirrorFlipBtn.setSelected(isMirrorFlip);
        } else if (i == R.id.screen_shot_btn) {
            Bitmap bitmap = mMediaPlayer.doScreenShot();
            String path = ImageUtils.INSTANCE.saveBitmap(bitmap);
            if (!TextUtils.isEmpty(path)) {
                ImageUtils.INSTANCE.insertImageToSystem(getContext(), path);
                Toast.makeText(mActivity, "截图已保存到" + path, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                L.e("PLAYER_NORMAL");
                if (mIsLocked) return;
                if (mNeedAdaptCutout) {
                    CutoutUtil.adaptCutoutAboveAndroidP(getContext(), false);
                }
                setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mIsGestureEnabled = false;
//                mFullScreenButton.setSelected(false);
                mBackButton.setVisibility(GONE);
                mLockButton.setVisibility(GONE);
                mScreenShotBtn.setVisibility(GONE);
                mTitle.setVisibility(INVISIBLE);
                mTitle.setNeedFocus(false);
                mSysTime.setVisibility(GONE);
                mBatteryLevel.setVisibility(GONE);
                mTopContainer.setVisibility(GONE);
//                mStopFullscreen.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                L.e("PLAYER_FULL_SCREEN");
                if (mIsLocked) return;
                if (mNeedAdaptCutout) {
                    CutoutUtil.adaptCutoutAboveAndroidP(getContext(), true);
                }
                mIsGestureEnabled = true;
//                mFullScreenButton.setSelected(true);
                mBackButton.setVisibility(VISIBLE);
                mTitle.setVisibility(VISIBLE);
                mTitle.setNeedFocus(true);
                mSysTime.setVisibility(VISIBLE);
                mBatteryLevel.setVisibility(VISIBLE);
//                mStopFullscreen.setVisibility(VISIBLE);
                if (mShowing) {
                    mLockButton.setVisibility(VISIBLE);
                    mTopContainer.setVisibility(VISIBLE);
                    mScreenShotBtn.setVisibility(VISIBLE);
                } else {
                    mLockButton.setVisibility(GONE);
                    mScreenShotBtn.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                hide();
                mIsLocked = false;
                mLockButton.setSelected(false);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                mCompleteContainer.setVisibility(GONE);
                mBottomProgress.setVisibility(GONE);
                mLoadingContainer.setVisibility(GONE);
                mLoadingContainer.setBackgroundResource(R.drawable.preview_bg);
                mLoadingAnimDrawable.stop();
                mStatusView.dismiss();
                mStartPlayButton.setVisibility(VISIBLE);
                mThumb.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                //开始刷新进度
                post(mShowProgress);
                mPlayButton.setSelected(true);
                mLoadingContainer.setVisibility(GONE);
                mCompleteContainer.setVisibility(GONE);
                mLoadingContainer.setBackground(null);
                mSingleBackBtn.setVisibility(GONE);
                mLoadingAnimDrawable.stop();
                mThumb.setVisibility(GONE);
                mStartPlayButton.setVisibility(GONE);
                break;
            case VideoView.STATE_PAUSED:
                L.e("STATE_PAUSED");
                mPlayButton.setSelected(false);
                mStartPlayButton.setVisibility(GONE);
                //removeCallbacks(mShowProgress);
                break;
            case VideoView.STATE_PREPARING:
                L.e("STATE_PREPARING");
                mCompleteContainer.setVisibility(GONE);
                mStartPlayButton.setVisibility(GONE);
                mStatusView.dismiss();
                showLoadingContent();
                mLoadingContainer.setVisibility(VISIBLE);
//                mThumb.setVisibility(VISIBLE);
                mLoadingAnimDrawable.start();
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                if (!mIsLive) mBottomProgress.setVisibility(VISIBLE);
//                mLoadingProgress.setVisibility(GONE);
                mStartPlayButton.setVisibility(GONE);
                show(8000);
                break;
            case VideoView.STATE_ERROR:
                L.e("STATE_ERROR");
                removeCallbacks(mFadeOut);
                hide();
                mStatusView.showErrorView(this);
                removeCallbacks(mShowProgress);
                mStartPlayButton.setVisibility(GONE);
                mLoadingContainer.setVisibility(GONE);
                mLoadingAnimDrawable.stop();
                mThumb.setVisibility(GONE);
                mBottomProgress.setVisibility(GONE);
                mTopContainer.setVisibility(GONE);
                break;
            case VideoView.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                mStartPlayButton.setVisibility(GONE);
                mLoadingContainer.setVisibility(VISIBLE);
                mLoadingAnimDrawable.start();
                mThumb.setVisibility(GONE);
                showLoadingContent();
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case VideoView.STATE_BUFFERED:
                L.e("STATE_BUFFERED");
                mLoadingContainer.setVisibility(GONE);
                mStartPlayButton.setVisibility(GONE);
                mLoadingContainer.setBackground(null);
                mSingleBackBtn.setVisibility(GONE);
                mLoadingAnimDrawable.stop();
                mThumb.setVisibility(GONE);
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                int behavior = SharedUtil.read(Constants.PALY_BEHAVIOR, Constants.PlayBehavior.PAUSE);
                if (behavior == Constants.PlayBehavior.PAUSE) {
                    pauseAndShowReplay();
                } else if (behavior == Constants.PlayBehavior.PLAY_NEXT ||
                        behavior == Constants.PlayBehavior.LIST_CYCLE) {
                    sCurrentIndex += 1;
                    if (mPlayList == null || sCurrentIndex >= mPlayList.size()) {
                        if (behavior == Constants.PlayBehavior.LIST_CYCLE) {
                            sCurrentIndex = 0;
                            mActivity.playByEpisodeIndex(sCurrentIndex);
                        } else {
                            sCurrentIndex--;
                            pauseAndShowReplay();
                        }
                    } else {
                        mActivity.playByEpisodeIndex(sCurrentIndex);
                    }
                } else if (behavior == Constants.PlayBehavior.SINGLE_CYCLE) {
                    mMediaPlayer.replay(true);
                }

                mIsLocked = false;
                break;
        }
    }

    private void pauseAndShowReplay() {
        hide();
        removeCallbacks(mShowProgress);
        mStartPlayButton.setVisibility(GONE);
        mThumb.setVisibility(VISIBLE);
        mCompleteContainer.setVisibility(VISIBLE);
//                mStopFullscreen.setVisibility(mMediaPlayer.isFullScreen() ? VISIBLE : GONE);
        mBottomProgress.setVisibility(GONE);
        mBottomProgress.setProgress(0);
        mBottomProgress.setSecondaryProgress(0);
        mLoadingContainer.setVisibility(GONE);
        mLoadingAnimDrawable.stop();
    }

    /**
     * 显示loading
     */
    private void showLoadingContent() {
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingAnimDrawable.start();
        //开始获取网速
        refreshNetSpeed();
        handler.sendEmptyMessage(0);
    }

    /**
     * 显示移动网络播放警告
     */
    @Override
    public boolean showNetWarning() {
        //现在是按父类的逻辑显示移动网络播放警告
        if (super.showNetWarning()) {
            mStatusView.showNetWarning(this);
        }
        return super.showNetWarning();
    }

    @Override
    public void hideNetWarning() {
        mStatusView.dismiss();
    }

    protected void doLockUnlock() {
        if (mIsLocked) {
            mIsLocked = false;
            mShowing = false;
            mIsGestureEnabled = true;
            show();
            mLockButton.setSelected(false);
            Toast.makeText(getContext(), R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show();
        } else {
            hide();
            mIsLocked = true;
            mIsGestureEnabled = false;
            mLockButton.setSelected(true);
            Toast.makeText(getContext(), R.string.dkplayer_locked, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置是否为直播视频
     */
    public void setLive() {
        mIsLive = true;
        mBottomProgress.setVisibility(GONE);
        mVideoProgress.setVisibility(INVISIBLE);
        mTotalTime.setVisibility(INVISIBLE);
        mCurrTime.setVisibility(INVISIBLE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mMediaPlayer.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
        mMediaPlayer.seekTo((int) newPosition);
        mIsDragging = false;
        post(mShowProgress);
        show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mMediaPlayer.getDuration();
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime((int) newPosition));
    }

    @Override
    public void hide() {
        if (mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                mLockButton.setVisibility(GONE);
                mLockButton.setAnimation(mHideAnim);
                mScreenShotBtn.setVisibility(GONE);
                mScreenShotBtn.setAnimation(mHideAnim);
                if (!mIsLocked) {
                    hideAllViews();
                }
            } else {
                mBottomContainer.setVisibility(GONE);
                mBottomContainer.startAnimation(mHideAnim);
            }
            if (!mIsLive && !mIsLocked) {
                mBottomProgress.setVisibility(VISIBLE);
                mBottomProgress.startAnimation(mShowAnim);
            }
            mShowing = false;
        }
    }

    private void hideAllViews() {
        mTopContainer.setVisibility(GONE);
        mTopContainer.startAnimation(mHideAnim);
        mBottomContainer.setVisibility(GONE);
        mBottomContainer.startAnimation(mHideAnim);
        mScreenShotBtn.setVisibility(GONE);
    }

    private void show(int timeout) {
        if (mSysTime != null)
            mSysTime.setText(getCurrentSystemTime());
        if (!mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                if (mLockButton.getVisibility() != VISIBLE) {
                    mLockButton.setVisibility(VISIBLE);
                    mLockButton.setAnimation(mShowAnim);
                    mScreenShotBtn.setVisibility(VISIBLE);
                    mScreenShotBtn.setAnimation(mShowAnim);
                }
                if (!mIsLocked) {
                    showAllViews();
                }
            } else {
                mBottomContainer.setVisibility(VISIBLE);
                mBottomContainer.startAnimation(mShowAnim);
            }
            if (!mIsLocked && !mIsLive) {
                mBottomProgress.setVisibility(GONE);
                mBottomProgress.startAnimation(mHideAnim);
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    private void showAllViews() {
        mBottomContainer.setVisibility(VISIBLE);
        mBottomContainer.startAnimation(mShowAnim);
        mTopContainer.setVisibility(VISIBLE);
        mTopContainer.startAnimation(mShowAnim);
    }

    @Override
    public void show() {
        if (mChooseSpeedPanel.isShown()) {
            hideChooseSpeedPanel();
            return;
        }
        if (mMenuPanel.isShown()) {
            hideMenuPanel();
            return;
        }
        show(mDefaultTimeout);
    }

    @Override
    protected int setProgress() {
        if (mMediaPlayer == null || mIsDragging) {
            return 0;
        }

        if (mIsLive) return 0;

        int position = (int) mMediaPlayer.getCurrentPosition();
        int duration = (int) mMediaPlayer.getDuration();
        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mMediaPlayer.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTotalTime != null)
            mTotalTime.setText(stringForTime(duration));
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime(position));

        return position;
    }


    @Override
    protected void slideToChangePosition(float deltaX) {
        if (mIsLive) {
            mNeedSeek = false;
        } else {
            super.slideToChangePosition(deltaX);
        }
    }

    public ImageView getThumb() {
        return mThumb;
    }

    @Override
    public boolean onBackPressed() {
        if (mIsLocked) {
            show();
            Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return super.onBackPressed();

        if (mMediaPlayer.isFullScreen()) {
            stopFullScreenFromUser();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onPositionChange(int slidePosition, int currentPosition, int duration) {
        mCenterView.setProVisibility(View.GONE);
        if (slidePosition > currentPosition) {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_fast_forward);
        } else {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_fast_rewind);
        }
        mCenterView.setTextView(stringForTime(slidePosition) + "/" + stringForTime(duration));
    }

    @Override
    public void onBrightnessChange(int percent) {
        mCenterView.setProVisibility(View.VISIBLE);
        mCenterView.setIcon(R.drawable.dkplayer_ic_action_brightness);
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
    }

    @Override
    public void onVolumeChange(int percent) {
        mCenterView.setProVisibility(View.VISIBLE);
        if (percent <= 0) {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_volume_off);
        } else {
            mCenterView.setIcon(R.drawable.dkplayer_ic_action_volume_up);
        }
        mCenterView.setTextView(percent + "%");
        mCenterView.setProPercent(percent);
    }

    @Override
    public void onStartSlide() {
        hide();
        mCenterView.setVisibility(VISIBLE);
    }

    @Override
    public void onStopSlide() {
        if (mCenterView.getVisibility() == VISIBLE) {
            mCenterView.setVisibility(GONE);
        }
    }


    public interface OnStateChangeListener {
        void onAirPlay();

        void onPic2Pic();

        void onLocalCast();
    }

    private OnStateChangeListener mStateListener;

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.mStateListener = listener;
    }
}
