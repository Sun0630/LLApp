//package com.example.myselfapp.net.entity;
//
//
//import org.apache.http.entity.mime.content.FileBody;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//public class ProgressFileBody extends FileBody {
//
//	public static final int BLOCK_SIZE = 8 * 1024;
//	private int              mIndex;//第几个文件
//	private ProgressListener mProgressListener;
//	private long             mTotal;//总大小
//	private String mKeyName     = "";
//	private int    mUploadTotal = 0;
//
//	public ProgressFileBody(File file) {
//		super(file);
//	}
//
//	public ProgressFileBody(File file, String keyName) {
//		super(file);
//		mKeyName = keyName;
//	}
//
//	public ProgressFileBody(File file, String keyName,String mimeType) {
//		super(file,mimeType);
//		mKeyName = keyName;
//	}
//
//	/**
//	 * 获取已传输大小
//	 *
//	 * @return 当前已上传大小
//	 */
//	public int getCurrent() {
//		return mUploadTotal;
//	}
//
//	/**
//	 * 获取已计算的总大小
//	 *
//	 * @return 大小
//	 */
//	public long getTotal() {
//		if (mTotal == 0) {
//			return getContentLength();
//		}
//		return mTotal;
//	}
//
//	public ProgressListener getProgressListener() {
//		return mProgressListener;
//	}
//
//	public int getIndex() {
//		return mIndex;
//	}
//
//	public void setProgressListener(ProgressListener progressListener) {
//		this.mProgressListener = progressListener;
//	}
//
//	public void setIndex(int index) {
//		this.mIndex = index;
//	}
//
//	@Override
//	public void writeTo(OutputStream out) throws IOException {
//		// 文件输入流
//		final InputStream in = super.getInputStream();
//		try {
//			// 缓存
//			final byte[] tmp = new byte[BLOCK_SIZE];
//			mTotal = getContentLength();
//			// 用来记录进度
//			for (int len; (len = in.read(tmp)) != -1; mProgressListener.update(mUploadTotal, mTotal, mIndex)) {
//				out.write(tmp, 0, len);
//				mUploadTotal += len;
//			}
//			out.flush();
//		} finally {
//			in.close();
//		}
//	}
//
//	public String getKeyName() {
//		return mKeyName;
//	}
//
//	public void setKeyName(String keyName) {
//		this.mKeyName = keyName;
//	}
//
//	public interface ProgressListener {
//		void update(long current, long total, int index);
//	}
//
//}
