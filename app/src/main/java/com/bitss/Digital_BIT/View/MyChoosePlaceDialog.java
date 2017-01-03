package com.bitss.Digital_BIT.View;

import java.util.ArrayList;
import java.util.List;

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

public class MyChoosePlaceDialog extends AlertDialog implements OnClickListener {

	private WheelView wv_start;
	private WheelView wv_end;

	private final OnChooseSetListener mCallBack;
	private String[] starts = { "中关村", "良乡", "回龙观", "西三旗", "城铁良乡大学城北站" };
	private String[] ends = { "良乡", "回龙观", "西三旗" };
	private String ch_start, ch_end; // 选择的起点、终点

	public MyChoosePlaceDialog(Context context, OnChooseSetListener callback) {
		super(context);
		this.mCallBack = callback;

		View view = LayoutInflater.from(context).inflate(R.layout.layout_choose_place, null);
		wv_start = (WheelView) view.findViewById(R.id.start_place);
		wv_end = (WheelView) view.findViewById(R.id.end_place);
		wv_start.setAdapter(new ArrayWheelAdapter(starts));
		wv_end.setAdapter(new ArrayWheelAdapter(ends));

		int textSize = 0;
		textSize = adjustFontSize(context, getWindow().getWindowManager());
		wv_start.TEXT_SIZE = textSize;
		wv_end.TEXT_SIZE = textSize;

		setView(view);
		setButton("确定", this);
		setButton2("取消", (OnClickListener) null);

		wv_start.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				ch_start = starts[newValue];
				ends = ableChoose(ch_start);
				wv_end.setAdapter(new ArrayWheelAdapter(ends));
			}
		});

	}

	/**
	 * 可以选择的地址
	 * 
	 * @param place
	 *            :当前选择的地址
	 * @return 对于当前的place，可选择的地址
	 * */
	public String[] ableChoose(String start) {
		List<String> placeList = new ArrayList<String>();

		if (start.equals(starts[4])) { // 起点为地铁，终点只有一个
			placeList.add(starts[1]);
		} else {
			for (String place : starts) {
				if (!place.equals(start)) {
					placeList.add(place);
				}
			}

			if (!start.equals(starts[1])) { // 如果不是良乡，目的地不应该有地铁
				placeList.remove(placeList.size() - 1);
			}
		}

		return placeList.toArray(new String[placeList.size()]);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		ch_start = starts[wv_start.getCurrentItem()];
		ch_end = ends[wv_end.getCurrentItem()];
		if (mCallBack != null) {
			mCallBack.onChooseSet(ch_start, ch_end);
		}
	}

	public interface OnChooseSetListener {
		void onChooseSet(String start, String end);
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
