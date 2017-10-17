package com.cdbwsoft.library.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.cdbwsoft.library.AppConfig;


public abstract class AudioPlayer {

	public static final String TAG = "MessageOut";

	public static final int AUDIO_SAMPLE_RATE   = 48000;
	//设置 头header & bit0 & bit1 的频率值
	public static final int HEADER_1K_HZ_RATE  = 1000;
	public static final int HEADER_SAMPLE_RATE = 2000;
	public static final int NUM_1_SAMPLE_RATE  = 2800;
	public static final int NUM_0_SAMPLE_RATE  = 4000;

	private boolean mDualChannel;
	private int     mEncodedBufferSize;

	private boolean msgIsSending = false;

	private AudioTrack mAudioTrack;

	public AudioPlayer(boolean dualChannel) {
		mDualChannel = dualChannel;
	}

	public boolean msgIsSending() {
		return msgIsSending;
	}

	public void setEncodedBufferSize(int encodedBufferSize) {
		mEncodedBufferSize = encodedBufferSize;
	}


	public abstract byte[] encode(byte[] data);

	public long play(byte[] data, int loopCount) {
		byte[] pcmData = encode(data);
		mEncodedBufferSize = pcmData.length;
		if (msgIsSending)
			stop();

		int msgMinBufferSize = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE,    //采样率
		                                                   AudioFormat.CHANNEL_OUT_STEREO,        //
		                                                   AudioFormat.ENCODING_PCM_8BIT);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
		                             mDualChannel ? AUDIO_SAMPLE_RATE / 2 : AUDIO_SAMPLE_RATE,
		                             mDualChannel ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO,    //单通道播放还是双通道播放
		                             AudioFormat.ENCODING_PCM_8BIT,
		                             msgMinBufferSize,
		                             AudioTrack.MODE_STATIC);

		msgIsSending = true;

		byte[] snd = new byte[mEncodedBufferSize];
		System.arraycopy(pcmData, 0, snd, 0, pcmData.length);

		int write = mAudioTrack.write(snd, 0, snd.length);

		if (AppConfig.DEBUG) {
			Log.d(TAG, "写入音频数据完成：" + write);
		}
		if (write < 0) {
			return write;
		}

		if (mDualChannel) {
			mAudioTrack.setStereoVolume(1, 1);
			mAudioTrack.setLoopPoints(0, mEncodedBufferSize / 2, loopCount);
		} else {
			mAudioTrack.setStereoVolume(0, 1);
			mAudioTrack.setLoopPoints(0, mEncodedBufferSize, loopCount);
		}

		mAudioTrack.play();

		return ((long) pcmData.length * 1000) / AUDIO_SAMPLE_RATE + 100;
	}

	public void stop() {
		if (mAudioTrack != null) {
			mAudioTrack.release();
			mAudioTrack = null;
		}
		msgIsSending = false;
	}
}
