package com.bitss.Digital_BIT.News;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;

public class NewsListAdapter extends BaseAdapter {

	private Context activity;
	private boolean isAlumni; // 是否是校友网新闻
	private String newsName;
	private int newsType;
	private LinkedList<NewsData> data;
	private Map<String, Bitmap> pictureMap = new HashMap<String, Bitmap>();

	public NewsListAdapter(Context activity, boolean isAlumni, String newsName,
			int newsType, LinkedList<NewsData> data) {
		super();
		this.activity = activity;
		this.isAlumni = isAlumni;
		this.newsName = newsName;
		this.newsType = newsType;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// convertView缓存
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.news_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.itemLayout = (RelativeLayout) convertView
					.findViewById(R.id.listviewlayout);
			viewHolder.newtagButton = (ImageView) convertView
					.findViewById(R.id.newslistnewtag);
			viewHolder.titleView = (TextView) convertView
					.findViewById(R.id.newslisttitle);
			viewHolder.dateView = (TextView) convertView
					.findViewById(R.id.newslistdate);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 设置属性
		final NewsData news = data.get(position);
		viewHolder.titleView.setText(news.title);
		viewHolder.dateView.setText("发布于:" + news.pubtime);

		if (news.newTag)
			viewHolder.newtagButton.setVisibility(View.VISIBLE);
		else
			viewHolder.newtagButton.setVisibility(View.GONE);

		if (news.readTag == false) {
			viewHolder.titleView.setTextColor(Color.rgb(0, 0, 0));
		} else {
			viewHolder.titleView.setTextColor(Color.rgb(130, 130, 130));
		}

		viewHolder.itemLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, NewsReaderActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isAlumni", isAlumni); // 是否来自校友网
				bundle.putInt("NewsType", newsType);
				bundle.putString("Url", news.url);
				bundle.putString("NewsTitle", news.title);
				bundle.putString("NewsName", newsName);
				bundle.putString("NewsTime", news.pubtime);
				bundle.putLong("NewsID", news.id);
				intent.putExtras(bundle);

				news.newTag = false;
				news.readTag = true;
				notifyDataSetChanged();
				activity.startActivity(intent);
			}
		});

		return convertView;
	}

	private class ViewHolder {
		RelativeLayout itemLayout;
		ImageView newtagButton;
		TextView titleView;
		TextView dateView;
	}

}
