package com.bitss.Digital_BIT.Post.database;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.News.CommentData;
import com.bitss.Digital_BIT.Post.model.HobbyGroupInformation;
import com.bitss.Digital_BIT.Post.model.PostInformation;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class ActivityDB {

	private static final String TAG = "ActivityDB";

	private volatile static ActivityDB mInstance = null;

	private DatabaseHelper mHelper = null;

	// singleton pattern
	public static ActivityDB getInstance(Context context) {
		if (mInstance == null) {
			synchronized (ActivityDB.class) {
				if (mInstance == null) {
					mInstance = new ActivityDB(context.getApplicationContext());
				}
			}
		}
		return mInstance;
	}

	private ActivityDB(Context context) {
		this.mHelper = DatabaseHelper.getInstance(context);
	}

	public void close() {
		if (mHelper != null) {
			mHelper.close();
		}
	}

	public ArrayList<JSONObject> getPostInformationList(long sinceId,
			long untilId, int count, long corId) {
		Log.i("ActivityDB", "codID:" + corId);
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = mHelper.getReadableDatabase();

			if (Constants.POST_ALL == corId) {
				if (untilId == 0) {
					String[] args = new String[2];
					args[0] = Long.toString(sinceId);
					args[1] = Integer.toString(count);
					cursor = db
							.rawQuery(
									"select json from post_list where id > ? order by id desc limit ?",
									args);
				} else {
					String[] args = new String[3];
					args[0] = Long.toString(sinceId);
					args[1] = Long.toString(untilId);
					args[2] = Integer.toString(count);
					cursor = db
							.rawQuery(
									"select json from post_list where id > ? and id <= ? order by id desc limit ?",
									args);
				}
			} else {
				if (untilId == 0) {
					String[] args = new String[3];
					args[0] = Long.toString(sinceId);
					args[1] = Long.toString(corId);
					args[2] = Integer.toString(count);
					cursor = db
							.rawQuery(
									"select json from post_list where id > ? and corid = ? order by id desc limit ?",
									args);
				} else {
					String[] args = new String[4];
					args[0] = Long.toString(sinceId);
					args[1] = Long.toString(untilId);
					args[2] = Long.toString(corId);
					args[3] = Integer.toString(count);
					cursor = db
							.rawQuery(
									"select json from post_list where id > ? and id <= ? and corid = ? order by id desc limit ?",
									args);
				}
			}

			Logger.d(TAG, "get post information list size:" + cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {
						JSONObject json = new JSONObject(cursor.getString(0));
						if (json != null) {
							list.add(json);
						}
					} while (cursor.moveToNext());
				}
			}

		} catch (JSONException e) {
			Logger.d(TAG,
					"[getPostInformationList] JSONException." + e.toString());
		} catch (SQLiteException e) {
			Logger.d(TAG,
					"[getPostInformationList] SQLiteException." + e.toString());
		} catch (Exception e) {
			Logger.d(TAG, "[getPostInformationList] Exception." + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	public void savePostInformationsList(List<PostInformation> data) {
		Logger.i("ActivityDB", "data.size:" + data.size());
		if (data != null && data.size() > 0) {
			try {
				SQLiteDatabase db = mHelper.getWritableDatabase();
				db.beginTransaction();
				long maxId = data.get(0).getPostId();
				long minId = data.get(data.size() - 1).getPostId();
				Logger.i("ActivityDB", "maxId:" + maxId);
				Logger.i("ActivityDB", "minId:" + minId);
				db.execSQL("delete from post_list where id>=? and id<=?",
						new Object[] { minId, maxId });
				for (int i = 0; i < data.size(); i++) {
					JSONObject postInformation = data.get(i).getJSONData();
					Object[] args = new Object[3];
					args[0] = postInformation.getLong("post_id");
					args[1] = postInformation.getLong("cor_id");
					args[2] = postInformation.toString();
					db.execSQL(
							"replace into post_list(id, corid, json) values(?, ?, ?)",
							args);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			} catch (JSONException e) {
				Logger.d(
						TAG,
						"[savePostInformationList] JSONException."
								+ e.toString());
			} catch (SQLiteException e) {
				Logger.d(
						TAG,
						"[savePostInformationList] SQLiteException."
								+ e.toString());
			} catch (Exception e) {
				Logger.d(TAG,
						"[savePostInformationList] Exception." + e.toString());
			}
		}

	}

	public void saveComment(LinkedList<CommentData> list) {
		if (list != null && list.size() > 0) {
			try {
				SQLiteDatabase db = mHelper.getWritableDatabase();
				db.beginTransaction();
				long maxId = list.get(0).id;
				long minId = list.get(list.size() - 1).id;
				Logger.i("ActivityDB", "maxId:" + maxId);
				Logger.i("ActivityDB", "minId:" + minId);
				db.execSQL("delete from post_comment where id>=? and id<=?",
						new Object[] { minId, maxId });
				for (int i = 0; i < list.size(); i++) {

					Object[] args = new Object[5];
					args[0] = list.get(i).id;
					args[1] = list.get(i).newsId;
					args[2] = list.get(i).username;
					args[3] = list.get(i).pubtime;
					args[4] = list.get(i).content;
					db.execSQL(
							"replace into post_comment(id, annouid,username,pubtime,content) values(?, ?, ?, ?, ?)",
							args);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			} catch (SQLiteException e) {
				Logger.d(TAG, "[saveCommentInformationList] SQLiteException."
						+ e.toString());
			} catch (Exception e) {
				Logger.d(
						TAG,
						"[saveCommentInformationList] Exception."
								+ e.toString());
			}
		}
	}

	public LinkedList<CommentData> getCommentData(long annouid, int count) {
		LinkedList<CommentData> list = new LinkedList<CommentData>();
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = mHelper.getReadableDatabase();

			String[] args = new String[2];
			args[0] = Long.toString(annouid);
			args[1] = Integer.toString(count);
			cursor = db
					.rawQuery(
							"select id , username,pubtime , content  from post_comment where annouid = ? order by id desc limit ? offset 0",
							args);

			Logger.d(TAG, "get post information list size:" + cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {
						String _username = cursor.getString(cursor
								.getColumnIndex("username"));
						String _content = cursor.getString(cursor
								.getColumnIndex("content"));
						String _pubtime = cursor.getString(cursor
								.getColumnIndex("pubtime"));
						long _id = Integer.parseInt(cursor.getString(cursor
								.getColumnIndex("id")));
						list.add(new CommentData(_username, _content, _pubtime,
								"", annouid, 0, _id));

					} while (cursor.moveToNext());
				}
			}

		} catch (SQLiteException e) {
			Logger.d(TAG,
					"[getPostInformationList] SQLiteException." + e.toString());
		} catch (Exception e) {
			Logger.d(TAG, "[getPostInformationList] Exception." + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;

	}

	public String getPostContent(long id) {
		String postContent = "";
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] args;

		try {
			db = mHelper.getReadableDatabase();
			args = new String[1];
			args[0] = Long.toString(id);
			cursor = db.rawQuery("select json from post_content where id = ?",
					args);

			if (cursor != null && cursor.getCount() == 1) {
				if (cursor.moveToFirst()) {
					postContent = cursor.getString(0);
				}
			}
		} catch (SQLiteException e) {
			Logger.d(TAG, "[getPostContent] SQLiteException." + e.toString());
		} catch (Exception e) {
			Logger.d(TAG, "[getPostContent] Exception." + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return postContent;
	}

	public void savePostContent(long id, String content) {
		try {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			Object[] args = new Object[2];
			args[0] = id;
			args[1] = content;
			db.execSQL("replace into post_content(id, json) values(?, ?)", args);
		} catch (SQLiteException e) {
			Logger.d(TAG, "[savePostContent] SQLiteException." + e.toString());
		} catch (Exception e) {
			Logger.d(TAG, "[savePostContent] Exception." + e.toString());
		}

	}

	public void clearCache() {
		try {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			db.execSQL("delete from post_list;");
			db.execSQL("delete from post_content;");
		} catch (SQLiteException e) {
			Logger.d(TAG, "[clearCache] SQLiteException." + e.toString());
		} catch (Exception e) {
			Logger.d(TAG, "[clearCache] Exception." + e.toString());
		}

	}

	// TODO
	// 在每次按返回键的时候保存内容，默认清空表里所有的内容，将当前内容存到表里
	public void saveHobbyGroupInformationsList(List<HobbyGroupInformation> data) {
		Logger.i("ActivityDB", "data.size:" + data.size());
		if (data != null && data.size() > 0) {
			try {
				SQLiteDatabase db = mHelper.getWritableDatabase();
				db.beginTransaction();
				db.execSQL("delete from hobby_group_list");
				for (int i = 0; i < data.size(); i++) {
					JSONObject postInformation = data.get(i).getJSONData();
					Object[] args = new Object[3];
					args[0] = postInformation.getLong("CorID");
					Log.i("ActivityDB", "args[0]:" + args[0]);
					args[1] = postInformation.getLong("PubNum");
					Log.i("ActivityDB", "args[1]:" + args[1]);
					args[2] = postInformation.toString();
					Log.i("ActivityDB", "args[2]:" + args[2]);
					db.execSQL(
							"replace into hobby_group_list(id, postCount, json) values(?, ?, ?)",
							args);
				}
				db.setTransactionSuccessful();
				db.endTransaction();
			} catch (JSONException e) {
				Logger.d(TAG, "[saveHobbyGroupInformationList] JSONException."
						+ e.toString());
			} catch (SQLiteException e) {
				Logger.d(
						TAG,
						"[saveHobbyGroupInformationList] SQLiteException."
								+ e.toString());
			} catch (Exception e) {
				Logger.d(
						TAG,
						"[saveHobbyGroupInformationList] Exception."
								+ e.toString());
			}
		}

	}

	public ArrayList<JSONObject> getHobbyGroupInformationList() {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = mHelper.getReadableDatabase();
			String sql = "select json from hobby_group_list order by postCount desc";
			cursor = db.rawQuery(sql, null);

			Logger.d(
					TAG,
					"get hobby group information list size:"
							+ cursor.getCount());

			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {
						JSONObject json = new JSONObject(cursor.getString(0));
						if (json != null) {
							list.add(json);
						}
					} while (cursor.moveToNext());
				}
			}

		} catch (JSONException e) {
			Logger.d(TAG,
					"[getPostInformationList] JSONException." + e.toString());
		} catch (SQLiteException e) {
			Logger.d(TAG,
					"[getPostInformationList] SQLiteException." + e.toString());
		} catch (Exception e) {
			Logger.d(TAG, "[getPostInformationList] Exception." + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}
}
