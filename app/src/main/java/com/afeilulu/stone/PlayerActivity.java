package com.afeilulu.stone;

/**
 * Created by chen on 2/21/14.
 */

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afeilulu.stone.database.HistoryDao;
import com.afeilulu.stone.model.Episode;
import com.afeilulu.stone.model.LocalVideoInfo;
import com.afeilulu.stone.model.PlayHistory;
import com.afeilulu.stone.model.Source;
import com.afeilulu.stone.util.SourceHolder;
import com.afeilulu.stone.util.StreamProxy;
import com.afeilulu.stone.widget.TappableSurfaceView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.util.ArrayList;
import java.util.Collection;

import static com.afeilulu.stone.util.LogUtil.LOGD;
import static com.afeilulu.stone.util.LogUtil.LOGE;

public class PlayerActivity extends Activity implements
        OnCompletionListener,
        OnPreparedListener,
        OnErrorListener,
        OnSeekCompleteListener,
        OnVideoSizeChangedListener,
        SurfaceHolder.Callback {

    private AQuery aq;
    private final static int mForwardStep = 20;
    private final static int mForwardWaitingTime = 5000;
    private static String TAG = "PlayerActivity";
    private Thread.UncaughtExceptionHandler onBlooey =
            new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e(TAG, "0:Uncaught exception", ex);
                    goBlooey(ex);
                }
            };

    private boolean hasActiveHolder;
    private int width = 0;
    private int height = 0;
    private MediaPlayer player;
    private TappableSurfaceView surface;
    private SurfaceHolder holder;
    private TextView mProgresssTextView = null;
    private TextView mSpeedTextView = null;
    private TextView mDurationTextView = null;

    private boolean mIsPrepared;
    private boolean mPlayerFromStart;
    private TappableSurfaceView.TapListener onTap =
            new TappableSurfaceView.TapListener() {
                public void onTap(MotionEvent event) {
//                    if (event.getY() < surface.getHeight() / 2) {
//                        topPanel.setVisibility(View.VISIBLE);
//                    } else {
//                        bottomPanel.setVisibility(View.VISIBLE);
//                    }
                }
            };
    private long mForwardActionStartTime;
    private boolean mForwardFlag = false;
    //    private int m3u8LastSeekPosition;
    private boolean mIsJumpToUrlFinished = false;
    private ArrayList<LocalVideoInfo> mLocalVideos;
    private boolean isPaused = false;
    private SeekBar timeline = null;
    private ImageView media = null;
    private TextView whereyougo = null;
    private TextView mProgramName = null;
    private ProgressBar mWaiting = null;
    private StreamProxy mProxy;
    private Collection<Source> mSourcesList;
    private int mSourceIndex;
    private String mSiteName;
    private String mPlayPage;
    private int mEpisodeIndex;
    private int mLastEpisodeIndex;
    private int mEpisodeTotal;
//    private AsyncHttpClient mHttpc = new AsyncHttpClient();
    private HistoryDao databaseHandler;
    private int mMenuId;
    private String[] mDetectResult;
    private int mQualityPanelHeight;
    private int mSourcePanelHeight;
    private int mHd = -1;// current hd
    private int mLastHD;

    private ArrayList<Integer> mHdAll;
    private int mStartTime;
    private int mEndTime;
    private int mDuration;// in seconds
    //    private boolean mIsM3u8;
    private int mCurrentPosition;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Thread.setDefaultUncaughtExceptionHandler(onBlooey);

        setContentView(R.layout.activity_player);

        aq = new AQuery(this);
        databaseHandler = new HistoryDao(this);

        surface = (TappableSurfaceView) findViewById(R.id.surface);
        surface.addTapListener(onTap);
        holder = surface.getHolder();
        holder.addCallback(this);
//        holder.setFormat(PixelFormat.TRANSLUCENT);
//        holder.setFormat(PixelFormat.RGBA_8888);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surface.setZOrderOnTop(false);

        mSourceIndex = getIntent().getIntExtra("source_index",0);
        mSiteName = SourceHolder.getInstance().getSourcesList().get(mSourceIndex).getAlias();
        mEpisodeIndex = getIntent().getIntExtra("episode_index",0);
        mEpisodeTotal = SourceHolder.getInstance().getSourcesList().get(mSourceIndex).getEpisodes().size();

        // get last mHd selected by user
        /*PlayHistory ph = databaseHandler.find(p.getId());
        if (ph != null) {
            mHd = ph.getPlayHD();
            mSiteName = ph.getVideo();

            if (mEpisodeIndex == ph.getPart())
                mCurrentPosition = ph.getPlaytime();
            else
                mCurrentPosition = 0;

            if (mCurrentPosition > 0)
                mPlayerFromStart = false;
            else
                mPlayerFromStart = true;
        }*/

        streamProxyStart();

//        mProgresssTextView = (TextView) findViewById(R.id.progress_text);
//        mSpeedTextView = (TextView) findViewById(R.id.speed_text);
//        mDurationTextView = (TextView) findViewById(R.id.duration);

        // Avoiding Clipping when Animating a View on top of a SurfaceView
        ((ViewGroup) surface.getParent()).addView(new View(this));

//        mProgresssTextView.setX(timeline.getSeekBarThumb().getBounds().centerX() + 25);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
//        surface.postDelayed(onEverySecond, 1000);
//        surface.postDelayed(onEvery2Second, 2000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finishActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return (super.onKeyDown(keyCode, event));
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onCompletion");
        if (isPaused) return;

        // reset timeline position
//        m3u8LastSeekPosition = 0;
        mCurrentPosition = 0;
        timeline.setProgress(0);
        timeline.setSecondaryProgress(0);
        mPlayerFromStart = true;

        if (mHdAll != null) {
            mHdAll.clear();
            mHdAll = null;
        }

        // go to play next or quit
        mEpisodeIndex++;
        if (mEpisodeIndex >= mEpisodeTotal) {
            // all episode played over
            mEpisodeIndex--;
            finishActivity();
        } else
            detect();
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        width = player.getVideoWidth();
        height = player.getVideoHeight();

        if (width != 0 && height != 0) {
//            holder.setFixedSize(width, height);
            keepRatio();
        }

        mIsPrepared = true;

        if (mHdAll == null || mHdAll.size() == 0) {
            videoInfoParse(mDetectResult);

            // set position on first load
            if (mPlayerFromStart) {
                mCurrentPosition = 0;
            }
        }

        if (mCurrentPosition > 0)
            SeekTo(mCurrentPosition * 1000);
        else {
            mediaplayer.start();
        }


        // if no duration from http server, try to get duration from media player
        /*if (mDuration == 0 && mediaplayer != null) {
            int mediaPlayerDuration = mediaplayer.getDuration();
            if (mediaPlayerDuration > 0) {
                mDuration = mediaPlayerDuration / 1000;
                mDurationTextView.setText(seconds2TimeString(mDuration));
                timeline.setMax(mDuration);
            }
        }*/

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Log.d(TAG, "onError");

        if (!isPaused) {
            goBlooey(new Throwable("6:Exception in media prep"));
            return true;
        }

        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onSeekComplete");
        mediaPlayer.start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
        Log.d(TAG, "onVideoSizeChanged");
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        // no-op
        Log.d(TAG, "surfaceChanged");
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        // no-op
        Log.d(TAG, "surfaceDestroyed");

        synchronized (this) {
            hasActiveHolder = false;

            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // no-op
        Log.d(TAG, "surfaceCreated");

        synchronized (this) {
            hasActiveHolder = true;
            this.notifyAll();
        }
    }

    private boolean playVideo(String url) {
        try {
            if (player == null) {
                player = new MediaPlayer();
//                player.setScreenOnWhilePlaying(true);
            } else {
                player.stop();
                player.reset();
            }

            player.setDataSource(url);
//            player.setDisplay(holder);
            synchronized (this) {
                while (!hasActiveHolder) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "surface view create error");
                    }
                }
                player.setDisplay(holder);
                player.setScreenOnWhilePlaying(true);
            }

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnPreparedListener(this);
            player.prepareAsync();
            player.setOnCompletionListener(this);

            player.setOnErrorListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnVideoSizeChangedListener(this);

            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private void goBlooey(Throwable t) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setTitle("Exception!")
                .setMessage(t.toString())
                .setPositiveButton("OK", null)
                .show();*/

//        mHd = -1;

        int errorStringR = R.string.player_error_0;
        if (t.getMessage().startsWith("1")) {
            errorStringR = R.string.player_error_1;
        } else if (t.getMessage().startsWith("2")) {
            errorStringR = R.string.player_error_2;
        } else if (t.getMessage().startsWith("3")) {
            errorStringR = R.string.player_error_3;
        } else if (t.getMessage().startsWith("4")) {
            errorStringR = R.string.player_error_4;
        } else if (t.getMessage().startsWith("5")) {
            errorStringR = R.string.player_error_5;
        } else if (t.getMessage().startsWith("6")) {
            errorStringR = R.string.player_error_6;
        } else if (t.getMessage().startsWith("7")) {
            String[] tmpStr = t.getMessage().split(";");
            if (tmpStr.length > 1) {
                errorStringR = R.string.player_error_7;
                Toast.makeText(getApplicationContext(), tmpStr[1] + getResources().getString(errorStringR), Toast.LENGTH_SHORT).show();
            }
            return;
        } else if (t.getMessage().startsWith("8:")) {
            errorStringR = R.string.player_error_8;
        } else if (t.getMessage().startsWith("9:")) {
            errorStringR = R.string.player_error_9;
        }

        Toast.makeText(getApplicationContext(), errorStringR, Toast.LENGTH_SHORT).show();
        finishActivity();
    }

    public void keepRatio() {
        int widthN;
        int heightN;

        float videoProportion = (float) width / (float) height;

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        float screenProportion = (float) screenWidth / (float) screenHeight;

        if (videoProportion > screenProportion) {
            widthN = screenWidth;
            heightN = (int) ((float) screenWidth / videoProportion);
        } else {
            widthN = (int) (videoProportion * (float) screenHeight);
            heightN = screenHeight;
        }

//        ViewGroup.LayoutParams lp = surface.getLayoutParams();
//        lp.width = widthN - 1;
//        lp.height = heightN - 1;
//        surface.setLayoutParams(lp);
//        surface.invalidate();

        holder.setFixedSize(widthN - 1, heightN - 1);

    }

    public void originalSize() {
        holder.setFixedSize(width, height);
    }

    public void fullScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        holder.setFixedSize(screenWidth, screenHeight);
    }

    private String getEpisodeWebPageUrl() {
        String playUrl = null;

        Episode e = SourceHolder.getInstance().getSourcesList().get(mSourceIndex).getEpisodes().get(mEpisodeIndex);
        if (e != null)
            playUrl = e.getUrl();


        LOGD(TAG, "playUrl=" + playUrl);
        return playUrl;
    }

    private void detect() {

        if (mProxy == null || mProxy.mHost == null) {
            Throwable throwable = new Throwable("1:HttpServer is not ready!");
            goBlooey(throwable);
            return;
        }

        mPlayPage = getEpisodeWebPageUrl();
        if (mPlayPage == null || mPlayPage.isEmpty()) {
            goBlooey(new Throwable("2:play url is null!"));
            return;
        }

        final Uri uri = new Uri.Builder().scheme("http")
                .encodedAuthority(mProxy.mHost)
                .encodedPath("/proxy/detect")
                .appendQueryParameter("sitename", mSiteName)
                .appendQueryParameter("videokey", mHd >= 0 ? ("[" + mHd + "]" + mPlayPage) : mPlayPage)
                .build();
        Log.e(TAG, "uri=" + uri.toString());
        /*mHttpc.setTimeout(30000);
        mHttpc.get(uri.toString(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String data) {
                if (!hasActiveHolder)
                    return;

                Log.e(TAG, "detect finished,return contents: " + data);
                if (data != null) {
                    mDetectResult = data.split("\n");

                    boolean resultFormatValid = false;
                    if (mDetectResult.length > 1) {
                        try {
                            int number = Integer.parseInt(mDetectResult[0]);
                            if (number > 0)
                                resultFormatValid = true;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    if (resultFormatValid) {

                        mIsPrepared = false;
                        if (playVideo(uriPrehandle(mDetectResult[1]))) {
                            // update ui in onPrepared
                        } else {
                            goBlooey(new Throwable("6:Exception in media prep"));
                        }
                    } else {
                        goBlooey(new Throwable("3:wrong data format"));
                    }
                } else {
                    goBlooey(new Throwable("4:null data from server"));
                }

            }

            @Override
            public void onFailure(Throwable e, String data) {
                goBlooey(new Throwable("5:detect failed"));
            }

        });*/

        aq.ajax(uri.toString(), String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String html, AjaxStatus status) {
                LOGD(TAG,String.valueOf(status.getCode()));
                if (status.getCode() == 200){
                    if (!hasActiveHolder)
                        return;

                    Log.e(TAG, "detect finished,return contents: " + html);
                    if (html != null) {
                        mDetectResult = html.split("\n");

                        boolean resultFormatValid = false;
                        if (mDetectResult.length > 1) {
                            try {
                                int number = Integer.parseInt(mDetectResult[0]);
                                if (number > 0)
                                    resultFormatValid = true;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }

                        if (resultFormatValid) {

                            mIsPrepared = false;
                            if (playVideo(uriPrehandle(mDetectResult[1]))) {
                                // update ui in onPrepared
                            } else {
                                goBlooey(new Throwable("6:Exception in media prep"));
                            }
                        } else {
                            goBlooey(new Throwable("3:wrong data format"));
                        }
                    } else {
                        goBlooey(new Throwable("4:null data from server"));
                    }
                } else {
                    goBlooey(new Throwable("5:detect failed"));
                }
            }

        });
    }

    /**
     * 返回值 ：
     * spd:63,,,pre:0,,,state:0,,,buf:364,,,st:-1,,,at:2729
     * spd:当前速度，63 K/s
     * pre: 缓冲进度 0-100
     * state:下载状态 200 下载完成
     * buf: 缓冲的可以供给播放的秒数  364：还可以播放364秒
     * st: m3u8 快进后的基础时间  ；
     * at:视频的总时间
     */
    private void getHttpServerState() {

        if (mProxy == null || mProxy.mHost == null) {
            return;
        }

        int currentProgress = timeline.getProgress();

        final Uri uri = new Uri.Builder().scheme("http")
                .encodedAuthority(mProxy.mHost)
                .encodedPath("/proxy/state")
                .appendQueryParameter("pauseflag", "0")
                .appendQueryParameter("seektime", "0")
                .appendQueryParameter("time", String.valueOf(currentProgress))
                .build();

       /* mHttpc.get(uri.toString(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String data) {
//                Log.e(TAG, "get state return contents: " + data);
                if (data != null) {
                    String[] stateResult = data.split(",,,");
                    if (stateResult.length > 5) {
                        // update duration. Sometime we can not get duration through detect.
                        // but in this thread, we can reset it.
                        String[] nameValue = stateResult[5].split(":");
                        if (nameValue.length > 1 && mDuration == 0) {
                            mDuration = Integer.parseInt(nameValue[1]);
                            if (mDuration > 0) {
                                mDurationTextView.setText(seconds2TimeString(mDuration));
                                timeline.setMax(mDuration);
                            }
                        }

                        // update second progress
                        nameValue = stateResult[3].split(":");
                        if (nameValue.length > 1) {
                            timeline.setSecondaryProgress(timeline.getProgress() + Integer.parseInt(nameValue[1]));
                        }

                        // if player stuck, show download speed and bottom panel
                        if (!mForwardFlag) {
                            nameValue = stateResult[0].split(":");

                            // if player have been played for 10s , hide speed text
                            if (player != null
                                    && !isPaused
                                    && nameValue.length > 1) {
                                if (mIsPrepared && timeline.getProgress() - mLastPlayPosition < 6) {
                                    mSpeedTextView.setText(nameValue[1] + "K/s");
                                    mSpeedTextView.setVisibility(View.VISIBLE);
                                } else {
                                    mSpeedTextView.setText("");
                                    mSpeedTextView.setVisibility(View.GONE);
                                }
                            }
                        }

                    }

                }
            }

            @Override
            public void onFailure(Throwable e, String data) {
            }

        });*/

        aq.ajax(uri.toString(), String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String html, AjaxStatus status) {
                LOGD(TAG,String.valueOf(status.getCode()));
                if (status.getCode() == 200) {
                    if (html != null) {
                        String[] stateResult = html.split(",,,");
                        if (stateResult.length > 5) {
                            // update duration. Sometime we can not get duration through detect.
                            // but in this thread, we can reset it.
                            String[] nameValue = stateResult[5].split(":");
                            if (nameValue.length > 1 && mDuration == 0) {
                                mDuration = Integer.parseInt(nameValue[1]);
                                if (mDuration > 0) {
                                    mDurationTextView.setText(seconds2TimeString(mDuration));
                                    timeline.setMax(mDuration);
                                }
                            }

                            // update second progress
                            nameValue = stateResult[3].split(":");
                            if (nameValue.length > 1) {
                                timeline.setSecondaryProgress(timeline.getProgress() + Integer.parseInt(nameValue[1]));
                            }

                        }
                    }
                }
            }

        });
    }

    private String uriPrehandle(String uri) {
        /*String newUri = null;
        if (uri.endsWith("m3u8")) {
            mIsM3u8 = true;
            long systemCurrentSeconds = System.currentTimeMillis() / 1000;
            if (m3u8LastSeekPosition == 0)
                newUri = uri + ".ut." + systemCurrentSeconds + ".ts";
            else
                newUri = uri + ".vod." + m3u8LastSeekPosition + ".ut." + systemCurrentSeconds + ".ts";
        } else {
            newUri = uri;
            mIsM3u8 = false;
        }

        return newUri;*/
        return uri;
    }

    /**
     * construct quality sub_menu
     * It will change only depending on program changed
     */
    /*private void setUpSubMenuQuality() {
        ((ViewGroup) qualityPanel).removeAllViews();
        for (Integer i : mHdAll) {
            addNewSubMenuButtonOfQuality(i);
        }
        if (((ViewGroup) qualityPanel).getChildCount() > 0) {
            View view = ((ViewGroup) qualityPanel).getChildAt(((ViewGroup) qualityPanel).getChildCount() - 1);
            view.setNextFocusDownId(view.getId());
        }
    }*/


    private void addToRecent() {
        PlayHistory po = new PlayHistory();
        po.setId(SourceHolder.getInstance().getProgramId());
        po.setName(SourceHolder.getInstance().getProgramName());
//        po.setPost(program.getPoster());
        po.setPlaytime(timeline.getProgress());
        po.setVideo(mSiteName);
        po.setPart(mEpisodeIndex);
//        po.setScore(program.getScore());
//        po.setPlayHD(mHd);
//        po.setQuality(program.getQuality());
        databaseHandler.delete(po.getId());
        databaseHandler.save(po);
    }

    /**
     * sample:
     * hd=3,hdAll=8,startTime=0,endTime=150,duration=2729,sitename=qq
     *
     * @param mDetectResult
     */
    private void videoInfoParse(String[] mDetectResult) {
        String info = mDetectResult[mDetectResult.length - 1];

        int hdAll;

        String[] tmpStrings = info.split(",");
        for (int i = 0; i < tmpStrings.length; i++) {
            if (tmpStrings[i].startsWith("hd")) {
                if (tmpStrings[i].startsWith("hdAll")) {
                    hdAll = Integer.parseInt(tmpStrings[i].split("=")[1]);
                    HdParser(hdAll);
                } else {
                    mHd = Integer.parseInt(tmpStrings[i].split("=")[1]);
                }
            }

            if (tmpStrings[i].startsWith("startTime")) {
                mStartTime = Integer.parseInt(tmpStrings[i].split("=")[1]);
            }

            if (tmpStrings[i].startsWith("endTime")) {
                mEndTime = Integer.parseInt(tmpStrings[i].split("=")[1]);
            }

            if (tmpStrings[i].startsWith("duration")) {
                mDuration = Integer.parseInt(tmpStrings[i].split("=")[1]);
                if (mDuration > 0) {
//                    mDurationTextView.setText(seconds2TimeString(mDuration));
//                    timeline.setMax(mDuration);
                }
            }
        }

        if (mLocalVideos == null)
            mLocalVideos = new ArrayList<LocalVideoInfo>();
        else
            mLocalVideos.clear();

        for (int i = 0; i < Integer.parseInt(mDetectResult[0]); i++) {
            LocalVideoInfo localVideoInfo = new LocalVideoInfo();
            String[] tmpStrs = mDetectResult[i + 1].split("-mohoduration-");
            if (tmpStrs.length > 1) {
                localVideoInfo.setUrl(tmpStrs[0]);
                localVideoInfo.setDuration(Integer.parseInt(tmpStrs[1]));
            } else {
                localVideoInfo.setUrl(tmpStrs[0]);
                localVideoInfo.setDuration(mDuration);
            }
            mLocalVideos.add(localVideoInfo);
        }
    }

    /**
     * 清晰度含义： 00111111表示360,480,640,720,1280,1920
     * 1920:蓝光 0x20
     * 1280:超清 0x10
     * 720:高清 0x08
     * 640:标清 0x04
     * 480:流畅 0x02
     * 360:极速 0x01
     *
     * @param all
     */
    private void HdParser(int all) {
        if (mHdAll == null)
            mHdAll = new ArrayList<Integer>();
        else
            mHdAll.clear();

        if ((all & 0X01) == 1) {
            mHdAll.add(0);
        }

        if ((all & 0X02) == 2) {
            mHdAll.add(1);
        }

        if ((all & 0X04) == 4) {
            mHdAll.add(2);
        }

        if ((all & 0X08) == 8) {
            mHdAll.add(3);
        }

        if ((all & 0X10) == 16) {
            mHdAll.add(4);
        }

        if ((all & 0X20) == 32) {
            mHdAll.add(5);
        }
    }

    private String seconds2TimeString(int seconds) {
        long hours = seconds / 3600,
                remainder = seconds % 3600,
                minutes = remainder / 60,
                secs = remainder % 60;

        return ((hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (secs < 10 ? "0" : "") + secs);
    }

    /*private void forwardAction(int keyCode) {

        if (mProgresssTextView.getVisibility() != View.VISIBLE) {
            bottomPanel.setVisibility(View.VISIBLE);
            mProgresssTextView.setX(timeline.getSeekBarThumb().getBounds().centerX() + 25);
            mProgresssTextView.setVisibility(View.VISIBLE);
        }

        mForwardActionStartTime = System.currentTimeMillis();

        if (!mForwardFlag) {
            mForwardFlag = true;
            return;
        }

        int position = timeline.getProgress();
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            // 快退
            if (position > mForwardStep)
                position = position - mForwardStep;
            else
                position = 0;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            // 快进
            if (position < mDuration - mForwardStep)
                position = position + mForwardStep;
            else
                position = mDuration;
        }

//        setFlagOfProgress(position);
    }*/

    private void SeekTo(int milliSeconds) {
//        if (!mIsM3u8) {
        if (mLocalVideos.size() == 1)
            player.seekTo(milliSeconds);
        else {
            if (mIsJumpToUrlFinished) {
                player.seekTo(milliSeconds);
                mIsJumpToUrlFinished = false;
            } else {
                String jumpToUrl = null;
                int countPosition = 0;
                for (int i = 0; i < mLocalVideos.size(); i++) {
                    jumpToUrl = mLocalVideos.get(i).getUrl();
                    countPosition = countPosition + mLocalVideos.get(i).getDuration() * 1000;
                    if (countPosition > milliSeconds) {
                        break;
                    }
                }
                mCurrentPosition = milliSeconds - countPosition;
                mIsJumpToUrlFinished = true;
                detect();
            }
        }
        return;
//        }
//
//        m3u8LastSeekPosition = milliSeconds / 1000;
//        playVideo(uriPrehandle(mLocalVideos.get(0).getUrl()));
    }

    /*private void setFlagOfProgress(int seconds) {
        timeline.setProgress(seconds);
        mProgresssTextView.setAlpha(1f);
        mProgresssTextView.setText(seconds2TimeString(seconds));
//                  mProgresssTextView.setX(timeline.getSeekBarThumb().getBounds().centerX() + 25);
        mProgresssTextView.animate().cancel();
        mProgresssTextView.animate().setStartDelay(200);
        mProgresssTextView.animate().translationX(timeline.getSeekBarThumb().getBounds().centerX() + 25);
    }*/

    private void streamProxyStart() {
        if (mProxy == null) {

            mProxy = StreamProxy.getInstance();
            mProxy.setOnStateListener(getApplicationContext(), new StreamProxy.OnStateListener() {

                @Override
                public void onStarted(boolean success, int extra) {
                    if (success) {
                        LOGE(TAG, "StreamProxy started, pid - " + extra);
                        detect();
                    } else {
                        LOGE(TAG, "StreamProxy start failed, error code - " + extra);
                    }
                }

                @Override
                public void onProxyStatus(String statusLine) {
                    if (statusLine.equals("crashed")) {
                        Log.e(TAG, "StreamProxy crashed");
                        // if this source(mSiteName) make http server crash
                        // change to another source which has not been detected.
                        // if none source working existed,quit
                        /*String tmpSourceName = getAnotherSource();
                        if (tmpSourceName == null) {
                            goBlooey(new Throwable("8:no source working found"));
                        } else {
                            mSiteName = tmpSourceName;
                            // reset
                            mHd = -1;
                            if (mHdAll != null) {
                                mHdAll.clear();
                                mHdAll = null;
                            }
                        }*/

                        goBlooey(new Throwable("9:StreamProxy crashed"));
                    }

                }

                @Override
                public void onExit(boolean normal) {
                    LOGE(TAG, "StreamProxy exit, normal - " + normal);
                }
            });
        }
        mProxy.start();
    }


    private void finishActivity() {
        isPaused = true;

        surface.setVisibility(View.GONE);

        if (player != null) {
            // if no detecting, save to recent
            if (mWaiting.getVisibility() != View.VISIBLE)
                addToRecent();

            player.stop();
            player.reset();
            player.release();
            player = null;
        }

        surface.removeTapListener(onTap);

        if (mProxy != null)
            mProxy.stop();

        finish();
    }


    /**
     * Thread for update timeline
     */
    private Runnable onEverySecond = new Runnable() {
        public void run() {

            // disappear after reset 3 seconds
            if (System.currentTimeMillis() - mForwardActionStartTime > mForwardWaitingTime + 3000) {
                mProgresssTextView.setVisibility(View.INVISIBLE);
            }

            // 设回正在播放的位置
            if (mForwardFlag && System.currentTimeMillis() - mForwardActionStartTime > mForwardWaitingTime) {
                mForwardFlag = false;
                /*if (mIsM3u8) {
                    timeline.setProgress(m3u8LastSeekPosition + player.getCurrentPosition() / 1000);
                } else {
                    timeline.setProgress(player.getCurrentPosition() / 1000);
                }*/
                timeline.setProgress(player.getCurrentPosition() / 1000);
//                mProgresssTextView.animate().translationX(timeline.getSeekBarThumb().getBounds().centerX() + 25);
            }

            // 正常设置位置
            if (!isPaused && player != null && !mForwardFlag) {

                /*if (mIsM3u8) {
                    timeline.setProgress(m3u8LastSeekPosition + player.getCurrentPosition() / 1000);
                } else {
                    timeline.setProgress(player.getCurrentPosition() / 1000);
                }*/
                timeline.setProgress(player.getCurrentPosition() / 1000);
                mProgresssTextView.setText(seconds2TimeString(timeline.getProgress()));

                if (timeline.getSecondaryProgress() < timeline.getProgress()) {
                    timeline.setSecondaryProgress(timeline.getProgress());
                }

            }

            if (!isPaused) {
                surface.postDelayed(onEverySecond, 1000);
            }
        }
    };


    /**
     * Thread for get http serve state and update ui
     */
    private Runnable onEvery2Second = new Runnable() {
        public void run() {

            getHttpServerState();

            if (!isPaused) {
                surface.postDelayed(onEvery2Second, 2000);
            }
        }
    };


}
