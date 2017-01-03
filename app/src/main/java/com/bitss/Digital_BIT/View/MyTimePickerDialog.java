package com.bitss.Digital_BIT.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.Widget.ArrayWheelAdapter;
import com.bitss.Digital_BIT.Widget.OnWheelChangedListener;
import com.bitss.Digital_BIT.Widget.WheelView;

public class MyTimePickerDialog extends AlertDialog implements OnClickListener {

	private String[] items = { "15", "30", "60", "90", "120" };
	private final OnMyTimeSetListener mCallBack;
	private String ahead_minute; // 提前的时间

	private WheelView wv_minute;

	public MyTimePickerDialog(Context context, OnMyTimeSetListener callBack) {
		super(context);
		this.mCallBack = callBack;

		View view = LayoutInflater.from(context).inflate(R.layout.layout_time, null);

		// 提前的时间
		wv_minute = (WheelView) view.findViewById(R.id.mins);
		wv_minute.setAdapter(new ArrayWheelAdapter(items));
		wv_minute.setLabel("分");// 添加文字
		wv_minute.setCurrentItem(0);
		wv_minute.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String ahead_min = items[newValue];
				updateTitle(ahead_min);
			}
		});

		int textSize = 0;
		textSize = adjustFontSize(context, getWindow().getWindowManager());
		wv_minute.TEXT_SIZE = textSize;

		updateTitle(items[0]);
		setView(view);
		setButton("确定", this);
		setButton2("取消", (OnClickListener) null);
	}

	/**
	 * 更新title
	 * */
	public void updateTitle(String time) {
		String title = "将在开会前" + time + "分钟给您提醒";
		setTitle(title);
	}

	public void onClick(DialogInterface dialog, int which) {

		ahead_minute = items[wv_minute.getCurrentItem()];
		if (mCallBack != null) {
			mCallBack.onMyTimeSet(ahead_minute);
		}
	}

	public void show() {
		super.show();
	}

	public interface OnMyTimeSetListener {
		void onMyTimeSet(String ahead_minute);
	}

	public static int adjustFontSize(Context context, WindowManager windowmanager) {

		int result = 0;
		int screenWidth = windowmanager.getDefaultDisplay().getWidth();

		if (screenWidth <= 240) { // 240X320 屏幕
			result = 10;
		} else if (screenWidth <= 320) { // 320X480 屏幕
			result = 12;
		} else if (screenWidth <= 480) { // 480X800 或 480X854 屏幕
			result = 14;
		} else if (screenWidth <= 540) { // 540X960 屏幕
			result = 16;
		} else if (screenWidth <= 800) { // 800X1280 屏幕
			result = 18;
		} else { // 大于 800X1280
			result = 20;
		}

		return Utils.dp2pixel(context, result);
	}

}
