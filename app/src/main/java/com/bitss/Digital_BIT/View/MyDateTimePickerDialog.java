package com.bitss.Digital_BIT.View;

import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.Widget.NumericWheelAdapter;
import com.bitss.Digital_BIT.Widget.OnWheelChangedListener;
import com.bitss.Digital_BIT.Widget.WheelView;

public class MyDateTimePickerDialog extends AlertDialog implements OnClickListener {
	private final static String TAG = MyDateTimePickerDialog.class.getSimpleName();

	private final OnDateTimeSetListener mCallBack;
	private int curr_year, curr_month, curr_day;

	// 添加大小月月份并将其转换为list,方便之后的判断
	String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	String[] months_little = { "4", "6", "9", "11" };

	final WheelView wv_year, wv_month, wv_day;
	final List<String> list_big, list_little;

	public MyDateTimePickerDialog(Context context, int year, int month, int day,
	        OnDateTimeSetListener callBack) {
		super(context);
		mCallBack = callBack;

		curr_year = year;
		curr_month = month;
		curr_day = day;
		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);

		View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_time, null);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(curr_year, curr_year));
		// wv_year.setAdapter(new NumericWheelAdapter(curr_year, curr_year + 1));
		wv_year.setLabel("年");
		wv_year.setCurrentItem(0);

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(curr_month + 1, 12));
		wv_month.setLabel("月");
		wv_month.setCurrentItem(0);

		// 日 (判断大小月及是否闰年,用来确定"日"的数据)
		wv_day = (WheelView) view.findViewById(R.id.day);

		if (list_big.contains(String.valueOf(curr_month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(curr_day, 31));
		} else if (list_little.contains(String.valueOf(curr_month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(curr_day, 30));
		} else {
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(curr_day, 29)); // 闰年
			else
				wv_day.setAdapter(new NumericWheelAdapter(curr_day, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(0);

		// 添加"年"监听
		// TODO: 凡是改变就设置新建的NumericWheelAdapter，可以看一下NumericWheelAdapter是不是单例的
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + curr_year;
				// 如果设置的年的时间比当前年要大，则显示所有的月份
				Log.i(TAG, "year_num:" + year_num + " newValue:" + newValue + " oldValue："
				        + oldValue + " curr_year:" + curr_year);
				if (year_num > curr_year) {
					wv_month.setAdapter(new NumericWheelAdapter(1, 12));
				} else {
					wv_month.setAdapter(new NumericWheelAdapter(curr_month + 1, 12));
				}
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					if (year_num == curr_year) {
						wv_day.setAdapter(new NumericWheelAdapter(curr_day, 31));
					} else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					}
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					if (year_num == curr_year) {
						wv_day.setAdapter(new NumericWheelAdapter(curr_day, 30));
					} else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					}
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
						if (year_num == curr_year) {
							wv_day.setAdapter(new NumericWheelAdapter(curr_day, 29));
						} else {
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						}
					} else {
						if (year_num == curr_year) {
							wv_day.setAdapter(new NumericWheelAdapter(curr_day, 28));
						} else {
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						}
					}
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = wv_year.getCurrentItem() + curr_year;
				int month_num = newValue + curr_month + 1;

				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					if (year_num == curr_year && month_num == curr_month + 1) {
						wv_day.setAdapter(new NumericWheelAdapter(curr_day, 31));
					} else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 31));
					}
				} else if (list_little.contains(String.valueOf(month_num))) {
					if (year_num == curr_year && month_num == curr_month + 1) {
						wv_day.setAdapter(new NumericWheelAdapter(curr_day, 30));
					} else {
						wv_day.setAdapter(new NumericWheelAdapter(1, 30));
					}
				} else {
					if (((wv_year.getCurrentItem()) % 4 == 0 && (wv_year.getCurrentItem()) % 100 != 0)
					        || (wv_year.getCurrentItem()) % 400 == 0) {
						if (year_num == curr_year && month_num == curr_month + 1) {
							wv_day.setAdapter(new NumericWheelAdapter(curr_day, 29));
						} else {
							wv_day.setAdapter(new NumericWheelAdapter(1, 29));
						}
					}

					else {
						if (year_num == curr_year && month_num == curr_month + 1) {
							wv_day.setAdapter(new NumericWheelAdapter(curr_day, 28));
						} else {
							wv_day.setAdapter(new NumericWheelAdapter(1, 28));
						}
					}
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		int textSize = 0;
		textSize = adjustFontSize(context, getWindow().getWindowManager());
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

		setView(view);
		setButton("确定", this);
		setButton2("取消", (OnClickListener) null);
	}

	public void onClick(DialogInterface dialog, int which) {

		int rt_day = 0;
		Log.i(TAG, "wv_year.getCurrentItem():" + wv_year.getCurrentItem() + " curr_year:"
		        + curr_year);
		int rt_year = wv_year.getCurrentItem() + curr_year;
		int rt_month;
		// 如果显示年份比今年年份要大，这使用滚轮的时间，否则用现在时间加上滚轮位置
		// TODO:修改了2014年月份的bug，但是不确定是否有更深的问题
		if (rt_year > curr_year) {
			rt_month = wv_month.getCurrentItem();
		} else {
			rt_month = wv_month.getCurrentItem() + curr_month;
		}

		if (rt_year == curr_year && rt_month == curr_month) { // 天数计算要注意
			rt_day = curr_day + wv_day.getCurrentItem();
		} else {
			rt_day = wv_day.getCurrentItem() + 1;
		}

		Log.i(TAG,
		        "rt_day:" + rt_day + " rt_year:" + rt_year + " rt_month:" + rt_month
		                + " curr_month" + curr_month + " wv_month.getCurrentItem():"
		                + wv_month.getCurrentItem());

		if (mCallBack != null) {
			mCallBack.onDateTimeSet(rt_year, rt_month, rt_day);
		}
	}

	public void show() {
		super.show();
	}

	public interface OnDateTimeSetListener {
		void onDateTimeSet(int year, int month, int day);
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
