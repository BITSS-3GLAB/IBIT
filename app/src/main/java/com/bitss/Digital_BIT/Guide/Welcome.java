package com.bitss.Digital_BIT.Guide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Utils;

public class Welcome extends Activity {

	private Context context;
	private static final long TIMER_DELAY = 1000;
	private static ImageView defalutPic;
	private static BitmapDrawable bitmapDrawable;
	private static Bitmap mBitmap;
	private static final String TAG = Welcome.class.getSimpleName().toString();

	// default_pic

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_welcome);

		context = this;
		defalutPic = (ImageView) this.findViewById(R.id.default_pic);

	}

	@Override
	protected void onResume() {

		if (null == mBitmap || mBitmap.isRecycled()) {
			Log.i(TAG, "mBitmap.isRecycled() mBitmap is recreated");
			mBitmap = Utils.decodeScreenSizeBitmapFromResource(
					getApplicationContext(), getResources(),
					R.drawable.default_pic);
		}

		defalutPic.setImageBitmap(mBitmap);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// 通过版本号来判断是否是第一次安装
				PackageInfo packInfo = null;
				try {
					packInfo = getPackageManager().getPackageInfo(
							getPackageName(), 0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				SharedPreferences preferences = context.getSharedPreferences(
						"hasLeadPage",
						context.getApplicationContext().MODE_PRIVATE);
				String leadPageVersion = preferences.getString(
						"lead_page_version", "");

				if (leadPageVersion.equals("")
						|| !leadPageVersion.equals(packInfo.versionName)) {
					startActivity(new Intent(Welcome.this,
							FunctionLeadActivity.class));
				} else {
					startActivity(new Intent(Welcome.this, GuideActivity.class));
				}
				Welcome.this.finish();
			}
		}, TIMER_DELAY);
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		// 必须判断非空
		if (mBitmap != null && !mBitmap.isRecycled()) {
			defalutPic.setImageBitmap(null);
			Log.i(TAG, "!(mBitmap.isRecycled()");
			mBitmap.recycle();
			mBitmap = null;
			System.gc();
		}

		super.onDestroy();
	}

}
