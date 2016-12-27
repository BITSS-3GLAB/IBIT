package com.bitss.Digital_BIT.Phone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *  Class <code>PhoneInfoManager</code>信息管理类，通过操作数据库管理在信息表
 *
 */
public class PhoneInfoManager {
	private DatabaseHelper helper = null;
	private SQLiteDatabase database = null;
	private boolean flag = false;
	public PhoneInfoManager(Context context){
		if(helper == null){
			helper = new DatabaseHelper(context);
		}
	}
	
	/**
	 * 插入数据
	 * @param rowId
	 * @param sid
	 * @param pid
	 * @param isPeople
	 * @param description
	 * @param location
	 * @param email
	 * @param comment
	 * @param phone
	 * @return long count
	 */
	public long insertData(String rowId, String sid, String pid, String isPeople, 
			String description, String location, String email, String comment, String phone){
		
		if(database == null || !database.isOpen()){
			database = helper.getWritableDatabase();
			flag = true;
		}
		
		ContentValues values = new ContentValues();
		values.put(PhoneColumns.KEY_ID, rowId);
		values.put(PhoneColumns.KEY_SID, sid);
		values.put(PhoneColumns.KEY_PID, pid);
		values.put(PhoneColumns.KEY_ISPEOPLE, isPeople);
		values.put(PhoneColumns.KEY_DESCRIPTION, description);
		values.put(PhoneColumns.KEY_LOCATION, location);
		values.put(PhoneColumns.KEY_EMAIL, email);
		values.put(PhoneColumns.KEY_COMMENT, comment);
		values.put(PhoneColumns.KEY_PHONENUM, phone);
		
		// the parameter count is the id of the newly inserted row.
		long count = database.insert(PhoneColumns.TABLE_NAME, null, values);
		
		if(flag == true){
			database.close();
		}
		
		return count;
	}
	
	/**
	 *  删除数据
	 * @param rowId
	 * @return boolean isDelete
	 */
	public boolean deleteData(String rowId){
		if(database == null || !database.isOpen()){
			database = helper.getWritableDatabase();
			flag = true;
		}
		boolean isDelete = false;
		database.delete(PhoneColumns.TABLE_NAME,
				PhoneColumns.KEY_ID + " =?", new String[]{rowId});
		isDelete = true;
		if(flag == true){
			database.close();
		}
		return isDelete;
	}
	
	/**
	 *  更新数据
	 * @param rowId
	 * @param sid
	 * @param pid
	 * @param isPeople
	 * @param description
	 * @param location
	 * @param email
	 * @param comment
	 * @param phone
	 * @return 
	 */
	public boolean updateData(String rowId, String sid, String pid, String isPeople, 
			String description, String location, String email, String comment, String phone){
		
		if(database == null || !database.isOpen()){
			database = helper.getWritableDatabase();
			flag = true;
		}
		
		ContentValues values = new ContentValues();
		values.put(PhoneColumns.KEY_ID, rowId);
		values.put(PhoneColumns.KEY_SID, sid);
		values.put(PhoneColumns.KEY_PID, pid);
		values.put(PhoneColumns.KEY_ISPEOPLE, isPeople);
		values.put(PhoneColumns.KEY_DESCRIPTION, description);
		values.put(PhoneColumns.KEY_LOCATION, location);
		values.put(PhoneColumns.KEY_EMAIL, email);
		values.put(PhoneColumns.KEY_COMMENT, comment);
		values.put(PhoneColumns.KEY_PHONENUM, phone);
		
		database.update(PhoneColumns.TABLE_NAME, values,
				PhoneColumns.KEY_ID + " =?", new String[]{rowId});
		
		if(flag == true){
			database.close();
		}
		
		return true;
	}
	
	/**
	 * 获取所有pid为asPid的记录，返回结果集
	 * 另:单位逻辑层次结构依照学校行政组织结构
	 * @param asPid
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getDataListByPid(String asPid){
		if(database == null || !database.isOpen()){
			database = helper.getReadableDatabase();
			flag = true;
		}
		
		List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
		
		Cursor cursor = null;
		cursor = database.query(PhoneColumns.TABLE_NAME, 
				null, PhoneColumns.KEY_PID + " = ? ", new String[] {asPid}, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				do {
					Map<String, Object> map = new HashMap<String, Object>();
						
						String rowId = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ID));
						map.put(PhoneColumns.KEY_ID, rowId);
						
						String sid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_SID));
						map.put(PhoneColumns.KEY_SID, sid);
						
						String pid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PID));
						map.put(PhoneColumns.KEY_PID, pid);
						
						String isPeople = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ISPEOPLE));
						map.put(PhoneColumns.KEY_ISPEOPLE, isPeople);

						String description = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_DESCRIPTION));
						map.put(PhoneColumns.KEY_DESCRIPTION, description);

						String location = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_LOCATION));
						map.put(PhoneColumns.KEY_LOCATION, location);

						String email = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_EMAIL));
						map.put(PhoneColumns.KEY_EMAIL, email);

						String comment = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_COMMENT));
						map.put(PhoneColumns.KEY_COMMENT, comment);

						String phone = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PHONENUM));
						map.put(PhoneColumns.KEY_PHONENUM, phone);
						
						tempList.add(map);
					} while (cursor.moveToNext());
			}
		}
		if(cursor != null){
			cursor.close();
		}
		
		if(flag){
			database.close();
		}
		
		return tempList;
	}
	
	/**
	 * 根据输入的字符串在当前界面查询，如果未得到正确的结果，则返回一个特殊的list以便鉴别
	 * @param asPid,保证查询范围是当前界面
	 * @param input，用户输入
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getDataListByInput(String asPid, String input){
		if(database == null || !database.isOpen()){
			database = helper.getReadableDatabase();
			flag = true;
		}
		
		List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();

		Cursor cursor = null;
		cursor = database.rawQuery("select * from phoneInfo where description like '%" + input + "%' and " + PhoneColumns.KEY_PID + " = ? ",
				new String[] { asPid });
		if(cursor != null && cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				do {
					Map<String, Object> map = new HashMap<String, Object>();
						
						String rowId = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ID));
						map.put(PhoneColumns.KEY_ID, rowId);
						
						String sid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_SID));
						map.put(PhoneColumns.KEY_SID, sid);
						
						String pid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PID));
						map.put(PhoneColumns.KEY_PID, pid);
						
						String isPeople = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ISPEOPLE));
						map.put(PhoneColumns.KEY_ISPEOPLE, isPeople);

						String description = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_DESCRIPTION));
						map.put(PhoneColumns.KEY_DESCRIPTION, description);

						String location = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_LOCATION));
						map.put(PhoneColumns.KEY_LOCATION, location);

						String email = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_EMAIL));
						map.put(PhoneColumns.KEY_EMAIL, email);

						String comment = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_COMMENT));
						map.put(PhoneColumns.KEY_COMMENT, comment);

						String phone = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PHONENUM));
						map.put(PhoneColumns.KEY_PHONENUM, phone);
						
						tempList.add(map);
					} while (cursor.moveToNext());
			}
		}
		else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isWrong", "wrong");
			tempList.add(map);
		}
		if(cursor != null){
			cursor.close();
		}
		
		if(flag){
			database.close();
		}
		
		return tempList;
	}
	
	/**
	 * 根据输入的字符串进行全面查询，如果未得到正确的结果，则返回一个特殊的list以便鉴别
	 * @param input，用户输入
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getDataListByInput(String input){
		if(database == null || !database.isOpen()){
			database = helper.getReadableDatabase();
			flag = true;
		}
		
		List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();

		Cursor cursor = null;
		cursor = database.rawQuery("select * from phoneInfo where description like '%" + input + "%'", null);
		if(cursor != null && cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				do {
					Map<String, Object> map = new HashMap<String, Object>();
						
						String rowId = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ID));
						map.put(PhoneColumns.KEY_ID, rowId);
						
						String sid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_SID));
						map.put(PhoneColumns.KEY_SID, sid);
						
						String pid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PID));
						map.put(PhoneColumns.KEY_PID, pid);
						
						String isPeople = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ISPEOPLE));
						map.put(PhoneColumns.KEY_ISPEOPLE, isPeople);

						String description = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_DESCRIPTION));
						map.put(PhoneColumns.KEY_DESCRIPTION, description);

						String location = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_LOCATION));
						map.put(PhoneColumns.KEY_LOCATION, location);

						String email = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_EMAIL));
						map.put(PhoneColumns.KEY_EMAIL, email);

						String comment = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_COMMENT));
						map.put(PhoneColumns.KEY_COMMENT, comment);

						String phone = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PHONENUM));
						map.put(PhoneColumns.KEY_PHONENUM, phone);
						
						tempList.add(map);
					} while (cursor.moveToNext());
			}
		}
		else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isWrong", "wrong");
			tempList.add(map);
		}
		if(cursor != null){
			cursor.close();
		}
		
		if(flag){
			database.close();
		}
		
		return tempList;
	}
	
	/**
	 *  根据sid进行查询，返回sid为asSid的记录，其结果集只包含一个元素
	 * @param asSid
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getDataListBySid(String asSid){
		if(database == null || !database.isOpen()){
			database = helper.getReadableDatabase();
			flag = true;
		}
		
		List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
		
		Cursor cursor = null;
		cursor = database.query(PhoneColumns.TABLE_NAME, 
				null, PhoneColumns.KEY_SID + " = ? ", new String[] {asSid}, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				do {
					Map<String, Object> map = new HashMap<String, Object>();
						
						String rowId = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ID));
						map.put(PhoneColumns.KEY_ID, rowId);
						
						String sid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_SID));
						map.put(PhoneColumns.KEY_SID, sid);
						
						String pid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PID));
						map.put(PhoneColumns.KEY_PID, pid);
						
						String isPeople = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_ISPEOPLE));
						map.put(PhoneColumns.KEY_ISPEOPLE, isPeople);

						String description = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_DESCRIPTION));
						map.put(PhoneColumns.KEY_DESCRIPTION, description);

						String location = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_LOCATION));
						map.put(PhoneColumns.KEY_LOCATION, location);

						String email = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_EMAIL));
						map.put(PhoneColumns.KEY_EMAIL, email);

						String comment = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_COMMENT));
						map.put(PhoneColumns.KEY_COMMENT, comment);

						String phone = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PHONENUM));
						map.put(PhoneColumns.KEY_PHONENUM, phone);
						
						tempList.add(map);
					} while (cursor.moveToNext());
			}
		}
		if(cursor != null){
			cursor.close();
		}
		
		if(flag){
			database.close();
		}
		
		return tempList;
	}
	
	/**
	 * 查询一个sid是否在pid字段中存在，即一个id是否有子id
	 * @param idString
	 * @return boolean
	 */
	public boolean isIdHolding(String idString){
		if(database == null || !database.isOpen()){
			database = helper.getReadableDatabase();
			flag = true;
		}
		
		boolean a = false;
		
		Cursor cursor = null;
		cursor = database.query(PhoneColumns.TABLE_NAME, 
				null, PhoneColumns.KEY_PID + " = ? ", new String[] {idString}, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			a = true;
		}
		else {
			a = false;
		}
		if(cursor != null){
			cursor.close();
		}
		
		if(flag){
			database.close();
		}
		
		return a;
	}
	
	/**
	 *  查询一个pid所对应的所有sid，获取所有子节点的sid，将sid等字段放入结果集中
	 * @param pidString
	 * @return List<Map<String, Object>>
	 */
		public List<Map<String, Object>> getSidByPid(String pidString){
			if(database == null || !database.isOpen()){
				database = helper.getReadableDatabase();
				flag = true;
			}
			
			List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
			
			Cursor cursor = null;
			cursor = database.query(PhoneColumns.TABLE_NAME, 
					null, PhoneColumns.KEY_PID + " = ? ", new String[] {pidString}, null, null, null);
			if(cursor != null && cursor.getCount() > 0){
				if(cursor.moveToFirst()){
					do {
						Map<String, Object> map = new HashMap<String, Object>();
						
						String sid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_SID));
						map.put(PhoneColumns.KEY_SID, sid);
						
						String pid = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_PID));
						map.put(PhoneColumns.KEY_PID, pid);
						
						String description = cursor.getString(cursor
								.getColumnIndex(PhoneColumns.KEY_DESCRIPTION));
						map.put(PhoneColumns.KEY_DESCRIPTION, description);
						
						tempList.add(map);
					} while (cursor.moveToNext());
				}
			}
			if(cursor != null){
				cursor.close();
			}
			
			if(flag){
				database.close();
			}
			
			return tempList;
		}
}
