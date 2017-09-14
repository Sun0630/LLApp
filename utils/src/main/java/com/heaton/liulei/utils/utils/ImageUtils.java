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
package com.heaton.liulei.utils.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * 图片处理工具类
 */
public class ImageUtils {
//	public static String getThumbnailImagePath(String imagePath) {
//		String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
//		path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
//		EMLog.d("msg", "original image path:" + imagePath);
//		EMLog.d("msg", "thum image path:" + path);
//		return path;
//	}

	public static final int SCALE_IMAGE_WIDTH = 640;
	public static final int SCALE_IMAGE_HEIGHT = 960;

	public ImageUtils() {
	}

	public static int[] computeSize(File srcImg) {
		int[] size = new int[2];

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 1;

		BitmapFactory.decodeFile(srcImg.getAbsolutePath(), options);
		size[0] = options.outWidth;
		size[1] = options.outHeight;

		return size;
	}

	/**
	 * @param urlpath
	 * @return Bitmap
	 * 根据图片url获取图片对象
	 */
	public static Bitmap getBitMBitmap(String urlpath) {
		Bitmap map = null;
		try {
			URL url = new URL(urlpath);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream in;
			in = conn.getInputStream();
			map = BitmapFactory.decodeStream(in);
			// TODO Auto-generated catch block
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param urlpath
	 * @return Bitmap
	 * 根据url获取布局背景的对象
	 */
	public static Drawable getDrawable(String urlpath){
		Drawable d = null;
		try {
			URL url = new URL(urlpath);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream in;
			in = conn.getInputStream();
			d = Drawable.createFromStream(in, "background.jpg");
			// TODO Auto-generated catch block
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	/**
	 * 转换图片成圆形
	 * @param bitmap 传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		width = dip2px(LLUtils.getmContext(),120);
		height = dip2px(LLUtils.getmContext(),120);
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width,
				height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
		final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap var0) {
		return getRoundedCornerBitmap(var0, 6.0F);
	}

	/***
	 * 图片的缩放方法
	 *
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
								   double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}


	/**
	 *
	 * 根据手机的分辨率 ?dp 的单 ?转成 ?px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	/**
	 *
	 * 根据手机的分辨率 ?px(像素) 的单 ?转成 ?dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	//获取圆角图片
	public static Bitmap getRoundedCornerBitmap(Bitmap var0, float var1) {
		Bitmap var2 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas var3 = new Canvas(var2);
		int var4 = -12434878;
		Paint var5 = new Paint();
		Rect var6 = new Rect(0, 0, var0.getWidth(), var0.getHeight());
		RectF var7 = new RectF(var6);
		var5.setAntiAlias(true);
		var3.drawARGB(0, 0, 0, 0);
		var5.setColor(-12434878);
		var3.drawRoundRect(var7, var1, var1, var5);
		var5.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		var3.drawBitmap(var0, var6, var6, var5);
		return var2;
	}

	public static Bitmap getVideoThumbnail(String var0, int var1, int var2, int var3) {
		Bitmap var4 = null;
		var4 = ThumbnailUtils.createVideoThumbnail(var0, var3);
//		EMLog.d("getVideoThumbnail", "video thumb width:" + var4.getWidth());
//		EMLog.d("getVideoThumbnail", "video thumb height:" + var4.getHeight());
		var4 = ThumbnailUtils.extractThumbnail(var4, var1, var2, 2);
		return var4;
	}

//	public static String saveVideoThumb(File var0, int var1, int var2, int var3) {
//		Bitmap var4 = getVideoThumbnail(var0.getAbsolutePath(), var1, var2, var3);
//		File var5 = new File(PathUtil.getInstance().getVideoPath(), "th" + var0.getName());
//
//		try {
//			var5.createNewFile();
//		} catch (IOException var11) {
//			var11.printStackTrace();
//		}
//
//		FileOutputStream var6 = null;
//
//		try {
//			var6 = new FileOutputStream(var5);
//		} catch (FileNotFoundException var10) {
//			var10.printStackTrace();
//		}
//
//		var4.compress(Bitmap.CompressFormat.JPEG, 100, var6);
//
//		try {
//			if (var6 != null) {
//				var6.flush();
//			}
//		} catch (IOException var9) {
//			var9.printStackTrace();
//		}
//
//		try {
//			var6.close();
//		} catch (IOException var8) {
//			var8.printStackTrace();
//		}
//
//		return var5.getAbsolutePath();
//	}

	public static Bitmap decodeScaleImage(String var0, int var1, int var2) {
		BitmapFactory.Options var3 = getBitmapOptions(var0);
		int var4 = calculateInSampleSize(var3, var1, var2);
//		EMLog.d("img", "original wid" + var3.outWidth + " original height:" + var3.outHeight + " sample:" + var4);
		var3.inSampleSize = var4;
		var3.inJustDecodeBounds = false;
		Bitmap var5 = BitmapFactory.decodeFile(var0, var3);
		int var6 = readPictureDegree(var0);
		Bitmap var7 = null;
		if(var5 != null && var6 != 0) {
			var7 = rotaingImageView(var6, var5);
			var5.recycle();
			var5 = null;
			return var7;
		} else {
			return var5;
		}
	}

	public static Bitmap decodeScaleImage(Context var0, int var1, int var2, int var3) {
		BitmapFactory.Options var5 = new BitmapFactory.Options();
		var5.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(var0.getResources(), var1, var5);
		int var6 = calculateInSampleSize(var5, var2, var3);
		var5.inSampleSize = var6;
		var5.inJustDecodeBounds = false;
		Bitmap var4 = BitmapFactory.decodeResource(var0.getResources(), var1, var5);
		return var4;
	}

	public static int calculateInSampleSize(BitmapFactory.Options var0, int var1, int var2) {
		int var3 = var0.outHeight;
		int var4 = var0.outWidth;
		int var5 = 1;
		if(var3 > var2 || var4 > var1) {
			int var6 = Math.round((float)var3 / (float)var2);
			int var7 = Math.round((float)var4 / (float)var1);
			var5 = var6 > var7?var6:var7;
		}

		return var5;
	}

	public static String getThumbnailImage(String var0, int var1) {
		Bitmap var2 = decodeScaleImage(var0, var1, var1);

		try {
			File var3 = File.createTempFile("image", ".jpg");
			FileOutputStream var4 = new FileOutputStream(var3);
			var2.compress(Bitmap.CompressFormat.JPEG, 60, var4);
			var4.close();
//			EMLog.d("img", "generate thumbnail image at:" + var3.getAbsolutePath() + " size:" + var3.length());
			return var3.getAbsolutePath();
		} catch (Exception var5) {
			var5.printStackTrace();
			return var0;
		}
	}

	public static String getScaledImage(Context var0, String var1) {
		File var2 = new File(var1);
		if(!var2.exists()) {
			return var1;
		} else {
			long var3 = var2.length();
//			EMLog.d("img", "original img size:" + var3);
			if(var3 <= 102400L) {
//				EMLog.d("img", "use original small image");
				return var1;
			} else {
				Bitmap var5 = decodeScaleImage(var1, 640, 960);

				try {
					File var6 = File.createTempFile("image", ".jpg", var0.getFilesDir());
					FileOutputStream var7 = new FileOutputStream(var6);
					var5.compress(Bitmap.CompressFormat.JPEG, 70, var7);
					var7.close();
//					EMLog.d("img", "compared to small fle" + var6.getAbsolutePath() + " size:" + var6.length());
					return var6.getAbsolutePath();
				} catch (Exception var8) {
					var8.printStackTrace();
					return var1;
				}
			}
		}
	}

	public static String getScaledImage(Context var0, String var1, int var2) {
		File var3 = new File(var1);
		if(var3.exists()) {
			long var4 = var3.length();
//			EMLog.d("img", "original img size:" + var4);
			if(var4 > 102400L) {
				Bitmap var6 = decodeScaleImage(var1, 640, 960);

				try {
					File var7 = new File(var0.getExternalCacheDir(), "eaemobTemp" + var2 + ".jpg");
					FileOutputStream var8 = new FileOutputStream(var7);
					var6.compress(Bitmap.CompressFormat.JPEG, 60, var8);
					var8.close();
//					EMLog.d("img", "compared to small fle" + var7.getAbsolutePath() + " size:" + var7.length());
					return var7.getAbsolutePath();
				} catch (Exception var9) {
					var9.printStackTrace();
				}
			}
		}

		return var1;
	}

	public static Bitmap mergeImages(int var0, int var1, List<Bitmap> var2) {
		Bitmap var3 = Bitmap.createBitmap(var0, var1, Bitmap.Config.ARGB_8888);
		Canvas var4 = new Canvas(var3);
		var4.drawColor(-3355444);
//		EMLog.d("img", "merge images to size:" + var0 + "*" + var1 + " with images:" + var2.size());
		byte var5;
		if(var2.size() <= 4) {
			var5 = 2;
		} else {
			var5 = 3;
		}

		int var6 = 0;
		int var7 = (var0 - 4) / var5;

		for(int var8 = 0; var8 < var5; ++var8) {
			for(int var9 = 0; var9 < var5; ++var9) {
				Bitmap var10 = var2.get(var6);
				Bitmap var11 = Bitmap.createScaledBitmap(var10, var7, var7, true);
				Bitmap var12 = getRoundedCornerBitmap(var11, 2.0F);
				var11.recycle();
				var4.drawBitmap(var12, (float)(var9 * var7 + var9 + 2), (float)(var8 * var7 + var8 + 2), null);
				var12.recycle();
				++var6;
				if(var6 == var2.size()) {
					return var3;
				}
			}
		}

		return var3;
	}

	public static int readPictureDegree(String var0) {
		short var1 = 0;

		try {
			ExifInterface var2 = new ExifInterface(var0);
			int var3 = var2.getAttributeInt("Orientation", 1);
			switch(var3) {
				case 3:
					var1 = 180;
				case 4:
				case 5:
				case 7:
				default:
					break;
				case 6:
					var1 = 90;
					break;
				case 8:
					var1 = 270;
			}
		} catch (IOException var4) {
			var4.printStackTrace();
		}

		return var1;
	}

	public static Bitmap rotaingImageView(int var0, Bitmap var1) {
		Matrix var2 = new Matrix();
		var2.postRotate((float)var0);
		Bitmap var3 = Bitmap.createBitmap(var1, 0, 0, var1.getWidth(), var1.getHeight(), var2, true);
		return var3;
	}

	public static BitmapFactory.Options getBitmapOptions(String var0) {
		BitmapFactory.Options var1 = new BitmapFactory.Options();
		var1.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(var0, var1);
		return var1;
	}

	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*** 将图片按照某个角度进行旋转
	** @param bm
	*            需要旋转的图片
	* @param degree
	*            旋转角度
	* @return 旋转后的图片
	*/

	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;
		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}
}
