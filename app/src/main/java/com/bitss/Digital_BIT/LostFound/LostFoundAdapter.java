package com.bitss.Digital_BIT.LostFound;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class LostFoundAdapter extends BaseAdapter {

	private Context context;
	private LinkedList<LostFoundModel> dateList;
	private DisplayImageOptions displayImageOptions;

	public LostFoundAdapter(Context context, LinkedList<LostFoundModel> dateList) {
		this.context = context;
		this.dateList = dateList;

		displayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.uil_ic_stub)
				.showImageForEmptyUri(R.drawable.uil_ic_empty)
				.showImageOnFail(R.drawable.uil_ic_error)
				.resetViewBeforeLoading(false)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.displayer(new FadeInBitmapDisplayer(200)).cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.build();
	}

	@Override
	public int getCount() {
		return dateList.size();
	}

	@Override
	public Object getItem(int position) {
		return dateList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_lost_found, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.img = (ImageView) convertView.findViewById(R.id.lf_img);
			viewHolder.desTv = (TextView) convertView
					.findViewById(R.id.lf_describe);
			viewHolder.timeTv = (TextView) convertView
					.findViewById(R.id.lf_time);
			viewHolder.phoneTv = (TextView) convertView
					.findViewById(R.id.lf_phone);
			viewHolder.placeTv = (TextView) convertView
					.findViewById(R.id.lf_place);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final LostFoundModel model = dateList.get(position);
		viewHolder.img.setVisibility(View.GONE);
		if (model.url != null) {
			viewHolder.img.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(model.url, viewHolder.img,
					displayImageOptions);
			// 点击显示大图
			viewHolder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							DetailPhotoActivity.class);
					intent.putExtra("photo_url", model.url);
					context.startActivity(intent);
				}
			});
		}
		viewHolder.desTv.setText(model.desc);
		viewHolder.placeTv.setText(model.loc);
		viewHolder.phoneTv.setText(model.cont);
		viewHolder.timeTv.setText(model.time);

		return convertView;
	}

	private class ViewHolder {
		ImageView img;
		TextView desTv;
		TextView timeTv;
		TextView placeTv;
		TextView phoneTv;
	}

}
