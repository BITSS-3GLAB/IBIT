package com.bitss.Digital_BIT.Tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bitss.Digital_BIT.Util.FileUtils;


import android.R.bool;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * Tools for handler picture
 * 
 * @author Ryan.Tang
 * 
 */
public final class ImageTools {

	/**
	 * Transfer drawable to bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap to drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	/**
	 * Input stream to bitmap
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static Bitmap inputStreamToBitmap(InputStream inputStream)
			throws Exception {
		return BitmapFactory.decodeStream(inputStream);
	}

	/**
	 * Byte transfer to bitmap
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Bitmap byteToBitmap(byte[] byteArray) {
		if (byteArray.length != 0) {
			return BitmapFactory
					.decodeByteArray(byteArray, 0, byteArray.length);
		} else {
			return null;
		}
	}

	/**
	 * Byte transfer to drawable
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Drawable byteToDrawable(byte[] byteArray) {
		ByteArrayInputStream ins = null;
		if (byteArray != null) {
			ins = new ByteArrayInputStream(byteArray);
		}
		return Drawable.createFromStream(ins, null);
	}

	/**
	 * Bitmap transfer to bytes
	 * 
	 * @param byteArray
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bm) {
		byte[] bytes = null;
		if (bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			bytes = baos.toByteArray();
		}
		return bytes;
	}

	/**
	 * Drawable transfer to bytes
	 * 
	 * @param drawable
	 * @return
	 */
	public static byte[] drawableToBytes(Drawable drawable) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		byte[] bytes = bitmapToBytes(bitmap);
		;
		return bytes;
	}

	/**
	 * Base64 to byte[] //
	 */
	// public static byte[] base64ToBytes(String base64) throws IOException {
	// byte[] bytes = Base64.decode(base64);
	// return bytes;
	// }
	//
	// /**
	// * Byte[] to base64
	// */
	// public static String bytesTobase64(byte[] bytes) {
	// String base64 = Base64.encode(bytes);
	// return base64;
	// }

	/**
	 * Create reflection images
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * Get rounded corner images
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            5 10
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Resize the bitmap
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * Resize the drawable
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		matrix.postScale(sx, sy);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}

	/**
	 * Get images from SD card by path and the name of image
	 * 
	 * @param photoName
	 * @return
	 */
	public static Bitmap getPhotoFromSDCard(String path, String photoName) {
		Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName
				+ ".jpg");
		if (photoBitmap == null) {
			return null;
		} else {
			return photoBitmap;
		}
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
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

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 通过编码方式获取占内存较小的位图对象。只能获取jpg文件。
	 * 
	 * @param path
	 * @param photoName
	 * @return
	 * @throws IOException
	 */
	public static Bitmap decodePhotoFromSDCard(String path, String photoName, int destW,
			int destH, boolean fixRotating) {

		String imgpath = path + "/" +photoName +".jpg";
		InputStream is = null;
		InputStream is2 = null;
		try {
			is = new FileInputStream(imgpath);
			int heightRatio = 1;
			int widthRatio = 1;
			BitmapFactory.Options opts=new BitmapFactory.Options();
			// 先解析图片的边缘获取它的尺寸。
			opts.inJustDecodeBounds = true;
			Bitmap temp =BitmapFactory.decodeStream(is,null, opts); 
			
			is.close();
			is = null;
			
			heightRatio = (int)Math.ceil(opts.outHeight/(float)destH);  
	        widthRatio = (int)Math.ceil(opts.outWidth/(float)destW);
			
	        temp = null;
			
			
			
			//2.为位图设置100K的缓存
			opts.inTempStorage = new byte[100 * 1024];
			//3.设置位图颜色显示优化方式
			//ALPHA_8：每个像素占用1byte内存（8位）
			//ARGB_4444:每个像素占用2byte内存（16位）
			//ARGB_8888:每个像素占用4byte内存（32位）
			//RGB_565:每个像素占用2byte内存（16位）
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			//4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
			opts.inPurgeable = true;
			
			if (heightRatio > 1 && widthRatio > 1)  
	        {  
				int tempRatio = heightRatio > widthRatio ? heightRatio:widthRatio; 
				opts.inSampleSize = tempRatio / 2 *2;  // 保证不出错，使用2的倍数来。
	        } 
			
			opts.inJustDecodeBounds = false;  
			
			//6.设置解码位图的尺寸信息
			opts.inInputShareable = true; 
			
			//7.解码位图
			is2 = new FileInputStream(imgpath);
			Bitmap result1 =BitmapFactory.decodeStream(is2,null, opts);    
			
			// 解决图片拍照后旋转的问题。
			if (fixRotating)
			{
				int degree = ImageTools.readPictureDegree(imgpath);
				Bitmap result = ImageTools.rotaingImageView(degree, result1);
				
				return result;
			}
			else
			{
				return result1;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		finally
		{
			try {
				if (is != null)
				{
					is.close();
				}
				if (is2 != null)
				{
					is2.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}

	/**
	 * Check the SD card
	 * 
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Get image from SD card by path and the name of image
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean findPhotoFromSDCard(String path, String photoName) {
		boolean flag = false;

		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (dir.exists()) {
				File folders = new File(path);
				File photoFile[] = folders.listFiles();
				for (int i = 0; i < photoFile.length; i++) {
					String fileName = photoFile[i].getName().split("\\.")[0];
					if (fileName.equals(photoName)) {
						flag = true;
					}
				}
			} else {
				flag = false;
			}
			// File file = new File(path + "/" + photoName + ".jpg" );
			// if (file.exists()) {
			// flag = true;
			// }else {
			// flag = false;
			// }

		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * Save image to the SD card , quality表示图片的质量，为0~100，值越高，质量越好。
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static void savePhotoToSDCard(Bitmap photoBitmap, String path,
			String photoName, int quality) {
		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName + ".jpg");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG,
							quality, fileOutputStream)) {
						fileOutputStream.flush();
						// fileOutputStream.close();
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Delete the image from SD card
	 * 
	 * @param context
	 * @param path
	 *            file:///sdcard/temp.jpg
	 */
	public static void deleteAllPhoto(String path) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	public static void deletePhotoAtPathAndName(String path, String fileName) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().split("\\.")[0].equals(fileName)) {
					files[i].delete();
				}
			}
		}
	}

	// DEBUG USER
	private static boolean Clear_SysPhotos = true;

	private static String[] olderList = null;
	private static String[] newFiles = null;

	private static String[] olderList_t = null;
	private static String[] newFiles_t = null;

	private static String defaultCameraDir = Environment
			.getExternalStorageDirectory() + "/DCIM/Camera/";

	private static String defaultThumbDir = Environment
			.getExternalStorageDirectory() + "/DCIM/.thumbnails/";

	// 刷新提供缓存图片库的显示。
	public static void ScanSysPhotos(Context context) {
		// 先 在“管理应用程序”里清一下图库的数据, 再删除sdcard/android/data/com.cooliris.media文件夹下内容
		// 待测试能否使用。

		File f = new File(defaultThumbDir);
		if (f.exists()) {

			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

			Uri uri = Uri.fromFile(f);
			intent.setData(uri);

			// Intent intent = new Intent(
			// Intent.ACTION_MEDIA_MOUNTED, uri);

			context.sendBroadcast(intent);
		}

		File f1 = new File(defaultCameraDir);
		if (f.exists()) {

			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

			Uri uri = Uri.fromFile(f1);
			intent.setData(uri);

			// Intent intent = new Intent(
			// Intent.ACTION_MEDIA_MOUNTED, uri);

			context.sendBroadcast(intent);
		}

	}

	// 李曦 TODO 2014-1-13 这里暂时不清空所有的临时图片，不然会导致莫名的错误。估计是clear的时候使用了线程导致的问题。
	// 图片还未转换成功，但线程已经启动了。
	public static void CatchOlderImageList() {
		// 将缓存中的数据都清空。
		olderList = null;
		newFiles = null;
		olderList_t = null;
		newFiles_t = null;

		if (!Clear_SysPhotos)
			return;

		File file = new File(defaultCameraDir);
		if (file.exists()) {
			olderList = file.list();
		}

		// // 对于有缩略图的手机，也要删除新生成的缩略图。
		// File dir_t = new File(defaultThumbDir);
		// if (dir_t.exists())
		// {
		// olderList_t = dir_t.list();
		// }
	}

	private static String imgTag = "";

	public static String getCorpImgName() {
		if (TextUtils.isEmpty(imgTag)) {
			Long t = System.currentTimeMillis();
			imgTag = t.toString();
		}

		return imgTag + "_f_c_image";
	}

	public static void generateNewCorpImgTag() {
		Long t = System.currentTimeMillis();
		imgTag = t.toString();
	}

	/**
	 * 删除相册中最新拍的一张图片，只能删除最新的一张。
	 * 
	 * @param mContext
	 */
	@SuppressWarnings("deprecation")
	public static void deleteLatestPhoto(Activity mContext) {

		try {
			String[] projection = new String[] {
					MediaStore.Images.ImageColumns._ID,
					MediaStore.Images.ImageColumns.DATE_TAKEN };
			Cursor cursor = mContext.managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					null, null, MediaStore.Images.ImageColumns.DATE_TAKEN
							+ " DESC");
			if (cursor != null) {
				cursor.moveToFirst();
				ContentResolver cr = mContext.getContentResolver();
				cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						BaseColumns._ID + "=" + cursor.getString(0), null);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
