package com.bitss.Digital_BIT.Post.adapter;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Post.model.PostInformation;
import com.bitss.Digital_BIT.Util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostListAdapterTemp extends BaseAdapter {

	private Activity activity = null;
	private List<PostInformation> data = null;
	private BaseApplication mApp = null;

	public PostListAdapterTemp(Activity activity, List<PostInformation> data) {
		super();
		this.activity = activity;
		this.data = data;
		this.mApp = (BaseApplication) (activity.getApplicationContext());
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
		return data.get(position).getPostId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// convertView缓存
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.row_staggered, null);
			viewHolder = new ViewHolder();
			viewHolder.ivPost = (ImageView) convertView
					.findViewById(R.id.iv_post);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.tvLocation = (TextView) convertView
					.findViewById(R.id.tv_location);
			viewHolder.tvHost = (TextView) convertView
					.findViewById(R.id.tv_host);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// TODO
		final PostInformation item = data.get(position);
		// 200是width
		int actural_height = Utils.getImgActuralHeight(mApp, item.getPostHeight(), item.getPostWidth());

		// 设置view的大小，因为AsynctaskImage和view的自动销毁会产生位置计算的问题，所以一定要固定view的高度再加载图片
		viewHolder.ivPost.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, actural_height));

		// 根据position,将对应的内容复制设置属性
		String url = item.getPostImageUri();
		// ImageSize minImageSize = new ImageSize();
		ImageLoader.getInstance().displayImage(url, viewHolder.ivPost,
				mApp.getdisplayImageOptions());

		viewHolder.tvTime.setText(item.getPostTimestamp());
		viewHolder.tvLocation.setText(item.getPostLocation());
		viewHolder.tvHost.setText(item.getPostHost());
		viewHolder.tvTitle.setText(item.getPostTitle());
		return convertView;
	}

	private class ViewHolder {
		ImageView ivPost;
		TextView tvTitle;
		TextView tvTime;
		TextView tvLocation;
		TextView tvHost;
	}

}
