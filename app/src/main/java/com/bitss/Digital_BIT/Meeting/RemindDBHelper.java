package com.bitss.Digital_BIT.Meeting;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RemindDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "remind.db";

	private static final int DATABASE_VERSION = 1;

	private final String TABLE_CREATE = "create table if not exists "
			+ RemindColumns.TABLE_NAME
			+ " ( " + RemindColumns.KEY_ID
			+ " integer primary key autoincrement, "
			+ RemindColumns.KEY_MEETING_NAME
			+ " text , "
			+ RemindColumns.KEY_MEETING_PLACE
			+ " text , "
			+ RemindColumns.KEY_MEETING_TIME
			+ " text , "
			+ RemindColumns.KEY_MEETING_REMIND_TIME
			+ " text , "
			+ RemindColumns.KEY_IS_REMINDED
			+ " text " + ");";

	public RemindDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
