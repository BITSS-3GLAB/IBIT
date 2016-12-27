package com.bitss.Digital_BIT.Util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.bitss.Digital_BIT.Personal.LoginActivity;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandDetailActivity;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandPublishActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;
import android.widget.Toast;

public class Utils {

	private static int displayWidth = 0;
	private static int displayHeight = 0;

	public static void showToast(Context context, int resid) {
		showToast(context, context.getResources().getString(resid));
	}

	public static void showToast(Context context, int resid, int duration) {
		showToast(context, context.getResources().getString(resid), duration);
	}

	public static void showToast(Context context, String msg) {
		showToast(context, msg, Toast.LENGTH_SHORT);
	}

	public static void showToast(Context context, String msg, int duration) {

		// Toast toast = new Toast(context);
		// // toast.setGravity(Gravity.CENTER, 0, 0);
		// toast.setDuration(duration);
		Toast.makeText(context, msg, duration).show();

	}

	/**
	 * 用户没有登录的情况
	 */
	public static void haveNotLogin(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				context);
		builder.setTitle("您尚未登录，无法执行此操作");
		builder.setPositiveButton("现在登录",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// 通过相机获取
						Intent intent = new Intent().setClass(
								context, LoginActivity.class);
						context.startActivity(intent);
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.cancel();
					}
				});

		builder.create().show();
		
	}
	
	/**
	 * 从相册中读取照片的信息
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}
	// public static Dialog makeGoingDialog(Context context, int id) {
	// Dialog dialog = new LoadingDialog(context, R.style.LoadingDialogTheme,
	// id);
	// return dialog;
	// }
	//
	// public static Dialog makeGoingDialog(Context context, String msg) {
	// Dialog dialog = new LoadingDialog(context, R.style.LoadingDialogTheme,
	// msg);
	// return dialog;
	// }

	/**
	 * check if network is available
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] infos = connectivity.getAllNetworkInfo();
			if (infos != null) {
				for (NetworkInfo info : infos) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * get screen width
	 * */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return manager.getDefaultDisplay().getWidth();
	}

	/**
	 * convert dp to px
	 */
	public static int dp2pixel(Context context, int dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale);
	}

	public static int pixel2dp(Context context, int pixel) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pixel / scale);
	}

	public static int getDensityScale(Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final float width = context.getResources().getDisplayMetrics().widthPixels;

		if (width >= 800) {
			return 2;
		}
		if (scale >= 2) {
			return 2;
		}
		return 1;
	}

	public static int getHalfDisplayWidth(Context context) {
		int halfWidth = (context.getResources().getDisplayMetrics().widthPixels) / 2;
		return halfWidth;
	}

	// 8是card_bg.9于桌面的透明间距大小
	public static int getImgActuralHeight(Context context, int height, int width) {
		int actural_height = (Utils.getHalfDisplayWidth(context) - 8) * height
				/ width;
		return actural_height;
	}

	public static int getDisplayWidth(Context context) {
		int width = (context.getResources().getDisplayMetrics().widthPixels);
		return width;
	}

	// 8是card_bg.9于桌面的透明间距大小
	public static int getDisplayHeight(Context context) {
		int height = (context.getResources().getDisplayMetrics().heightPixels);
		return height;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap decodeScreenSizeBitmapFromResource(Context context,
			Resources res, int resId) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		if (displayHeight == 0 || displayWidth == 0) {
			displayHeight = getDisplayHeight(context);
			displayWidth = getDisplayWidth(context);
		}
		options.inSampleSize = calculateInSampleSize(options, displayWidth,
				displayHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

}
