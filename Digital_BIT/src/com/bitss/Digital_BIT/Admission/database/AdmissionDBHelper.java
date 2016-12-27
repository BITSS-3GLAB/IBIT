package com.bitss.Digital_BIT.Admission.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdmissionDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "admission.db";
	private static final int DATABASE_VERSION = 1;

	// 招聘计划表
	private final String TABLE_CREATE_ADMISSION_PLAN = "create table if not exists "
			+ AdmissionColumns.Plan.TABLE_NAME
			+ " ( "
			+ AdmissionColumns.Plan.KEY_YEAR
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_PROVINCE
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_MAJOR
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_TYPE
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_LEVEL
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_TUITION
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_NUMBER
			+ " text not null, "
			+ AdmissionColumns.Plan.KEY_LASTS + " text not null" + ");";

	private final String TABLE_CREATE_ADMISSION_SCORE = "create table if not exists "
			+ AdmissionColumns.Score.TABLE_NAME
			+ " ( "
			+ AdmissionColumns.Score.KEY_YEAR
			+ " text not null, "
			+ AdmissionColumns.Score.KEY_PROVINCE
			+ " text not null, "
			+ AdmissionColumns.Score.KEY_MAJOR
			+ " text not null, "
			+ AdmissionColumns.Score.KEY_TYPE
			+ " text not null, "
			+ AdmissionColumns.Score.KEY_LASTS
			+ " text not null, "
			+ AdmissionColumns.Score.KEY_HIGH
			+ " text not null, "
			+ AdmissionColumns.Score.KEY_LOW + " text not null" + ");";

	public AdmissionDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_ADMISSION_PLAN);
		db.execSQL(TABLE_CREATE_ADMISSION_SCORE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

}
