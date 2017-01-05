package com.umeng.soexample.music;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.core.control.logcat.Logcat;
import com.umeng.soexample.App;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;
import com.umeng.soexample.custom.AlphaForegroundColorSpan;
import com.umeng.soexample.custom.KenBurnsView;
import com.umeng.soexample.custom.floatView.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 音乐播放列表
 *
 * @author Administrator
 */
public class MusicListActivity extends Activity implements OnItemClickListener {

    private ArrayList<Playlist> list; // 音乐列表
    private PlaylistAdapter listAdapter;
    private MusicBroadCast receiver;

    private App myApplication;
    private Context mContext = MusicListActivity.this;
//    private ListView listView;

    /**
     * 定义查找音乐信息数组，1.标题，2音乐时间,3.艺术家,4.音乐id，5.显示名字,6.数据。
     */
    String[] media_info = new String[]{MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID};

    private int mActionBarTitleColor;
    private int mActionBarHeight;
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private ListView mListView;
    private KenBurnsView mHeaderPicture;
    private ImageView mHeaderLogo;
    private View mHeader;
    private View mPlaceHolderView;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;

    private RectF mRect1 = new RectF();
    private RectF mRect2 = new RectF();

    private AlphaForegroundColorSpan mAlphaForegroundColorSpan;
    private SpannableString mSpannableString;

    private TypedValue mTypedValue = new TypedValue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mHeaderHeight + getActionBarHeight();

        setContentView(R.layout.activity_musiclist);

        mListView = (ListView) findViewById(R.id.music_listview);
        mHeader = findViewById(R.id.header);
        mHeaderPicture = (KenBurnsView) findViewById(R.id.header_picture);
        mHeaderPicture.setResourceIds(R.mipmap.ai1, R.mipmap.ai2,R.mipmap.ai3,R.mipmap.ai4);
        mHeaderLogo = (ImageView) findViewById(R.id.header_logo);

        mActionBarTitleColor = getResources().getColor(R.color.abc_white);

        mSpannableString = new SpannableString(getString(R.string.music_list));
        mAlphaForegroundColorSpan = new AlphaForegroundColorSpan(mActionBarTitleColor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.attachToListView(mListView, new ScrollDirectionListener() {
//            @Override
//            public void onScrollDown() {
//
//            }
//
//            @Override
//            public void onScrollUp() {
//
//            }
//        }, new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });
//        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.music_fab);
//        fab.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
////                Snackbar.make(view,"敬请期待",Snackbar.LENGTH_LONG)
////                        .setAction("Action",null).show();
//                mListView.smoothScrollToPosition(0);
//            }
//        });

        setupActionBar();
        setupListView();
        init();
    }

    private void setupListView() {
        ArrayList<String> FAKES = new ArrayList<String>();
        for (int i = 0; i < 1000; i++) {
            FAKES.add("entry " + i);
        }
        mPlaceHolderView = getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(mPlaceHolderView);
        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FAKES));
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                //sticky actionbar
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                //header_logo --> actionbar icon
                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                interpolate(mHeaderLogo, getActionBarIconView(), mSmoothInterpolator.getInterpolation(ratio));
                //actionbar title alpha
                //getActionBarTitleView().setAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
                //---------------------------------
                //better way thanks to @cyrilmottier
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
    }

    private void setTitleAlpha(float alpha) {
        mAlphaForegroundColorSpan.setAlpha(alpha);
        mSpannableString.setSpan(mAlphaForegroundColorSpan, 0, mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(mSpannableString);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min,Math.min(value, max));
    }

    private void interpolate(View view1, View view2, float interpolation) {
        getOnScreenRect(mRect1, view1);
        getOnScreenRect(mRect2, view2);

        float scaleX = 1.0F + interpolation * (mRect2.width() / mRect1.width() - 1.0F);
        float scaleY = 1.0F + interpolation * (mRect2.height() / mRect1.height() - 1.0F);
        float translationX = 0.5F * (interpolation * (mRect2.left + mRect2.right - mRect1.left - mRect1.right));
        float translationY = 0.5F * (interpolation * (mRect2.top + mRect2.bottom - mRect1.top - mRect1.bottom));

        view1.setTranslationX(translationX);
        view1.setTranslationY(translationY - mHeader.getTranslationY());
        view1.setScaleX(scaleX);
        view1.setScaleY(scaleY);
    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }

    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.mipmap.ic_transparent);

        //getActionBarTitleView().setAlpha(0f);
    }

    private ImageView getActionBarIconView() {
        return (ImageView) findViewById(android.R.id.home);
    }

    /*private TextView getActionBarTitleView() {
        int id = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        return (TextView) findViewById(id);
    }*/

    public int getActionBarHeight() {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }
        getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
        mActionBarHeight = TypedValue.complexToDimensionPixelSize(mTypedValue.data, getResources().getDisplayMetrics());
        return mActionBarHeight;
    }

    /**
     * 初始化
     */
    protected void init() {
        myApplication = App.getInstance();
        list = new ArrayList<>();
//        listView = (ListView) findViewById(R.id.music_listview);
        listAdapter = new PlaylistAdapter(mContext, list);
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(this);
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        initData();
    }

    /**
     * 音乐服务回调
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            myApplication.mMusicServer = binder.getService();
            Logcat.d(getClass().getSimpleName(), "绑定音乐服务成功");
//            myApplication.mBound = true;
//            initMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logcat.d(getClass().getSimpleName(), "绑定音乐服务断开");
//            myApplication.mBound = false;
        }
    };

    private HashMap<String, ArrayList<Playlist>> musicMap;
    /**
     * 初始化音乐列表
     */
    private void initData() {
/*        if (list.size() == 0) {
//            list = getMp3List();
            if (musicMap == null) {
                musicMap = MusicUtil.getMp3Lists(MusicListActivity.this, myApplication.getDbUtils());
                if (musicMap != null) {
                    list = musicMap.get("musicList");
                }
            }
        }*/
        if (myApplication.mMusicServer != null) {
            list = (ArrayList<Playlist>) myApplication.mMusicServer.getPlayList();
        } else {
            list = (ArrayList<Playlist>) MusicUtil.getMp3List(MusicListActivity.this);
        }
        if(list!=null){
            listAdapter.setData(list);
        }
        listAdapter.notifyDataSetChanged();
    }

    /**
     * 歌曲列表
     */
    private ArrayList<Playlist> getMp3List() {
        ArrayList<Playlist> list = new ArrayList<>();
        Cursor cursor = new ResolverSer().getResolverSer().getResover(MusicListActivity.this).query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, media_info, "",
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        /**
         * 判断游标是否为空，有些地方即使没有音乐也会报异常。而且游标不稳定。稍有不慎就出错了,其次，如果用户没有音乐的话，
         * 不妨可以告知用户没有音乐让用户添加进去
         */
        if (cursor != null && cursor.getCount() == 0) {
            final AlertDialog dialog = new AlertDialog.Builder(MusicListActivity.this).setTitle("Tips:").setMessage(getResources().getString(R.string.music_no_one)).
                    setPositiveButton(getResources().getString(R.string.is_positive), null).create();
            dialog.show();
            return list;
        }
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            int album_id = cursor.getInt(cursor.getColumnIndex(AudioColumns.ALBUM_ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(AudioColumns.ARTIST));
            int duration = cursor.getInt(cursor.getColumnIndex(AudioColumns.DURATION));
            Playlist playlist = new Playlist(id, title, duration, artist, album_id);
            list.add(playlist);
        }
        Log.e("getMp3List ", "list.size() = " + list.size());
        cursor.close();
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 根据所点击选项卡来判断是哪个列表，刷新该列表。
        // 把当前播放列表改为点击音乐所在列表
        listAdapter.notifyDataSetChanged();
        // 得到当前选中的音乐，播放
        // 若当前播放的音乐id和所点击的一样，且正在播放，则暂停，否则播放
        Playlist playlist = (Playlist) parent.getItemAtPosition(position);
        if (myApplication.mMusicServer.getCurrentPlay() != null && playlist.get_mid() == myApplication.mMusicServer.getCurrentPlay().get_mid()
                && myApplication.mMusicServer.isPlaying()) {
            //	myApplication.mMusicServer.pause();
        } else {
            myApplication.mMusicServer.setCurrentPlayMusic(playlist);
            myApplication.mMusicServer.play();
            myApplication.mMusicServer.updateNotification(MusicUtil.getArtwork(MusicListActivity.this, playlist.get_mid(), playlist.getAlbum_id(), true));
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (receiver == null) {
            receiver = new MusicBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_CHANGE_MUSIC);
            mContext.registerReceiver(receiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
            receiver = null;
        }
        //接触绑定音乐服务
        unbindService(mConnection);
    }

    /**
     * 音乐改变广播接收者
     */
    private class MusicBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到广播，刷新界面
            listAdapter.notifyDataSetChanged();
        }
    }

}