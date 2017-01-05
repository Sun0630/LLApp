package com.umeng.soexample.music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;


import com.umeng.soexample.App;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;

import java.io.IOException;
import java.util.List;
import java.util.Random;


public class MusicService extends Service {

    private App myApplication;
    // private static int SAMPLE_RATE_IN_HZ = 8000;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private List<Playlist> playingList;

    private Playlist currentMusic;
    private Playlist lastMusic;
    private static final String PAUSE_BROADCAST_NAME = "com.dlkj.music.pause.broadcast";
    private static final String NEXT_BROADCAST_NAME = "com.dlkj.music.next.broadcast";
    private static final String PRE_BROADCAST_NAME = "com.dlkj.music.pre.broadcast";
    private static final int PAUSE_FLAG = 0x1;
    private static final int NEXT_FLAG = 0x2;
    private static final int PRE_FLAG = 0x3;
    public boolean isLoop = false;
    public boolean isRandom = false;

    // 通知栏
    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID = 0x1;
//    private ControlBroadcast mConrolBroadcast;
    public final IBinder mBinder = new MusicBinder();
    private Random random = new Random();

    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (musicModeActivity != null) {
                    musicModeActivity.next_img.performClick();
                }
            }
        });
        myApplication = (App) getApplication();
//        handler.sendEmptyMessageDelayed(1002, 200);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_BROADCAST_NAME);
        filter.addAction(NEXT_BROADCAST_NAME);
        filter.addAction(PRE_BROADCAST_NAME);
//        registerReceiver(mConrolBroadcast, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void setCurrentPlayList(List<Playlist> list) {
        playingList = list;
    }

    /**
     * 设置当前播放音乐
     *
     * @param music 要播放的音乐
     */
    public void setCurrentPlayMusic(Playlist music) {
        if (mMediaPlayer == null) {
            return;
        }
        if (currentMusic == null || !music.equals(currentMusic)) {
            Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + music.get_mid());
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(this, uri);
                mMediaPlayer.setLooping(isLoop);
                mMediaPlayer.prepare();
                this.currentMusic = music;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                mMediaPlayer.prepare();
                mMediaPlayer.seekTo(0);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前播放音乐的音乐列表
     *
     * @return 音乐列表
     */
    public Playlist getCurrentPlay() {
        return currentMusic;
    }

    /**
     * 播放音乐
     */
    public void play() {
        Log.e("play", "is play");
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
        musicModeActivity.startImgRotate();
        sendBroadcast(new Intent(Constants.ACTION_CHANGE_MUSIC));
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (MusicModeActivity.isStart) {
            musicModeActivity.stopImgRotate();
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
//        handler.sendEmptyMessageDelayed(1003, 1000);
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    /**
     * 播放下一首
     */
    public void next() {
        Log.e("service", "next");
        if (playingList == null) {
            return;
        }
        stop();
        if (playingList != null) {
            int index = playingList.indexOf(currentMusic);
            lastMusic = currentMusic;
            Playlist tempMusic;
            if (!isRandom) {
                if (index < playingList.size() - 1 && playingList.size() != 0) {
                    tempMusic = playingList.get(index + 1);
                } else {
                    tempMusic = playingList.get(0);
                }
            } else {
                tempMusic = playingList.get(getRandomIndex(playingList.size()));
            }
            if (tempMusic != null) {
                setCurrentPlayMusic(tempMusic);
                currentMusic = tempMusic;
                play();
            }
        }
    }

    /**
     * 播放上一首
     */
    public void last() {
        if (playingList == null) {
            return;
        }
        stop();
        if (!isRandom) {
            if (playingList != null) {
                int index = playingList.indexOf(currentMusic);
                if (index != -1 && index != 0) {
                    lastMusic = playingList.get(index - 1);
                } else {
                    lastMusic = playingList.get(playingList.size() - 1);
                }
            }
        } else {
            Log.e("previous", "随机");
            lastMusic = playingList.get(getRandomIndex(playingList.size()));
        }
        setCurrentPlayMusic(lastMusic);
        currentMusic = lastMusic;
        play();
    }

    /**
     * 是否正在播放音乐
     *
     * @return true: 正在播放 false: 无音乐播放
     */
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void setLoop(boolean isLoop) {
        mMediaPlayer.setLooping(isLoop);
        this.isLoop = isLoop;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setRandom(boolean isRandom) {
        mMediaPlayer.setLooping(false);
        this.isRandom = isRandom;
        Log.e("isRandom", "" + isRandom);
    }

    public boolean isRandom() {
        return isRandom;
    }

    public int getCurrentTime() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void setMediaPlayTime(int progress) {
        mMediaPlayer.seekTo(progress);
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public int getAudioVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setAudioVolume(int progress) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public List<Playlist> getPlayList() {
        return playingList;
    }

    private int getRandomIndex(int size) {
        return new Random().nextInt(size);
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            switch (msg.what) {
//                case 1001:
//                    break;
//                case 1002:
//                    if (myApplication.BlueServiceIsConnnected() && myApplication.mMusicServer != null && myApplication.mMusicServer.isPlaying()) {
//                        int a = 400 + random.nextInt(300);
//                        myApplication.sendBlueOrder(a);
//                    }
//                    handler.sendEmptyMessageDelayed(1002, 50);
//                    break;
//                case 1003:
//                    if (myApplication.BlueServiceIsConnnected()) {
//                        myApplication.sendBlueOrder(400);
//                    }
//                    break;
//            }
//        }
//    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private Handler mHandler;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    private MusicModeActivity musicModeActivity;

    public void setMusicContext(Context c) {
        this.musicModeActivity = (MusicModeActivity) c;
    }

    /**
     * ********** 以下为更新通知栏方法 ************
     */
    public void updateNotification(Bitmap bitmap) {
        if (currentMusic == null) {
            return;
        }
        Intent intent = new Intent(getApplicationContext(), MusicModeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification);
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = currentMusic.getTitle();
        notification.contentIntent = pi;
        notification.contentView = remoteViews;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.image, bitmap);
        } else {
            remoteViews.setImageViewResource(R.id.image, R.mipmap.img_album_background);
        }
        remoteViews.setTextViewText(R.id.bbs_type_Title, currentMusic.getTitle());
        remoteViews.setTextViewText(R.id.text, currentMusic.getArtist());
        // mNotificationManager.notify(NOTIFICATION_ID, mNotification);

        // 此处action不能是一样的 如果一样的 接受的flag参数只是第一个设置的值
        Intent pauseIntent = new Intent(PAUSE_BROADCAST_NAME);
        pauseIntent.putExtra("FLAG", PAUSE_FLAG);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_pause, pausePIntent);

        Intent nextIntent = new Intent(NEXT_BROADCAST_NAME);
        nextIntent.putExtra("FLAG", NEXT_FLAG);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.next_img, nextPIntent);

        Intent preIntent = new Intent(PRE_BROADCAST_NAME);
        preIntent.putExtra("FLAG", PRE_FLAG);
        PendingIntent prePIntent = PendingIntent.getBroadcast(this, 0, preIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.iv_previous, prePIntent);

        startForeground(NOTIFICATION_ID, notification);
    }

    public void cancelNotification() {
        stopForeground(true);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

}
