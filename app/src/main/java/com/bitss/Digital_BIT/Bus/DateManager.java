package com.bitss.Digital_BIT.Bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DateManager {

	private DBHelper helper = null;
	private SQLiteDatabase database = null;
	private boolean flag = false;
	private Context context;

	public DateManager(Context context) {
		if (helper == null) {
			helper = new DBHelper(context);
		}
		this.context = context;
	}

	/* 插入数据 */
	public void insertData(String date, int type) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		ContentValues values = new ContentValues();
		values.put(DateColumns.BUS_DATE, date);
		values.put(DateColumns.DATE_TYPE, type);

		database.insert(DateColumns.TABLE_NAME, null, values);

		if (flag == true) {
			database.close();
		}
	}

	/**
	 * 获取日期对应的类型
	 * */
	public String getDateType(Calendar date) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(date.getTime());

		String result = null;
		Cursor cursor = null;
		//出错

		cursor = database.rawQuery("select " + DateColumns.DATE_TYPE + " from "
				+ DateColumns.TABLE_NAME + " where " + DateColumns.BUS_DATE
				+ " = ? ", new String[] { dateString });

		if (cursor != null && cursor.getCount() > 0) {

			cursor.moveToFirst();
			int type = cursor.getInt(cursor.getColumnIndex(DateColumns.DATE_TYPE));
			switch (type) {
			case 1:
				result = "节假日";
				break;
			case 2:
				result = "周末";
				break;
			case 3:
				result = "周五";
				break;
			case 4:
				result = "工作日";
				break;
			}
		}
		//数据库没有2014年以后的校车，超过后计算
		if(cursor.getCount() ==0){
			int year = Integer.valueOf(dateString.substring(0,4)).intValue();
			int mouth = Integer.valueOf(dateString.substring(5,7)).intValue();
			int day = Integer.valueOf(dateString.substring(8,10)).intValue();
			for(;;day=day-7){
				if(day<=0){
					if(mouth==2||mouth==4||mouth==6||mouth==8||mouth==9||mouth==11||mouth==1){
						day = day+31;
						mouth--;
					}
					else if(mouth==5||mouth==7||mouth==10||mouth==12){
						day = day+30;
						mouth--;
					}
					else if(mouth==3){
						if(year%4==0){
							day = day+29;
							mouth--;
						}
						else {
							day = day+28;
							mouth--;
						}
					}
				}
				if(mouth==0){
					mouth=12;
					year--;
				}
				if(year<=2014&&mouth<=11){
					break;
				}
			}
			dateString = ""+year+"-"+mouth+"-"+day;
			cursor = database.rawQuery("select " + DateColumns.DATE_TYPE + " from "
					+ DateColumns.TABLE_NAME + " where " + DateColumns.BUS_DATE
					+ " = ? ", new String[] { dateString });

			if (cursor != null && cursor.getCount() > 0) {

				cursor.moveToFirst();
				int type = cursor.getInt(cursor.getColumnIndex(DateColumns.DATE_TYPE));
				switch (type) {
				case 1:
					result = "节假日";
					break;
				case 2:
					result = "周末";
					break;
				case 3:
					result = "周五";
					break;
				case 4:
					result = "工作日";
					break;
				}
			}
		}
		if (cursor != null) {
			cursor.close();
		}
		if (flag) {
			database.close();
		}

		return result;
	}

}
