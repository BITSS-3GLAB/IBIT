package com.bitss.Digital_BIT.Bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BusManager {

	private DBHelper helper = null;
	private SQLiteDatabase database = null;
	private boolean flag = false;
	private Context context;

	public BusManager(Context context) {
		if (helper == null) {
			helper = new DBHelper(context);
		}
		this.context = context;
	}

	/* 插入数据 */
	public long insertData(String rowId, String type, String startPoint,
			String aimPoint, String startTime, String arriveTime,
			String midStation, String seatMessage) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		ContentValues values = new ContentValues();
		values.put(BusColumns.KEY_ID, rowId);
		values.put(BusColumns.KEY_TYPE, type);
		values.put(BusColumns.KEY_STARTPOINT, startPoint);
		values.put(BusColumns.KEY_AIMPOINT, aimPoint);
		values.put(BusColumns.KEY_STARTTIME, startTime);
		values.put(BusColumns.KEY_ARRIVINGTIME, arriveTime);
		values.put(BusColumns.KEY_MIDSTATION, midStation);
		values.put(BusColumns.KEY_SEATMESSAGE, seatMessage);

		long count = database.insert(BusColumns.TABLE_NAME, null, values);

		if (flag == true) {
			database.close();
		}

		return count;
	}

	/**
	 * 输入参数（时间类型-起点-终点）
	 * */
	public LinkedList<BusInfo> getBusInfoList(String date_type,
			String start_Point, String end_Point) {

		LinkedList<BusInfo> list = new LinkedList<BusInfo>();

		if (date_type == null) {
			return list;
		} else if (date_type.equals("周五")) {
			// 周五班次（工作日+周五添加）
			list = queryFromDB("工作日", start_Point, end_Point);
			list.addAll(queryFromDB(date_type, start_Point, end_Point));
		} else {
			list = queryFromDB(date_type, start_Point, end_Point);
		}

		// 对list按出发时间进行排序
		ComparatorBus comparatorBus = new ComparatorBus();
		Collections.sort(list, comparatorBus);

		return list;
	}

	/**
	 * 参数（时间类型-起点-终点）
	 * */
	public LinkedList<BusInfo> queryFromDB(String type, String start_Point,
			String end_Point) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		LinkedList<BusInfo> list = new LinkedList<BusInfo>();

		Cursor cursor = null;
		cursor = database.rawQuery("select * from " + BusColumns.TABLE_NAME
				+ " where " + BusColumns.KEY_TYPE + " = ? and "
				+ BusColumns.KEY_STARTPOINT + " = ? and "
				+ BusColumns.KEY_AIMPOINT + " = ? ", new String[] { type,
				start_Point, end_Point });

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {

				// 获取当前时间，比较此时校车是否已经开走
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				String time = format.format(calendar.getTime());

				BusInfo data;
				int bus_status = 0;

				do {
					String start_time = cursor.getString(cursor
							.getColumnIndex(BusColumns.KEY_STARTTIME));
					String start_place = cursor.getString(cursor
							.getColumnIndex(BusColumns.KEY_STARTPOINT));
					String bus_info = cursor.getString(cursor
							.getColumnIndex(BusColumns.KEY_SEATMESSAGE));
					String end_place = cursor.getString(cursor
							.getColumnIndex(BusColumns.KEY_AIMPOINT));
					String end_time = cursor.getString(cursor
							.getColumnIndex(BusColumns.KEY_ARRIVINGTIME));


					// 判断校车是否已经开走
					if (BusActivity.isItToday(BusActivity.date)) {
						int result = start_time.compareTo(time);

						if (result == 0) { // 校车即将出发，status = 1
							bus_status = 1;
						} else if (result > 0) { // 需要等待较长时间，status = 2
							bus_status = 2;
						}
					} else {
						bus_status = 2;
					}
					

					data = new BusInfo(bus_info, start_place, end_place,
							start_time, end_time, bus_status);
					list.add(data);

				} while (cursor.moveToNext());
			}
		}

		if (cursor != null) {
			cursor.close();
		}

		if (flag) {
			database.close();
		}
		return list;
	}


}
