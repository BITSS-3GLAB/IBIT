package com.bitss.Digital_BIT.News;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.bitss.Digital_BIT.okhttp.request.Parameter;

/**
 * 疑问1：传进去的评论id是10，那还会返回10这条评论吗？并且返回的数据是否都是新的评论放在第一条 疑问2：添加评论返回的true或者false怎么取得
 * 
 * @author Administrator
 * 
 */
public class HttpCommentAsker {

	private static final String SERVER_SETCOMMENT = "servlet/SetNewsComment";
	private static final String SERVER_GETCOMMENT_STRING = "servlet/GetNewsComment";
	private static final String SERVER_GETCOMMENT_NUM = "servlet/GetNewsCommentNum";
	private static final String url = Constants.BITKNOWTEST_SERVER_STRING;
	private static final String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING;

	private LinkedList<CommentData> commentDatas;

	public int askForMobileList(Context ctx, LinkedList<CommentData> comList,
			long newsId, int page) {
		HttpRequest request = new HttpRequest.Builder()
				.url("/news/front/newsComment")
				.add(new Parameter("newsId", newsId))
				.add(new Parameter("page", page)).build();
		try {
			Response response = HttpClient.getInstance(ctx).get(
					request.getUrl());
			JSONArray array = new JSONArray(response.body().string());
			int i;
			for (i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				CommentData data = new CommentData("",
						obj.getString("content"), obj.getString("time"), "", 0,
						0, obj.getLong("id"));
				comList.add(data);
			}
			return i;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	// 获取评论列表的信息，但是有刷新和加载两种动作
	public int askForMobileList(LinkedList<CommentData> comList,
			boolean isAskPre, int askNumber, int newsType, long newsId,
			boolean isNew) {

		// 创建JSON包
		int newComentNumber = 0;
		JSONObject object = new JSONObject();
		try {
			// 当起始为空的时候，会显示最新的信息
			if (comList.size() == 0)
				object.put("FloorStart", -1);
			else if (isAskPre == false)
				object.put("FloorStart", comList.getFirst().id + 1);// 刷新，请求更新的评论，这是请求的起始楼层
			else if (isAskPre == true) {
				object.put("FloorStart", comList.getLast().id - 1);// 加载，请求旧的评论这是请求的起始楼层
			}

			/**
			 * 疑问1：JSONObject能不能为null
			 */

			object.put("NewsType", newsType);
			object.put("NewsID", newsId);

			object.put("IsAskPre", isAskPre);// true表示向前请求，false表示向后请求
			object.put("AskNum", askNumber);// 请求的评论的条数
			object.put("IsNew", isNew);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		try {
			StringBuffer strResult = new StringBuffer();
			// 请求云端服务器
			int tmpInt = HttpAsker.Asker(strResult, cloudurl
					+ SERVER_GETCOMMENT_STRING, object);
			if (tmpInt != 0) {
				strResult = new StringBuffer();
				// 请求本地服务器
				int tempINT = HttpAsker.Asker(strResult, url
						+ SERVER_GETCOMMENT_STRING, object);
				if (tempINT != 0) {
					return tmpInt;
				}
			}

			JSONObject object2 = new JSONObject(strResult.toString());

			JSONArray jsonArray = object2
					.getJSONArray("NewsCommentInfoJSONArray");

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
			e.printStackTrace();
			return -1;
		}
		// 返回新添加的评论的条数
		return newComentNumber;
	}

	/**
	 * 问题5：不知道传出来的评论的数目是放在什么格式里面，所以取不到
	 */
	public int askForCommentNumber(int typeId, long newsId, int number) {

		JSONObject object = new JSONObject();
		try {
			object.put("NewsType", typeId);
			object.put("NewsID", newsId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			StringBuffer strResult = new StringBuffer();
			// 请求云端服务器
			int tmpInt = HttpAsker.Asker(strResult, cloudurl
					+ SERVER_GETCOMMENT_NUM, object);
			if (tmpInt != 0) {
				strResult = new StringBuffer();
				// 请求本地服务器
				int tempINT = HttpAsker.Asker(strResult, url
						+ SERVER_GETCOMMENT_NUM, object);
				if (tempINT != 0) {
					return tmpInt;
				}
			}

			JSONObject object2 = new JSONObject(strResult.toString());
			number = object2.getInt("CommentNum");
			// number = object3.getInt("")
		} catch (Exception e) {
		}
		return 0;
	}

	// 将新添的评论信息传给服务器,返回的数值是用来判断是否成功地将新添的数据放到服务器中了
	public int putCommentToMobile(CommentData comList) throws JSONException {

		// 将数据存放到JSON包里面
		JSONObject object = new JSONObject();

		try {
			object.put("NewsType", comList.newsType);
			object.put("NewsID", comList.newsId);
			object.put("UserName", comList.username);
			object.put("CommentText", comList.content);
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}

		try {
			StringBuffer strResult = new StringBuffer();// 就是为了显示出错之后返回的信息
			// 请求云端服务器
			int tmpInt = HttpAsker.Asker(strResult, cloudurl
					+ SERVER_SETCOMMENT, object);
			if (tmpInt != 0) {
				strResult = new StringBuffer();
				// 请求本地服务器
				int tempINT = HttpAsker.Asker(strResult, url
						+ SERVER_SETCOMMENT, object);
				if (tempINT != 0) {
					return tmpInt;
				}
			}

			JSONObject object2 = new JSONObject(strResult.toString());
			if (object2.getBoolean("Success") != true)
				return -1;

		} catch (IllegalStateException e) {
			e.printStackTrace();
			return -1;
		}

		return 0;

	}

}
