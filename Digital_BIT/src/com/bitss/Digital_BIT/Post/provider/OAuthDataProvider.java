package com.bitss.Digital_BIT.Post.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.News.CommentData;
import com.bitss.Digital_BIT.Post.model.HobbyGroupInformation;
import com.bitss.Digital_BIT.Post.model.PostInformation;
import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Tools.ParamUtil;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.RWToServer;

import android.R.string;
import android.content.Context;
import android.util.Log;

public class OAuthDataProvider {
	private final static String TAG = "OAuthDataProvider";

	// private Context context;

	public OAuthDataProvider(Context context) {
		// this.context = context;
	}

	private static boolean firstTime = true;

	public List<PostInformation> getPostInformationList(long sinceId,
			long untilId, int count, long corId) throws IOException,
			JSONException {
		String url = String
				.format("%s/servlet/GetPostInformationList?sinceId=%s&untilId=%s&count=%s&corId=%s",
						Constants.SERVER_URL, sinceId, untilId, count, corId);

		String jsonArrayString = RWToServer.doHttpGet(url);

		Log.i(TAG, url);
		Log.i(TAG, jsonArrayString);

		JSONArray jsonArray = new JSONObject(jsonArrayString)
				.getJSONArray("PostInformationList");
		// jsonArray = new
		// JSONObject(jsonArrayString).getJSONArray("postinformation");

		List<PostInformation> list = new ArrayList<PostInformation>(
				jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(new PostInformation(jsonArray.getJSONObject(i)));
		}

		return list;
	}

	public int getCommentList(LinkedList<CommentData> comList, long postId,
			int floorStart, boolean isAskPre, boolean isAskNew, int askNum) {
		int newComentNumber = 0;
		// TODO Merge Diff
		// String url = ParamUtil.SERVER_URL + "GetAnnounceComment";
		String url = ParamUtil.SERVER_COMMENT + "GetAnnounceComment";
		JSONObject object = new JSONObject();

		try {
			object.put("AnnounceID", postId);
			if (comList.size() == 0)
				object.put("FloorStart", -1);
			else if (isAskPre == false)
				object.put("FloorStart", comList.getFirst().id + 1);// 刷新，请求更新的评论，这是请求的起始楼层
			else if (isAskPre == true) {
				object.put("FloorStart", comList.getLast().id - 1);// 加载，请求旧的评论这是请求的起始楼层
			}
			object.put("IsNew", isAskNew);
			object.put("IsAskPre", isAskPre);
			object.put("AskNum", askNum);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			String strResult = "";
			try {
				strResult = RWToServer.doHttpPost(url, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject object2 = new JSONObject(strResult.toString());
			// TODO Merge Diff
			JSONArray jsonArray = object2
					.getJSONArray("AnnounceCommentInfoJSONArray");

			if (isAskPre == false) {
				for (int i = 0; i < jsonArray.length(); i++) {
					newComentNumber++;
					JSONObject object3 = jsonArray.getJSONObject(i);

					comList.addFirst(new CommentData(object3
							.getString("UserName"), object3
							.getString("CommentText"), object3
							.getString("PubTime"), "", 0, 0, object3
							.getInt("FloorLevel")));
				}
			} else {
				for (int i = jsonArray.length() - 1; i >= 0; i--) {
					newComentNumber++;
					JSONObject object3 = jsonArray.getJSONObject(i);
					comList.add(new CommentData(object3.getString("UserName"),
							object3.getString("CommentText"), object3
									.getString("PubTime"), "", 0, 0, object3
									.getInt("FloorLevel")));
				}
			}

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
		// 返回新添加的评论的条数
		return newComentNumber;

	}

	public int putCommentTo(long annouId, String username, String content)
			throws JSONException {
		JSONObject object = new JSONObject();
		String url = ParamUtil.SERVER_COMMENT + "SetAnnounceComment";
		try {
			object.put("AnnounceID", annouId);
			object.put("UserName", username);
			object.put("CommentText", content);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		try {
			String strResult = "";// 就是为了显示出错之后返回的信息
			try {
				strResult = RWToServer.doHttpPost(url, object);
				JSONObject object2 = new JSONObject(strResult.toString());
				if (object2.getBoolean("Success") != true)
					return -1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}

			JSONObject object2 = new JSONObject(strResult.toString());
			if (object2.getBoolean("Success") != true)
				return -1;

		} catch (IllegalStateException e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}

		return 0;

	}

	public String getPostContent(long id) throws IOException {
		String url = String.format("%s/servlet/GetNewsContent?id=%s",
				Constants.SERVER_URL, id);
		String jsonArrayString = RWToServer.doHttpGet(url);

		Logger.d(TAG, url);
		Logger.d(TAG, jsonArrayString);

		return jsonArrayString;
	}

	public List<HobbyGroupInformation> getHobbyGroupInformationList(
			long sinceId, int count) throws IOException, JSONException {
		String url = String.format("%s/servlet/GetCorInfoList",
				Constants.SERVER_URL);

		// TODO
		String jsonArrayString = RWToServer.doHttpGet(url);
		// initJSONArrayString;
		// RWToServer.doHttpGet(url);
		Log.i(TAG, url);
		Log.i(TAG, jsonArrayString);

		JSONArray jsonArray = new JSONObject(jsonArrayString)
				.getJSONArray("CorInfoList");

		List<HobbyGroupInformation> list = new ArrayList<HobbyGroupInformation>(
				jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(new HobbyGroupInformation(jsonArray.getJSONObject(i)));
		}

		return list;
	}

	private final String initJSONArrayString = "{"
			+ "HobbyGroupInformationList:"
			+ "["
			+ "{"
			+ "hobby_group_id: 0,"
			+ "hobby_group_post_count: 10,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\""
			+ "},"
			+ "{"
			+ "hobby_group_id: 1,"
			+ "hobby_group_post_count: 9,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\""
			+ "},"
			+ "{"
			+ "hobby_group_id: 2,"
			+ "hobby_group_post_count: 3,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\""
			+ "},"
			+ "{"
			+ "hobby_group_id: 3,"
			+ "hobby_group_post_count: 4,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\""
			+ "},"
			+ "{"
			+ "hobby_group_id: 4,"
			+ "hobby_group_post_count: 0,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\""
			+ "},"
			+ "{"
			+ "hobby_group_id: 5,"
			+ "hobby_group_post_count: 0,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\""
			+ "},"
			+ "{"
			+ "hobby_group_id: 6,"
			+ "hobby_group_post_count: 55,"
			+ "hobby_group_image_uri: \"http://img3.douban.com/bpic/o634821.jpg\","
			+ "hobby_group_name: \"京工新闻社\"" + "}" + "]" + "}";

}
