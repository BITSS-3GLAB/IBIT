package com.bitss.Digital_BIT.Bus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "bus.db";
	private static final int DATABASE_VERSION = 1;

	private final String TABLE_CREATE_BUSINFO = "create table if not exists "
			+ BusColumns.TABLE_NAME + " ( " + BusColumns.KEY_ID
			+ " integer primary key, " + BusColumns.KEY_TYPE
			+ " text not null, " + BusColumns.KEY_STARTPOINT
			+ " text not null, " + BusColumns.KEY_AIMPOINT + " text not null, "
			+ BusColumns.KEY_STARTTIME + " text not null, "
			+ BusColumns.KEY_ARRIVINGTIME + " text, "
			+ BusColumns.KEY_MIDSTATION + " text, "
			+ BusColumns.KEY_SEATMESSAGE + " text " + ");";

	private final String TABLE_CREATE_DATEINFO = "create table if not exists "
			+ DateColumns.TABLE_NAME + " ( " + DateColumns.BUS_DATE
			+ " text primary key, " + DateColumns.DATE_TYPE + " integer not null "
			+ ");";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_BUSINFO);
		db.execSQL(TABLE_CREATE_DATEINFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

}
