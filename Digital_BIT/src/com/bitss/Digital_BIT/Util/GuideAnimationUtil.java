package com.bitss.Digital_BIT.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Tools.ParamUtil;

/**
 * 
 * @author huangqian 2013-8-18 启动页面的动画类
 * */
public class GuideAnimationUtil {

	private static final String SD_PATH = "/sdcard/IBIT/Guide/";
	private Context context;
	private ImageView guideImageView;

	private AlphaAnimation mFadeIn;
	private ScaleAnimation mFadeInScaleLarge;
	private ScaleAnimation mFadeInScaleSmall;
	private AlphaAnimation mFadeOut;

	private Drawable mPicture_new;

	public GuideAnimationUtil(Context context, ImageView imageView) {
		this.context = context;
		this.guideImageView = imageView;

		// 先播放最近的一张图，然后开线程去检查并下载最新的
		setAnimation();
		new Thread(new GuideTask()).start();
	}

	public void setAnimation() {
		// 从配置文件中获取最新的图片名字
		mPicture_new = getGuideDrawable();

		mFadeIn = new AlphaAnimation(0.2f, 1.0f);
		mFadeIn.setDuration(1000);

		mFadeInScaleLarge = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, 0.5f,
				0.5f);
		mFadeInScaleLarge.setDuration(5000);

		mFadeInScaleSmall = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, 0.5f,
				0.5f);
		mFadeInScaleSmall.setDuration(2500);

		mFadeOut = new AlphaAnimation(1.0f, 0.0f);
		mFadeOut.setDuration(1000);

		guideImageView.setImageDrawable(mPicture_new);
		guideImageView.startAnimation(mFadeIn);

		setListener();

	}

	/**
	 * 对动画的监听
	 **/
	public void setListener() {
		// 淡出
		mFadeOut.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

			}
		});

		// 进入
		mFadeIn.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				guideImageView.startAnimation(mFadeInScaleLarge);
			}
		});

		// 放大
		mFadeInScaleLarge.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				guideImageView.startAnimation(mFadeInScaleSmall);
			}
		});
	}

	public class GuideTask implements Runnable {

		@Override
		public void run() {
			if (Utils.isNetworkAvailable(context)) {
				Log.i("GuideTask", "start task to check guide info");

				String name = HttpAsker.getGuideParam(ParamUtil.GET_GUIDE_NAME);
				Log.i("GuideTask", "guide name " + name);

				if (name != null) {
					// 判断本地是否有该文件，没有就去下载
					if (checkFileStatus(name) == 0) {
						String url = HttpAsker
								.getGuideParam(ParamUtil.GET_GUIDE_URL);
						Log.i("GuideTask", "guide url " + url);
						if (url == null) {
							return;
						}

						Bitmap bmp = HttpAsker.getGuideBmp(url);
						if (bmp != null) {
							FileOutputStream b = null;

							File file = new File(SD_PATH);
							if (file.exists()) {

								File[] f = file.listFiles();
								for (int i = 0; i < f.length; i++) {
									Log.i("GuideTask", "delete front file");
									f[i].delete(); // 删除之前图片
								}
							} else {
								file.mkdirs();// 创建文件夹
							}

							String fileName = SD_PATH + name + ".jpg";

							try {
								b = new FileOutputStream(fileName);
								bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

								// 在配置文件中把最新的guide名字保存下来
								setGuideName(name);

							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} finally {
								try {
									if (b != null) {
										b.flush();
										b.close();
										bmp.recycle();
									}

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					} else {
						// 在配置文件中把最新的guide名字保存下来
						setGuideName(name);
					}

				}
			}

		}

	}

	/**
	 * @return -1:无sd卡; 0：无该文件 ;1：有该文件
	 * */
	public int checkFileStatus(String name) {
		int rt;
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			rt = -1;
		}

		File file = new File(SD_PATH + name + ".jpg");
		if (file.exists()) {
			rt = 1;
		} else {
			rt = 0;
		}
		return rt;
	}

	/**
	 * 保存最新图片的名字
	 * */
	public void setGuideName(String value) {
		SharedPreferences pref = context.getSharedPreferences(
				"newest_guide_name", Context.MODE_PRIVATE);
		pref.edit().putString("name", value).commit();
	}

	/**
	 * 获取最新图片的名字
	 * */
	public Drawable getGuideDrawable() {
		Drawable rt;

		SharedPreferences pref = context.getSharedPreferences(
				"newest_guide_name", Context.MODE_PRIVATE);
		String name = pref.getString("name", "default");

		Log.i("GuideTask", "guide name from pref: " + name);

		if (!name.equals("default") && checkFileStatus(name) == 1) {
			rt = new BitmapDrawable(context.getResources(),
					BitmapFactory.decodeFile(SD_PATH + name + ".jpg"));
		} else {
			// 用默认的图片
			rt = context.getResources().getDrawable(R.drawable.guide_pic1);
		}

		return rt;
	}

}
