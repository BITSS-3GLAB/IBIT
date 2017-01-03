package com.bitss.Digital_BIT.Admission;

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
import com.bitss.Digital_BIT.Widget.WheelView;

/**
 * 招生计划、历年分数搜索条件切换
 * */
public class SearchPickerDialog extends AlertDialog implements OnClickListener {

	private final static String TAG = SearchPickerDialog.class.getSimpleName();

	final WheelView wv_year, wv_province, wv_type;
	private final OnSearchChangeSetListener mCallBack;

	// 添加大小月月份并将其转换为list,方便之后的判断
	private String[] yearArray;
	private String[] provinceArray;
	private String[] typeArray;

	public SearchPickerDialog(Context context, OnSearchChangeSetListener callBack) {
		super(context);
		mCallBack = callBack;
		yearArray = context.getResources().getStringArray(R.array.array_year);
		provinceArray = context.getResources().getStringArray(R.array.array_province);
		typeArray = context.getResources().getStringArray(R.array.array_type);

		View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_time, null);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new ArrayWheelAdapter(yearArray));
		wv_year.setCurrentItem(0);

		// 省份
		wv_province = (WheelView) view.findViewById(R.id.month);
		wv_province.setAdapter(new ArrayWheelAdapter(provinceArray));
		wv_province.setCurrentItem(0);

		// 理工或文史
		wv_type = (WheelView) view.findViewById(R.id.day);
		wv_type.setAdapter(new ArrayWheelAdapter(typeArray));
		wv_type.setCurrentItem(0);

		int textSize = 0;
		textSize = adjustFontSize(context, getWindow().getWindowManager());
		wv_year.TEXT_SIZE = textSize;
		wv_province.TEXT_SIZE = textSize;
		wv_type.TEXT_SIZE = textSize;

		setView(view);
		setButton("确定", this);
		setButton2("取消", (OnClickListener) null);
	}

	public void onClick(DialogInterface dialog, int which) {
		if (mCallBack != null) {
			String year = yearArray[wv_year.getCurrentItem()];
			String province = provinceArray[wv_province.getCurrentItem()];
			String type = typeArray[wv_type.getCurrentItem()];

			mCallBack.onDataChangeSet(year, province, type);
		}
	}

	public void show() {
		super.show();
	}

	public interface OnSearchChangeSetListener {
		void onDataChangeSet(String year, String province, String type);
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
