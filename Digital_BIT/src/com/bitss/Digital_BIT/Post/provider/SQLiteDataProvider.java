package com.bitss.Digital_BIT.Post.provider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.News.CommentData;
import com.bitss.Digital_BIT.Post.database.ActivityDB;
import com.bitss.Digital_BIT.Post.model.HobbyGroupInformation;
import com.bitss.Digital_BIT.Post.model.PostInformation;

import android.R.integer;
import android.content.Context;
import android.util.Log;

public class SQLiteDataProvider {

	private ActivityDB db = null;

	public SQLiteDataProvider(Context context) {
		this.db = (((BaseApplication) context.getApplicationContext())).getDB();
	}

	public List<PostInformation> getPostInformationList(long sinceId,
			long untilId, int count, long corId) {
		ArrayList<JSONObject> list = db.getPostInformationList(sinceId,
				untilId, count, corId);
		List<PostInformation> data = new ArrayList<PostInformation>(list.size());
		for (JSONObject json : list) {
			data.add(new PostInformation(json));
		}
		return data;
	}

	public void savePostInformationList(List<PostInformation> data) {
		db.savePostInformationsList(data);

	}

	public void savePostComment(LinkedList<CommentData> data) {
		db.saveComment(data);
	}

	public String getPostContent(long id) {
		return db.getPostContent(id);
	}

	public LinkedList<CommentData> getPostComment(long annouid, int count) {
		return db.getCommentData(annouid, count);
	}

	public void savePostContent(long id, String content) {
		db.savePostContent(id, content);
	}

	public void clearCache() {
		db.clearCache();
	}

	public List<HobbyGroupInformation> getHobbyGroupInformationList() {
		Log.i("SQliteDataProvider", "HobbyGroupList is gotten from db");
		ArrayList<JSONObject> list = db.getHobbyGroupInformationList();
		List<HobbyGroupInformation> data = new ArrayList<HobbyGroupInformation>(
				list.size());
		for (JSONObject json : list) {
			data.add(new HobbyGroupInformation(json));
		}
		return data;
	}

	public void saveHobbyGroupInformationList(List<HobbyGroupInformation> data) {
		Log.i("SQliteDataProvider", "HobbyGroupList is saved");
		db.saveHobbyGroupInformationsList(data);
	}

}
