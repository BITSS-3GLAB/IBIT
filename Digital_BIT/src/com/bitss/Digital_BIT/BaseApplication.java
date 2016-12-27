package com.bitss.Digital_BIT;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.baidu.mapapi.BMapManager;
import com.bitss.Digital_BIT.Post.database.ActivityDB;
import com.bitss.Digital_BIT.Post.provider.DataProvider;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.StrUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class BaseApplication extends Application {

	private final List<Activity> mActivityList = new ArrayList<Activity>();

	private static BMapManager mBMapMan = null;

	private SharedPreferences mPrefs = null;
	private DataProvider mProvider = null;
	private ActivityDB mDb = null;

	private Context context = null;
	private File imageLoaderCacheDir = null;
	public ImageLoaderConfiguration imageLoaderConfiguration = null;
	private DisplayImageOptions displayImageOptions = null;

	public BMapManager getBMapManager(Context mContext) {
		if (mBMapMan == null) {
			mBMapMan = new BMapManager(mContext);
			mBMapMan.init("hshbWTD3dqn3BZrLU6UEWsL7", null);
		}
		return mBMapMan;
	}

	public SharedPreferences getPreferences() {
		if (mPrefs == null) {
			mPrefs = getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE);
		}
		return mPrefs;
	}
	
	public DataProvider getProvider() {
		if (mProvider == null) {
			mProvider = DataProvider.getInstance(this);
		}
		return mProvider;
	}

	public ActivityDB getDB() {
		if (mDb == null) {
			mDb = ActivityDB.getInstance(this);
		}
		return mDb;
	}

	public File getimageLoaderCacheDir() {
		return imageLoaderCacheDir;
	}

	public void setimageLoaderCacheDir(File mImageLoaderCacheDir) {
		this.imageLoaderCacheDir = mImageLoaderCacheDir;
	}

	public DisplayImageOptions getdisplayImageOptions() {
		return displayImageOptions;
	}

	public void setmDisplayImageOptions(DisplayImageOptions mDisplayImageOptions) {
		this.displayImageOptions = mDisplayImageOptions;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			Bundle bundle = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA).metaData;
			Logger.DEBUG = bundle.getBoolean("DBLOG");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		imageLoaderCacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(), "UniversalImageLoader/Cache");

		context = this.getApplicationContext();

		imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.discCacheFileCount(200)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.writeDebugLogs() // Remove
				.build();

		displayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.uil_ic_stub)
				.showImageForEmptyUri(R.drawable.uil_ic_empty)
				.showImageOnFail(R.drawable.uil_ic_error)
				.resetViewBeforeLoading(false)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.displayer(new FadeInBitmapDisplayer(200)).cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.build();

		ImageLoader.getInstance().init(imageLoaderConfiguration); // Do it on
																	// Application
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		// close database
		if (mDb != null) {
			mDb.close();
			mDb = null;
		}
	}

	public void addActivity(Activity act) {
		mActivityList.add(act);
	}

	public void removeActivity(Activity act) {
		if (mActivityList.size() > 0) {
			mActivityList.remove(act);
		}
	}

	public void exitAll() {
		if (mActivityList.size() > 0) {
			for (Activity activity : mActivityList) {
				if (activity != null) {
					activity.finish();
				}
			}
		}
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}

	public void setLastRefreshTime(long time) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putLong(Constants.LAST_REFRESH_TIME_BROWSE_ACTIVITY, time);
		editor.commit();
	}

	public String getLastRefreshTime() {
		long time = getPreferences().getLong(
				Constants.LAST_REFRESH_TIME_BROWSE_ACTIVITY, 0);
		if (time == 0) {
			return "";
		}
		return getString(R.string.last_refresh_time) + StrUtils.getTime(time);
	}

}