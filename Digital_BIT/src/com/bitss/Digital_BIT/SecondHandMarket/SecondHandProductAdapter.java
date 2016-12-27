package com.bitss.Digital_BIT.SecondHandMarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SecondHandProductAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, Object>> mData;

	public SecondHandProductAdapter(Context context,
			ArrayList<HashMap<String, Object>> mData) {
		this.mData = mData;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (mData == null)
			return 0;
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {

		ViewHolder holder = null;

		if (convertView == null) {

		holder = new ViewHolder();
		LayoutInflater flater = LayoutInflater.from(context);
		convertView = flater.inflate(R.layout.secondhandmarket_item, null);

		holder.img_ObjImg = (ImageView) convertView
				.findViewById(R.id.secondhanditemImage);
		holder.txtVw_ObjName = (TextView) convertView
				.findViewById(R.id.secondhanditemTitle);
		
		convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		// 这里是把数据送到绑定好的地方
		
		HashMap<String, Object> map = mData.get(position);
		map.put("ItemImage", R.drawable.icon_bike);
		map.put("Name", "代步工具");

		Object aString = map.get("ItemImage");
		


		// convertView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // Bundle dataBundle = new Bundle();
		// // dataBundle.putInt("position", position);
		// // Intent intent = new Intent(context, DetailActivity.class);
		// // intent.putExtras(dataBundle);
		// // context.startActivity(intent);
		//
		// }
		// });

		return convertView;
	}

	private class ViewHolder {
		ImageView img_ObjImg;
		TextView txtVw_ObjName;
		TextView txtVw_ObjNameContent;
		TextView txtVw_ObjDescribe;
		TextView tv_price;
	}
}
