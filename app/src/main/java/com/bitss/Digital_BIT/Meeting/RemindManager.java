package com.bitss.Digital_BIT.Meeting;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RemindManager {

	private RemindDBHelper helper = null;
	private SQLiteDatabase database = null;
	private boolean flag = false;
	private Context context;

	public RemindManager(Context context) {
		if (helper == null) {
			helper = new RemindDBHelper(context);
		}
		this.context = context;
	}

	/* 插入数据 */
	public long insertData(String name, String place, long time,
			long remind_time) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		ContentValues values = new ContentValues();
		values.put(RemindColumns.KEY_MEETING_NAME, name);
		values.put(RemindColumns.KEY_MEETING_PLACE, place);
		values.put(RemindColumns.KEY_MEETING_TIME, time);
		values.put(RemindColumns.KEY_MEETING_REMIND_TIME, remind_time);
		values.put(RemindColumns.KEY_IS_REMINDED, "n");

		long count = database.insert(RemindColumns.TABLE_NAME, null, values);

		if (flag == true) {
			database.close();
		}

		return count;
	}

	/**
	 * 获取所有提醒的记录
	 * */
	public List<RemindModel> getUnRemindList() {
		if (database == null || !database.isOpen()) {
			database = helper.getReadableDatabase();
			flag = true;
		}

		List<RemindModel> tempList = new ArrayList<RemindModel>();

		Cursor cursor = null;
		cursor = database.query(RemindColumns.TABLE_NAME, null,
				RemindColumns.KEY_IS_REMINDED + " = ? ", new String[] { "n" },
				null, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					String id = cursor.getString(cursor
							.getColumnIndex(RemindColumns.KEY_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(RemindColumns.KEY_MEETING_NAME));
					String place = cursor.getString(cursor
							.getColumnIndex(RemindColumns.KEY_MEETING_PLACE));
					long time = cursor.getLong(cursor
							.getColumnIndex(RemindColumns.KEY_MEETING_TIME));
					long remind_time = cursor
							.getLong(cursor
									.getColumnIndex(RemindColumns.KEY_MEETING_REMIND_TIME));

					RemindModel model = new RemindModel(id, name, place, time,
							remind_time);
					tempList.add(model);
				} while (cursor.moveToNext());
			}
		}
		if (cursor != null) {
			cursor.close();
		}

		if (flag) {
			database.close();
		}

		return tempList;
	}

	public RemindModel getRemindById(String rowId) {
		if (database == null || !database.isOpen()) {
			database = helper.getReadableDatabase();
			flag = true;
		}

		RemindModel model = null;

		Cursor cursor = null;
		cursor = database.query(RemindColumns.TABLE_NAME, null,
				RemindColumns.KEY_ID + " = ? ", new String[] { rowId }, null,
				null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					String id = cursor.getString(cursor
							.getColumnIndex(RemindColumns.KEY_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(RemindColumns.KEY_MEETING_NAME));
					String place = cursor.getString(cursor
							.getColumnIndex(RemindColumns.KEY_MEETING_PLACE));
					long time = cursor.getLong(cursor
							.getColumnIndex(RemindColumns.KEY_MEETING_TIME));
					long remind_time = cursor
							.getLong(cursor
									.getColumnIndex(RemindColumns.KEY_MEETING_REMIND_TIME));

					model = new RemindModel(id, name, place, time, remind_time);
				} while (cursor.moveToNext());
			}
		}
		if (cursor != null) {
			cursor.close();
		}

		if (flag) {
			database.close();
		}

		return model;
	}

	public boolean deleteData(String rowId) {
		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}
		boolean isDelete = false;

		database.delete(RemindColumns.TABLE_NAME, RemindColumns.KEY_ID + " =?",
				new String[] { rowId });
		isDelete = true;
		if (flag == true) {
			database.close();
		}
		return isDelete;
	}

}
