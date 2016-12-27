package com.bitss.Digital_BIT.Admission.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bitss.Digital_BIT.R;

public class AdmissionDBManager {

	private AdmissionDBHelper helper = null;
	private SQLiteDatabase database = null;
	private boolean flag = false;
	private Context context;

	public AdmissionDBManager(Context context) {
		if (helper == null) {
			helper = new AdmissionDBHelper(context);
		}
		this.context = context;
	}

	/**
	 * 
	 * 获取招生计划
	 * 
	 * @param year
	 * @param province
	 * @param type
	 *            ：理工或者文史
	 * 
	 * @return ArrayList<ArrayList<String>>
	 * */
	public ArrayList<ArrayList<String>> getAdmissionPlan(String _year, String _province,
	        String _type) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		Cursor cursor = null;
		cursor = database.rawQuery("select * from " + AdmissionColumns.Plan.TABLE_NAME + " where "
		        + AdmissionColumns.Plan.KEY_YEAR + " = ? and " + AdmissionColumns.Plan.KEY_PROVINCE
		        + " = ? and " + AdmissionColumns.Plan.KEY_TYPE + " = ? " + " order by sort asc ", new String[] {
		        _year, _province, _type });

		if (cursor != null && cursor.getCount() > 0) {

			// 表的第一行
			ArrayList<String> tableTitle = new ArrayList<String>();
			tableTitle.add(context.getString(R.string.str_admission_major));
			tableTitle.add(context.getString(R.string.str_admission_type));
			tableTitle.add(context.getString(R.string.str_admission_number));
			tableTitle.add(context.getString(R.string.str_admission_level));
			tableTitle.add(context.getString(R.string.str_admission_tuition));
			tableTitle.add(context.getString(R.string.str_admission_lasts));

			result.add(tableTitle);

			if (cursor.moveToFirst()) {
				do {
					String major = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Plan.KEY_MAJOR));
					String type = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Plan.KEY_TYPE));
					String lasts = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Plan.KEY_LASTS));
					String level = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Plan.KEY_LEVEL));
					String tuition = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Plan.KEY_TUITION));
					String number = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Plan.KEY_NUMBER));

					ArrayList<String> row = new ArrayList<String>();
					row.add(major);
					row.add(type);
					row.add(number);
					row.add(level);
					row.add(tuition);
					row.add(lasts);
					result.add(row);
				} while (cursor.moveToNext());
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

	/**
	 * 
	 * 历年分数
	 * 
	 * @param year
	 * @param province
	 * @param type
	 *            ：理工或者文史
	 * */
	public ArrayList<ArrayList<String>> getAdmissionScore(String _year, String _province,
	        String _type) {

		if (database == null || !database.isOpen()) {
			database = helper.getWritableDatabase();
			flag = true;
		}

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		Cursor cursor = null;
		cursor = database.rawQuery("select * from " + AdmissionColumns.Score.TABLE_NAME + " where "
		        + AdmissionColumns.Score.KEY_YEAR + " = ? and "
		        + AdmissionColumns.Score.KEY_PROVINCE + " = ? and "
		        + AdmissionColumns.Score.KEY_TYPE + " = ? ",
		        new String[] { _year, _province, _type });

		if (cursor != null && cursor.getCount() > 0) {
			// 表的第一行
			ArrayList<String> tableTitle = new ArrayList<String>();
			tableTitle.add(context.getString(R.string.str_admission_major));
			tableTitle.add(context.getString(R.string.str_admission_type));
			tableTitle.add(context.getString(R.string.str_admission_high));
			tableTitle.add(context.getString(R.string.str_admission_low));
			tableTitle.add(context.getString(R.string.str_admission_lasts));
			result.add(tableTitle);

			if (cursor.moveToFirst()) {
				do {
					String major = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Score.KEY_MAJOR));
					String type = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Score.KEY_TYPE));
					String lasts = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Score.KEY_LASTS));
					String high = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Score.KEY_HIGH));
					String low = cursor.getString(cursor
					        .getColumnIndex(AdmissionColumns.Score.KEY_LOW));

					ArrayList<String> row = new ArrayList<String>();
					row.add(major);
					row.add(type);
					row.add(high);
					row.add(low);
					row.add(lasts);
					result.add(row);
				} while (cursor.moveToNext());
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
