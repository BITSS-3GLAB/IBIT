package com.bitss.Digital_BIT.BitKnow;

import java.util.List;

import com.bitss.Digital_BIT.BitKnow.model.BitKnowMessageData;
import com.bitss.Digital_BIT.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BitKnowMessageAdapter extends BaseAdapter {
	private Context context;
	private List<BitKnowMessageData> mData;
	
	public BitKnowMessageAdapter(Context context,List<BitKnowMessageData> mData) {
		this.context = context;
		this.mData = mData;
	}
	
	@Override
	public int getCount() {
		if(mData == null) return 0;
		else return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		
		if(holder == null)
		{
			holder = new ViewHolder();
			LayoutInflater flater = LayoutInflater.from(context);
			convertView = flater.inflate(R.layout.bitknow_message_adapter_item, null);
			holder.text = (TextView) convertView.findViewById(R.id.answer_list_item);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		String name = mData.get(position).getName();
		String text = mData.get(position).getText();
		
		holder.text.setText(name + "回答了这个问题" + "\"" + text +"\"");
		
		return convertView;
	}
	
	private class ViewHolder
	{
		TextView text;
	}
}
