package com.bitss.Digital_BIT.News;

import java.util.LinkedList;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.R.id;
import com.bitss.Digital_BIT.Util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentListAdapter extends BaseAdapter {

	private LinkedList<CommentData> data;
	private Activity activity;

	public CommentListAdapter(Activity activity, LinkedList<CommentData> data) {
		this.activity = activity;
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.comment_item, parent, false);

			viewHolder.userNameView = (TextView) convertView
					.findViewById(R.id.txtUserName);
			viewHolder.timeView = (TextView) convertView
					.findViewById(R.id.txtTime);
			viewHolder.commentView = (TextView) convertView
					.findViewById(R.id.txtcommentBody);
			viewHolder.personView = (ImageView) convertView
					.findViewById(R.id.imgPeople);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CommentData comment = data.get(position);
		String name = comment.username;
		if (TextUtils.isEmpty(name)) {
			name = "匿名";
		}
		viewHolder.userNameView.setText(name);
		viewHolder.timeView.setText(comment.pubtime);
		viewHolder.commentView.setText(comment.content);
		if (!data.get(position).peopleUrl.equals("")) {
			ImageLoader.getInstance().displayImage(
					Constants.TEST_SERVER_STRING
							+ (String) data.get(position).peopleUrl,
					viewHolder.personView);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView userNameView;
		TextView timeView;
		TextView commentView;
		ImageView personView;
	}

}
