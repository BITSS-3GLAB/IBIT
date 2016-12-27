package com.bitss.Digital_BIT.Bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Util.Constants;

/*
 * 新闻与服务器请求类，用于访问旧的新闻,新的新闻和新闻具体内容
 */

public class HttpBusAsker {
	private static final String TAG = HttpAsker.class.getSimpleName();
	private static final String serverBus = "GetSchoolBusData"; // 旧新闻端口

	// 外部需要访问
	public LinkedList<BusInfo> busData;
	public int date_type; // 当天的校车类型（工作日、周五、周末、节假日）

	private String[] place_name;
	private String date;
	private String start_place, end_place;
	private int sp, ap;

	public HttpBusAsker(Context mContext, String date, String start_place,
			String end_place) {
		this.start_place = start_place;
		this.end_place = end_place;
		this.date = date;

		place_name = mContext.getResources().getStringArray(R.array.bus_place);
		for (int i = 0; i < place_name.length; i++) {
			if (start_place.equals(place_name[i])) {
				sp = i + 1;
				Log.i(TAG, "sp:" + sp + ",start_place:" + start_place);
			}
			if (end_place.equals(place_name[i])) {
				ap = i + 1;
				Log.i(TAG, "ap:" + ap + ",end_place:" + end_place);
			}
		}
	}

	/**
	 * 返回类型：（0：表示网络请求成功，但是无新数据；-1：网络请求失败；大于0：有数据返回）
	 * */
	public int askForBusList() {

		JSONObject obj = new JSONObject();
		try {
			obj.put("date", date);
			System.out.println(date);
			obj.put("sp", sp);
			obj.put("ap", ap);
		} catch (JSONException e1) {
			return -1;
		}
		// 从服务器返回校车数据
		int ans = askForBus(serverBus, obj);
		return ans;
	}

	/**
	 * 返回类型：（0：表示网络请求成功，但是无新数据；-1：网络请求失败；大于0：有数据返回）
	 * */
	private int askForBus(String url, JSONObject obj) {
		busData = new LinkedList<BusInfo>();
		// 获取当前时间，比较此时校车是否已经开走
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String time = format.format(calendar.getTime());
		try {
			StringBuffer strResult = new StringBuffer();
			int tmpInt = HttpAsker
					.Asker(strResult, Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "servlet/" + url, obj); // 调用http请求工具
			if (tmpInt != 0) {
				strResult = new StringBuffer();
				tmpInt = HttpAsker.Asker(strResult,
						Constants.BITKNOWTEST_SERVER_STRING + "servlet/" + url,
						obj); // 调用http请求工具
				if (tmpInt != 0) {
					return -1; // 网络问题
				}

			}

			// 解析json，提取其中的数据
			JSONObject obj2 = new JSONObject(strResult.toString());
			System.out.println(strResult.toString());
			// 当天校车类型
			date_type = obj2.getInt("Type");
			int new_bus = obj2.getInt("NewBus");
			if (new_bus == 1) {
				JSONArray jsonArray = obj2.getJSONArray("Data");

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject tmpobj = jsonArray.getJSONObject(i);
					String startTime = (String) tmpobj.get("startTime");
					String endTime = (String) tmpobj.get("arriveTime");
					String busInfo = (String) tmpobj.get("seatMessage");

					int bus_status = 0;
					// 判断校车是否已经开走
					if (BusActivity.isItToday(BusActivity.date)) {
						int result = startTime.compareTo(time);

						if (result == 0) { // 校车即将出发，status = 1
							bus_status = 1;
						} else if (result > 0) { // 需要等待较长时间，status = 2
							bus_status = 2;
						}
					} else {
						bus_status = 2;
					}

					BusInfo bus = new BusInfo(busInfo, start_place, end_place,
							startTime, endTime, bus_status);
					busData.add(bus);
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return -1;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return busData.size();
	}
}
