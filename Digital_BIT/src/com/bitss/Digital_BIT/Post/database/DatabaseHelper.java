package com.bitss.Digital_BIT.Post.database;

import com.bitss.Digital_BIT.Util.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private volatile static DatabaseHelper mInstance = null;

	private static final String DATABASE_NAME = "BitWaterFall.db";
	private static final int DATABASE_VERSION = 1;

	// singleton pattern
	public static DatabaseHelper getInstance(Context context) {
		Logger.i("DatabaseHelper", "DatabaseHelper getInstance");
		if (mInstance == null) {
			synchronized (DatabaseHelper.class) {
				if (mInstance == null) {
					mInstance = new DatabaseHelper(
							context.getApplicationContext());
					Logger.i("BitWaterFall", "db is created");
				}
			}
		}
		return mInstance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE post_list(id INTEGER PRIMARY KEY, corid INTEGER, json TEXT);");
		db.execSQL("CREATE TABLE post_content(id INTEGER PRIMARY KEY, json TEXT);");
		db.execSQL("CREATE TABLE post_comment(id INTEGER PRIMARY KEY, annouid INTEGER,username TEXT, pubtime TEXT , content TEXT);");
		db.execSQL("CREATE TABLE hobby_group_list(id INTEGER PRIMARY KEY, postCount INTEGER, json TEXT);");
	}

}
