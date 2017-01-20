package com.umeng.soexample.music;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.core.Help;
import com.android.core.base.AbsBaseActivity;
import com.cdbwsoft.library.AppConfig;
import com.cdbwsoft.library.bluetooth.BtDevice;
import com.cdbwsoft.library.bluetooth.BtManager;
import com.umeng.soexample.App;
import com.umeng.soexample.R;
import com.heaton.liulei.utils.custom.CircleImageView;
import com.heaton.liulei.utils.utils.PhoneStateUtils;
import com.heaton.liulei.utils.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;

/**
 * 音乐模式
 */
public class MusicModeActivity extends AbsBaseActivity implements OnClickListener {

    @Bind(R.id.music_bg)
    LinearLayout bg;
    private CircleImageView iv_album; //
    public ImageView next_img; //
    private SeekBar cb_progress; //
    private ImageView musicPlays; //
    private TextView tv_time; //
    private TextView totle_time; //
    private TextView singer; //
    private TextView battery_tv; // 电量
    public static boolean isStart;
    private RotateAnimation animation;
    //	public static  PlayFlashView flashView;
    private App mApp;
    private Bitmap bmp;
    private List<Integer> list = new ArrayList<>();
    private Integer i;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setStatus();
                    break;
                case 1:
                    musicPlays.setImageResource(R.mipmap.play);
                    break;
                case 2:
                    if (mApp.mMusicServer != null && mApp.mMusicServer.isPlaying()) {
                        tv_time.setText(formatTime(mApp.mMusicServer.getCurrentTime()));
                        cb_progress.setMax(mApp.mMusicServer.getDuration());
                        cb_progress.setProgress(mApp.mMusicServer.getCurrentTime());
                    }
                    handler.sendEmptyMessageDelayed(2, 500);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getLayoutResource() {
        isTransparentSystem(true);
        return R.layout.activity_music_mode;
    }

    @Override
    protected void onInitView() {
        bg.setBackgroundResource(getRes());
        setTitle(R.string.music_song);
        Help.initSystemBar(this, R.color.transparent);//这个对所有的都适合
        toolbar.setBackgroundColor(getColor(R.color.transparent));
        toolbar.setNavigationIcon(R.mipmap.abc_ic_ab_back_mtrl_am_alpha);//必须放在setSupportActionBar后才有用，否则没有，设置返回图标

        mApp = App.getInstance();

        startMusicService();

        init();
        initAnim();
    }

    private Integer getRes() {
        list.add(R.mipmap.b_1);
        list.add(R.mipmap.b_2);
        list.add(R.mipmap.b_3);
        list.add(R.mipmap.b_4);
        i = list.get(new Random().nextInt(4));
        return i;
    }

    /**
     * 初始化 “旋转” 动画
     */
    private void initAnim() {
        animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(5000);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setRepeatCount(Animation.INFINITE);
    }

    /**
     * 开始 “旋转” 动画
     */
    public void startImgRotate() {
        iv_album.startAnimation(animation);
    }

    /**
     * 停止 “旋转” 动画
     */
    public void stopImgRotate() {
        iv_album.clearAnimation();// 清除此ImageView身上的动画
    }

    private void init() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        iv_album = (CircleImageView) findViewById(R.id.iv_album);
//        iv_album.setBorderColor(Color.rgb(108, 62, 62));
        ImageView previous_img = (ImageView) findViewById(R.id.previous_img);
        next_img = (ImageView) findViewById(R.id.next_img);
        cb_progress = (SeekBar) findViewById(R.id.cb_progress);
        cb_progress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    cb_progress.setProgress(progress);
                    mApp.mMusicServer.setMediaPlayTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        musicPlays = (ImageView) findViewById(R.id.play_pause_img);
        tv_time = (TextView) findViewById(R.id.tv_time);
        totle_time = (TextView) findViewById(R.id.tv_time_s);
        singer = (TextView) findViewById(R.id.singer);
        previous_img.setOnClickListener(this);
        next_img.setOnClickListener(this);
        musicPlays.setOnClickListener(this);
        battery_tv = (TextView) findViewById(R.id.battery_tv);
        mApp.updateBatteryAndIcon(mApp.batteryValue, battery_tv);
        if (mApp.mMusicServer != null) {
            mApp.mMusicServer.setHandler(handler);
            handler.sendEmptyMessage(2);
            mApp.mMusicServer.setMusicContext(this);
        }
    }

    private void setStatus() {
        if (mApp.mMusicServer == null || !mApp.mMusicServer.isPlaying()) {
            return;
        }
        if (mApp.mMusicServer.getCurrentPlay() != null) {
            String song = mApp.mMusicServer.getCurrentPlay().getTitle();
            totle_time.setText(formatTime(mApp.mMusicServer.getDuration()));
            singer.setText(mApp.mMusicServer.getCurrentPlay().getArtist());
            setTitle(song);
            tv_time.setText(formatTime(mApp.mMusicServer.getCurrentTime()));
            cb_progress.setMax(mApp.mMusicServer.getDuration());
            cb_progress.setProgress(mApp.mMusicServer.getCurrentTime());
//            bmp = MusicUtil.getArtwork(MusicModeActivity.this, mApp.mMusicServer.getCurrentPlay().get_mid(), mApp.mMusicServer.getCurrentPlay().getAlbum_id(), true);
//            iv_album.setImageBitmap(bmp);
//            Log.e("new music", "broadcast");
           /* if (MusicModeActivity.this != null) {
                bmp = MusicUtil.getArtwork(MusicModeActivity.this, mApp.mMusicServer.getCurrentPlay().get_mid(), mApp.mMusicServer.getCurrentPlay().getAlbum_id(), true);
                iv_album.setImageBitmap(bmp);
                Log.e("new music", "broadcast");
            }*/
        }
    }

    private void initView() {
        if (mApp.mMusicServer != null) {
            if (mApp.mMusicServer.isPlaying()) {
                musicPlays.setImageResource(R.mipmap.pause);
            } else {
                musicPlays.setImageResource(R.mipmap.play);
            }
            if (mApp.mMusicServer.isRandom() && !mApp.mMusicServer.isLoop()) {
                //	iv_circle.setImageResource(R.drawable.circle_no);
//                iv_random.setImageResource(R.mipmap.random);
            } else if (!mApp.mMusicServer.isRandom() && !mApp.mMusicServer.isLoop()) {
                //	iv_circle.setImageResource(R.drawable.all_circle);
//                iv_random.setImageResource(R.mipmap.random_no);
            } else if (mApp.mMusicServer.isLoop()) {
//                iv_random.setImageResource(R.mipmap.circle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mApp.mMusicServer.stop();
                mApp.mMusicServer.cancelNotification();
                finish();
                break;
            case R.id.action_play_list:
                startActivity(new Intent(this, MusicListActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isStart = false;
        if (mApp.mMusicServer != null && mApp.mMusicServer.isPlaying()) {
            stopImgRotate();
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //接触绑定音乐服务
        unbindService(mConnection);
    }

    @Override
    public void onResume() {
        isStart = true;
//        iv_list.setEnabled(true);
        initView();
        setStatus();
        if (mApp.mMusicServer != null && mApp.mMusicServer.isPlaying()) {
            startImgRotate();
        }
        registerCallBoradcastReceiver();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mApp.mMusicServer.stop();
        mApp.mMusicServer.cancelNotification();
    }

    public String formatTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_img:
                if (mApp.mMusicServer != null) {
                    Log.e("modle", mApp.mMusicServer.isRandom() + "isRandom");
                    Log.e("modle", mApp.mMusicServer.isLoop() + "isLoop");
                    mApp.mMusicServer.last();
                    setStatus();
                    mApp.mMusicServer.updateNotification(bmp);
                }
                initView();
                break;
            case R.id.next_img:
                Log.e("modle", mApp.mMusicServer.isRandom() + "isRandom");
                Log.e("modle", mApp.mMusicServer.isLoop() + "isLoop");
                if (mApp.mMusicServer != null) {
                    mApp.mMusicServer.next();
                    setStatus();
                    mApp.mMusicServer.updateNotification(bmp);
                }
                initView();
                break;
            case R.id.play_pause_img:
                if (mApp.mMusicServer.isPlaying()) {
                    musicPlays.setImageResource(R.mipmap.play);
                    mApp.mMusicServer.pause();
                    mApp.mMusicServer.cancelNotification();
                } else {
                    if (mApp.mMusicServer.getPlayList() == null) {//没有播放列表
//                    if (mApp.mMusicServer.getPlayList() == null) {//没有播放列表
//                        List<Playlist> list = MusicUtil.getMp3List(this, MyApplication.getInstance().getDbUtils());
//                        if (list == null || list.size() <= 0) {
//                            Toast.makeText(mApp, getResources().getString(R.string.music_no_one), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        mApp.mMusicServer.setCurrentPlayList(list);
//                        mApp.mMusicServer.setCurrentPlayMusic(list.get(0));
                        Toast.makeText(mApp, getResources().getString(R.string.music_no_one), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    musicPlays.setImageResource(R.mipmap.pause);
                    mApp.mMusicServer.play();
                    setStatus();
                    mApp.mMusicServer.updateNotification(bmp);
                }
                // initView();
                break;
            default:
                break;
        }
    }

    private void startMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
//            mApp.mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mApp.mMusicServer = binder.getService();
//            mApp.mBound = true;
            requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, "请求读取权限", new AbsBaseActivity.GrantedResult() {
                @Override
                public void onResult(boolean granted) {
                    if (granted) {
                        initMusic();
                    } else {
                        ToastUtil.showToast("权限被拒绝");
                        return;
                    }
                }
            });
            mApp.mMusicServer.setHandler(handler);
            handler.sendEmptyMessage(2);
            mApp.mMusicServer.setMusicContext(MusicModeActivity.this);
        }
    };

    //    private HashMap<String, ArrayList<Playlist>> musicMap;
    private List<Playlist> musicMap;

    private void initMusic() {
        new Thread() {
            @Override
            public void run() {
                musicMap = MusicUtil.getMp3List(MusicModeActivity.this);
                mApp.musicList = (ArrayList<Playlist>) musicMap;
                mApp.mMusicServer.setCurrentPlayList(mApp.musicList);
                mApp.mMusicServer.setCurrentPlayMusic(mApp.musicList.get(0));
                super.run();
            }
        }.start();
    }

    /**
     * 当电话来时接受的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(PhoneStateUtils.CALL_RINGING)) {
                if (mApp.mMusicServer.isPlaying()) {
                    musicPlays.setImageResource(R.mipmap.play);
                    mApp.mMusicServer.pause();
                    mApp.mMusicServer.cancelNotification();
                }
            }
        }

    };

    public void registerCallBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(PhoneStateUtils.CALL_RINGING);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

}
