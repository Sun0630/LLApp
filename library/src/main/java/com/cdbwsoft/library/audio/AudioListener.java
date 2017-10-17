package com.cdbwsoft.library.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.cdbwsoft.library.base.DataListener;

/**
 * 音频监听器
 * Created by DDL on 2016/5/16.
 */
public abstract class AudioListener extends DataListener {
	public final String TAG = AudioListener.class.getSimpleName();
	private final Context mContext;
	private AudioManager mAudioManager;
	private boolean mSending = false;
	private AudioTrack mAudioTrack;
	public AudioListener(Context context){
		mContext = context;
	}

	public boolean checkMaxVolume(){
		if(mAudioManager == null) {
			mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		}
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		return volume == maxVolume;
	}

	@Override
	protected boolean doWriteData(byte[] data) {
		return false;
	}

	@Override
	protected boolean doReadData(byte[] data) {
		return false;
	}


	public void onConnectionChanged(boolean connected){}

}
