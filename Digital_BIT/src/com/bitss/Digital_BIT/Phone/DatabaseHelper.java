package com.bitss.Digital_BIT.Phone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	// ��ݿ���
	private static final String DATABASE_NAME = "phone.db";
	// ��ݿ�汾��
	private static final int DATABASE_VERSION = 1;
	// �?���ַ���
	public static final String TABLE_CREATE_PHONEINFO = "create table if not exists " 
			+ PhoneColumns.TABLE_NAME + " ( " 
			+ PhoneColumns.KEY_ID + " text primary key, "
			+ PhoneColumns.KEY_SID + " text not null, "
			+ PhoneColumns.KEY_PID + " text not null, "
			+ PhoneColumns.KEY_ISPEOPLE + " text not null, "
			+ PhoneColumns.KEY_DESCRIPTION + " text not null, "
			+ PhoneColumns.KEY_LOCATION + " text, "
			+ PhoneColumns.KEY_EMAIL + " text, "
			+ PhoneColumns.KEY_COMMENT + " text, "
			+ PhoneColumns.KEY_PHONENUM + " text " + ");";
	
	// ���캯��
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// ����ݿⴴ��ʱ������
	public void onCreate(SQLiteDatabase db){
		db.execSQL(TABLE_CREATE_PHONEINFO);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		/*
		db.execSQL("drop table if exists notes");
		db.execSQL(TABLE_CREATE_PHONEINFO);
		*/
	}

	public void onOpen(SQLiteDatabase db){
		super.onOpen(db);
	}
}
