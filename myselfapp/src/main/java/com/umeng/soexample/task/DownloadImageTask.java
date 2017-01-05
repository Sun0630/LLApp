/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.umeng.soexample.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap>{
	private DownloadFileCallback callback;
	Bitmap bitmap = null;
	public boolean downloadThumbnail = false;
	String message;
	private String remoteDir;

	public DownloadImageTask(String remoteDir, DownloadFileCallback callback){
		this.callback = callback;
		this.remoteDir = remoteDir;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		URL url = null;
		try {
			url = new URL(params[0]);
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {

					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(remoteDir);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					// 返回一个URI对象
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	    return null;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		callback.afterDownload(result);
	}
	
	@Override
	protected void onPreExecute() {
		callback.beforeDownload();
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		callback.downloadProgress(values[0]);
	}

	public interface DownloadFileCallback{
		void beforeDownload();//下载之前
		void downloadProgress(int progress);//下载时的当前进度
		void afterDownload(Bitmap bitmap);//下载完成后的回调
	}

	public static String getThumbnailImagePath(String imagePath) {
        String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
        path += "th" + imagePath.substring(imagePath.lastIndexOf("/")+1, imagePath.length());
//        EMLog.d("msg", "original image path:" + imagePath);
//        EMLog.d("msg", "thum image path:" + path);
        return path;
    }
}
