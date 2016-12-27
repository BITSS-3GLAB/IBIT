package com.bitss.Digital_BIT.Personal;

import java.util.List;

import com.bitss.Digital_BIT.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {

	private List<Message> mData;
	private Context context;
	
	public MessageListAdapter(Context ctx, List<Message> data) {
		context = ctx;
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		Message message = mData.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_message_list, parent, false);
			holder = new ViewHolder();
			
			holder.mTvContent = (TextView) convertView.findViewById(R.id.tv_content);
			holder.mTvTime = (TextView) convertView.findViewById(R.id.tv_time);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mTvContent.setText(message.getContent());
		holder.mTvTime.setText(message.getTime());
		
		return convertView;
	}

	private static class ViewHolder {
		TextView mTvContent;
		TextView mTvTime;
	}
}
