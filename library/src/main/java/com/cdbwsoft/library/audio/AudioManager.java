package com.cdbwsoft.library.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.cdbwsoft.library.AppConfig;

/**
 * 音频管理器
 * Created by DDL on 2016/8/16.
 */

public class AudioManager implements Handler.Callback{

	public static final int MSG_CONNECTED = 0xc01;
	public static final int MSG_DISCONNECTED = 0xc02;

	private boolean mConnected;
	private AudioListener mAudioListener;
	private Context mContext;
	private HeadsetReceiver mReceiver;
	private Handler mHandler;
	private AudioPlayer mAudioPlayer;
	public AudioManager(Context context,AudioListener audioListener,AudioPlayer audioPlayer){
		mContext = context;
		mAudioListener = audioListener;
		mHandler = new Handler(mContext.getMainLooper(),this);
		mAudioPlayer = audioPlayer;

		mReceiver = new HeadsetReceiver();
	}

	public boolean isConnected(){
		return mConnected;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if(msg == null){
			return false;
		}
		switch(msg.what){
			case MSG_CONNECTED:
				mConnected = true;
				if(mAudioListener != null){
					mAudioListener.onConnectionChanged(true);
				}
				break;
			case MSG_DISCONNECTED:
				mConnected = false;
				if(mAudioListener != null){
					mAudioListener.onConnectionChanged(false);
				}
				break;
		}
		return false;
	}
	/**
	 * 暂停视图
	 */
	public void onPause(){
		try {
			if (mContext != null) {
				mContext.unregisterReceiver(mReceiver);
			}
		}catch(Exception e){
			if(AppConfig.DEBUG){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 恢复视图
	 */
	public void onResume(){
		try {
			if (mContext != null) {
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("android.intent.action.HEADSET_PLUG");
				mContext.registerReceiver(mReceiver, intentFilter);
			}
		}catch(Exception e){
			if(AppConfig.DEBUG){
				e.printStackTrace();
			}
		}
	}

	public void release() {
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			if (AppConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		mContext = null;
		mReceiver = null;
		mAudioPlayer.stop();
		mAudioPlayer = null;
	}

	public long writeData(byte[] data,int loopCount) {
		if (mAudioPlayer != null && mConnected) {
			return mAudioPlayer.play(data, loopCount);
		}
		return 0;
	}

	private class HeadsetReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case "android.intent.action.HEADSET_PLUG":
					if (intent.hasExtra("state")) {
						if (0 == intent.getIntExtra("state", 0)) {
							mHandler.obtainMessage(MSG_DISCONNECTED).sendToTarget();
						} else if (1 == intent.getIntExtra("state", 0)) {
							mHandler.obtainMessage(MSG_CONNECTED).sendToTarget();
						}
					}
					break;
			}
		}
	}
}
