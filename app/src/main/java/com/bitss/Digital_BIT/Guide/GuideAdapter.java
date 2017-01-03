package com.bitss.Digital_BIT.Guide;

import java.util.HashMap;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class GuideAdapter extends PagerAdapter {

	private Context context;
	private HashMap<Integer, GuideItemView> mHashMap;

	public GuideAdapter(Context context) {
		this.context = context;
		mHashMap = new HashMap<Integer, GuideItemView>();
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		return;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		GuideItemView itemView;
		if (mHashMap.containsKey(position)) {
			itemView = mHashMap.get(position);
		} else {
			itemView = new GuideItemView(context, position);
			mHashMap.put(position, itemView);
			((ViewPager) container).addView(itemView);
		}

		return itemView;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

}
