package com.bitss.Digital_BIT.SecondHandMarket;

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

public class SecondHandMarketListViewAdapter extends BaseAdapter {
	private Context context;
	private List<SecondHandMarketData> mData;

	public SecondHandMarketListViewAdapter(Context context,
			List<SecondHandMarketData> mData) {
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

//		if (convertView == null) {

			holder = new ViewHolder();
			LayoutInflater flater = LayoutInflater.from(context);
			convertView = flater.inflate(R.layout.second_hand_listview_item, null);

			holder.img_ObjImg = (ImageView) convertView
					.findViewById(R.id.secondhanditemImage);
			holder.txtVw_ObjName = (TextView) convertView
					.findViewById(R.id.secondhanditemTitle);
			holder.txtVw_ObjNameContent = (TextView) convertView
					.findViewById(R.id.secondhanditemText1);
			holder.txtVw_ObjDescribe = (TextView) convertView
					.findViewById(R.id.secondhanditemText2);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.secondhandPrice);
			convertView.setTag(holder);
//
//		} else {
//
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//		// 这里是把数据送到绑定好的地方

		String aString = Constants.BITKNOWTEST_CLOUDSERVER_STRING
				+ (String) mData.get(position).getUrl1();
		ImageLoader.getInstance().displayImage(
				Constants.BITKNOWTEST_CLOUDSERVER_STRING
				+ (String) mData.get(position).getUrl1(),
				holder.img_ObjImg);
		holder.txtVw_ObjName.setText((String) mData.get(position).getTitle());
		if (mData.get(position).getCampus().equals("0"))
			holder.txtVw_ObjNameContent.setText("中关村校区");
		else {
			holder.txtVw_ObjNameContent.setText("良乡校区");
		}
		holder.txtVw_ObjDescribe
		.setText((String) mData.get(position).getTime());
		holder.tv_price
		.setText(mData.get(position).getPrice().toString() + "元");

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
