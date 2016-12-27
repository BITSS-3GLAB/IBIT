package com.bitss.Digital_BIT.Bus;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;

public class BusAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private LinkedList<BusInfo> dataList;

	public BusAdapter(Context context, LinkedList<BusInfo> dataList) {
		this.inflater = LayoutInflater.from(context);
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
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
			convertView = inflater.inflate(R.layout.buslistlayout, null);
			viewHolder = new ViewHolder();

			viewHolder.start_time = (TextView) convertView
					.findViewById(R.id.bustimestart);
			viewHolder.start_place = (TextView) convertView
					.findViewById(R.id.busplacestart);
			viewHolder.end_time = (TextView) convertView
					.findViewById(R.id.bustimeend);
			viewHolder.end_place = (TextView) convertView
					.findViewById(R.id.busplaceend);
			viewHolder.bus_info = (TextView) convertView
					.findViewById(R.id.businformation);
			viewHolder.wait = (ImageView) convertView
					.findViewById(R.id.buspeople1);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 设置数据
		BusInfo data = dataList.get(position);

		viewHolder.start_time.setText(data.startTime);
		viewHolder.start_place.setText(data.startPlace);

		String bus_info = data.busInfo;
		// if (bus_info.startsWith("T")) { // 临时车次，带有车的信息
		// viewHolder.bus_info.setTextColor(android.graphics.Color.RED);
		// viewHolder.bus_info
		// .setText(bus_info.substring(1, bus_info.length()));
		// } else {
		// if (bus_info.equals("(临时车次)")) {
		// viewHolder.bus_info.setTextColor(android.graphics.Color.RED);
		// } else {
		// viewHolder.bus_info.setTextColor(android.graphics.Color.BLACK);
		// }
		// viewHolder.bus_info.setText(bus_info);
		// }
		int busState = data.busStatus;
		if (busState == 0) {// 已发车
			viewHolder.bus_info.setTextColor(android.graphics.Color.GRAY);
		} else {
			viewHolder.bus_info.setTextColor(android.graphics.Color.BLACK);
		}
		viewHolder.bus_info.setText(getBusStateString(busState) + bus_info);

		viewHolder.end_time.setText(data.endTime);
		viewHolder.end_place.setText(data.endPlace);

		int bus_status = data.busStatus;
		if (bus_status == 0) { // 校车已开走
			viewHolder.wait.setVisibility(View.INVISIBLE);
		} else { // 需要等待
			viewHolder.wait.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	private class ViewHolder {
		TextView start_time;
		TextView start_place;
		TextView bus_info;
		TextView end_time;
		TextView end_place;

		ImageView wait;
	}

	public static String getBusStateString(int i) {
		if (i == 0) {
			return "已发车 ";
		} else if (i == 1) {
			return "即将要开 ";
		} else {
			return "未发车 ";
		}
	}

}
