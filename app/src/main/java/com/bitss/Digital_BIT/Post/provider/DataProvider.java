package com.bitss.Digital_BIT.Post.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.bitss.Digital_BIT.Post.model.HobbyGroupInformation;
import com.bitss.Digital_BIT.Post.model.PostContent;
import com.bitss.Digital_BIT.Post.model.PostInformation;
import com.bitss.Digital_BIT.Util.Logger;

import android.content.Context;

public class DataProvider {

	private volatile static DataProvider mInstance = null;

	private final SQLiteDataProvider sqlite;
	private final OAuthDataProvider oauth;

	// singleton pattern
	public static DataProvider getInstance(Context context) {
		if (mInstance == null) {
			synchronized (DataProvider.class) {
				if (mInstance == null) {
					mInstance = new DataProvider(
							context.getApplicationContext());
				}
			}
		}
		return mInstance;
	}

	private DataProvider(Context context) {
		this.sqlite = new SQLiteDataProvider(context);
		this.oauth = new OAuthDataProvider(context);
	}

	public SQLiteDataProvider getSQLiteDataProvider() {
		return sqlite;
	}

	public OAuthDataProvider getOAuthDataProvider() {
		return oauth;
	}

	/*
	 * public PostInformation getPostInformation(long id) throws IOException,
	 * JSONException { PostInformation postInformation = null; return
	 * postInformation; }
	 */

	public List<PostInformation> getPostInformationList(long sinceId,
			long untilId, int count, long corId) throws IOException,
			JSONException {
		List<PostInformation> list = oauth.getPostInformationList(sinceId,
				untilId, count, corId);
		Logger.i("DataProvider", "list.size():" + list.size());
		if (-1 == corId) {
			sqlite.savePostInformationList(list);
		}

		return list;
	}

	public List<PostContent> getPostContent(long id) throws IOException,
			JSONException {
		String jsonArrayString = sqlite.getPostContent(id);

		JSONArray jsonArray = new JSONArray();

		if (jsonArrayString.equals("")) {
			jsonArrayString = oauth.getPostContent(id);
			jsonArray = new JSONArray(jsonArrayString);
			// 如果返回的不是jsonArray字符串，上面一行语句会抛异常
			// 那么不会执行以下语句，不会保存到数据库中
			sqlite.savePostContent(id, jsonArrayString);
		} else {
			jsonArray = new JSONArray(jsonArrayString);
		}

		List<PostContent> list = new ArrayList<PostContent>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(new PostContent(jsonArray.getJSONObject(i)));
		}

		return list;
	}

	public List<PostInformation> getPostInitData(int count, long corID) {
		List<PostInformation> list = this.getSQLiteDataProvider()
				.getPostInformationList(0, 0, count, corID);
		Logger.i("DataProvider", "list.size():" + list.size());
		return list;
	}

	public List<HobbyGroupInformation> getHobbyGroupInitData() {
		List<HobbyGroupInformation> list = this.getSQLiteDataProvider()
				.getHobbyGroupInformationList();
		Logger.i("DataProvider",
				"getHobbyGroupInitData list.size():" + list.size());
		return list;
	}

	public List<HobbyGroupInformation> getHobbyGroupInformationList(
			long sinceId, int count) throws IOException, JSONException {
		List<HobbyGroupInformation> list = oauth.getHobbyGroupInformationList(
				sinceId, count);
		Logger.i("DataProvider", "list.size():" + list.size());
		sqlite.saveHobbyGroupInformationList(list);

		return list;
	}

	public void clearCache() {
		sqlite.clearCache();
	}

}
