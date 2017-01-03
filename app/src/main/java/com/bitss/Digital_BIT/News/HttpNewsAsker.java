package com.bitss.Digital_BIT.News;

import java.io.IOException;
import java.util.LinkedList;

import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.okhttp.HttpClient;

/*
 * 新闻与服务器请求类，用于访问旧的新闻,新的新闻和新闻具体内容
 */

public class HttpNewsAsker {
	// 校友汇的接口

	private LinkedList<NewsData> newData; // 新闻列表数据
	private boolean type; // 新闻是否为新的标识
	private Context context;

	public HttpNewsAsker(Context ctx) {
		this.context = ctx;
		newData = new LinkedList<NewsData>();
	}

	// // -------------------------------校友报--------------------------------
	// /**
	// * 请求校友报的新闻列表
	// *
	// * @param:isNewList = true 获取新数据，false获取久数据
	// * */
	// public int askForMobileList(LinkedList<NewsData> newsList, boolean
	// isNewList) {
	// // 创建JSON
	// JSONObject obj = new JSONObject();
	// try {
	// long sinceId = 0, untilId = 0;
	//
	// if (isNewList) { // 获取新数据
	// if (newsList.size() == 0) {
	// sinceId = 0;
	// untilId = 0;
	// } else {
	// sinceId = newsList.get(0).id;
	// untilId = 0;
	// }
	// } else {
	// if (newsList.size() == 0) {
	// sinceId = 0;
	// untilId = 0;
	// } else {
	// sinceId = 0;
	// untilId = newsList.get(newsList.size() - 1).id;
	// }
	// }
	//
	// obj.put("sinceId", sinceId);
	// obj.put("untilId", untilId);
	// obj.put("count", 10);
	// } catch (JSONException e1) {
	// e1.printStackTrace();
	// return -1;
	// }
	// // 分析校友报返回的列表信息
	// int ans = extractForMobileList(serverMobile, obj);
	// if (ans >= 0) {
	// for (int i = newData.size() - 1; i >= 0; i--)
	// // 将数据写入
	// newsList.add(newData.get(i));
	// }
	// return ans;
	// }

	// /**
	// * 分析校友报新闻列表返回的字段
	// * */
	// private int extractForMobileList(String url, JSONObject obj) {
	// newData = new LinkedList<NewsData>();
	// try {
	// StringBuffer strResult = new StringBuffer();
	// // int tmpInt = HttpAsker.Asker(strResult, url, obj); // 调用http请求工具
	// // if (tmpInt != 0)
	// // return tmpInt;
	// // 请求云端服务器
	// int tmpInt = HttpAsker
	// .Asker(strResult, Constants.BITKNOWTEST_CLOUDSERVER_STRING
	// + "servlet/" + url, obj);
	// if (tmpInt != 0) {
	// strResult = new StringBuffer();
	// // 请求本地服务器
	// int tempINT = HttpAsker.Asker(strResult,
	// Constants.BITKNOWTEST_SERVER_STRING + "servlet/" + url,
	// obj);
	// if (tempINT != 0) {
	// return tmpInt;
	// }
	// }
	// System.out.printf(strResult.toString());
	// JSONArray jsonArray = new JSONArray(strResult.toString());
	//
	// for (int i = jsonArray.length() - 1; i >= 0; i--) {
	// JSONObject tmpobj = jsonArray.getJSONObject(i);
	//
	// long id = (int) tmpobj.getLong("id");
	// String title = (String) tmpobj.get("title");
	// String summary = (String) tmpobj.get("summary");
	// String publish_time = (String) tmpobj.get("publish_time");
	// String url = (String) tmpobj.get("");
	// NewsData data = new NewsData(title, publish_time, type, false,
	// id, url);
	// newData.add(data);
	// }
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// return -1;
	// } catch (JSONException e) {
	// e.printStackTrace();
	// return -1;
	// }
	// return newData.size();
	// }

	/**
	 * 请求旧新闻，传入参数： Parameter:新闻数据列表，新闻类型。得到结果最后写入新闻数据列表中
	 * 
	 * */
	public int askForOldList(LinkedList<NewsData> newsList, int newsType,
			int page) {
		type = false; // 新闻为旧

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.SERVER_URL).append("/news/front/newsList/")
				.append(newsType).append("?page=").append(page);
		String url = sb.toString();
		int ans = askForNews(url); // 从服务器请求新闻
		if (ans >= 0) {
			for (int i = newData.size() - 1; i >= 0; i--)
				// 将数据写入
				newsList.add(newData.get(i));
		}
		return ans;
	}

	/**
	 * 请求最新新闻，传入参数：新闻数据列表，新闻类型。 得到结果最后写入新闻数据列表中
	 * 
	 * @param isAlumni
	 *            :是不是校友汇
	 * @param newsType
	 *            :新闻类型
	 * */
	public int askForNewsList(boolean isAlumni, LinkedList<NewsData> newsList,
			int newsType) {
		type = true;
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.SERVER_URL).append("/news/front/newsList/")
				.append(newsType).append("?page=1");
		String url = sb.toString();

		int ans = 0;
		ans = askForNews(url);

		if (ans >= 0) {
			newsList.clear();
			for (int i = 0; i < newData.size(); i++)
				newsList.addFirst(newData.get(i));
		}
		return ans;
	}

	private int askForNews(String url) {
		newData.clear();
		try {
			Response response = HttpClient.getInstance(context).get(url);
			String data = response.body().string();
			JSONArray jsonArray = new JSONArray(data);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				NewsData news = new NewsData(obj.getString("title"),
						obj.getString("pubTime"), type, false,
						obj.getLong("id"), obj.getString("url"));
				newData.add(news);
			}
			return newData.size();
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	// /**
	// * 请求新闻方法，传入参数url和json，将新闻列表解析后传回，并返回新闻数量
	// * */
	// private int askForNews(String url, JSONObject obj) {
	// newData = new LinkedList<NewsData>();
	// try {
	// StringBuffer strResult = new StringBuffer();
	// // int tmpInt = HttpAsker.Asker(strResult, url, obj); // 调用http请求工具
	// // if (tmpInt != 0)
	// // return tmpInt;
	// int tmpInt = HttpAsker
	// .Asker(strResult, Constants.BITKNOWTEST_CLOUDSERVER_STRING
	// + "servlet/" + url, obj);
	// if (tmpInt != 0) {
	// strResult = new StringBuffer();
	// // 请求本地服务器
	// int tempINT = HttpAsker.Asker(strResult,
	// Constants.BITKNOWTEST_SERVER_STRING + "servlet/" + url,
	// obj);
	// if (tempINT != 0) {
	// return tmpInt;
	// }
	// }
	//
	// JSONObject obj2 = new JSONObject(strResult.toString());
	// JSONArray jsonArray = obj2.getJSONArray("NewsInfo");
	//
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject tmpobj = jsonArray.getJSONObject(i);
	// String title = (String) tmpobj.get("NewsTitle");
	// String content = (String) tmpobj.get("NewsContent");
	// String pubtime = (String) tmpobj.get("Pubtime");
	// long newsId = tmpobj.getLong("NewsId");
	// NewsData data = new NewsData(title, content, pubtime, type,
	// false, newsId);
	// newData.add(data);
	// }
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// return -1;
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return -1;
	// }
	// return newData.size();
	// }
	//
	// /**
	// * 请求新闻具体内容，传入参数：具体内容列表，新闻类型，新闻id
	// *
	// * @param isAlumni
	// * :是否来自校友网，请求地址不一样
	// * */
	// public NewsContentData askForContent(boolean isAlumni, int newsType,
	// long newsID) {
	// if (newsType == 14) { // 请求校友会的具体新闻
	// return forMobileContent(newsType, newsID);
	// } else {
	// return forContent(isAlumni, newsType, newsID);
	// }
	// }
	//
	// /**
	// * 获取非校友报的新闻
	// * */
	//
	// public String askForurl(int newsType, long newsID) {
	// String url = serverNewsContent;
	// String webUrl = "";
	// JSONObject obj = new JSONObject();
	// try {
	// obj.put("sourceID", newsType);
	// obj.put("NewsID", newsID);
	// } catch (JSONException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// try {
	// StringBuffer strResult = new StringBuffer();
	// int tmpInt = HttpAsker
	// .Asker(strResult, Constants.BITKNOWTEST_CLOUDSERVER_STRING
	// + "servlet/" + url, obj);
	// if (tmpInt != 0) {
	// strResult = new StringBuffer();
	// // 请求本地服务器
	// int tempINT = HttpAsker.Asker(strResult,
	// Constants.BITKNOWTEST_SERVER_STRING + "servlet/" + url,
	// obj);
	// }
	// System.out.printf(strResult.toString());
	//
	// JSONObject obj2 = new JSONObject(strResult.toString());
	//
	// webUrl = obj2.getString("NewsUrl");
	//
	// } catch (IllegalStateException e) {
	// e.printStackTrace();
	// return null;
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// }
	//
	// return webUrl;
	// }
	//
	// public NewsContentData forContent(boolean isAlumni, int newsType,
	// long newsID) {
	//
	// String url;
	// if (isAlumni) {
	// url = XYW_NEWS_CONTENT;
	// } else {
	// url = serverNewsContent;
	// }
	//
	// JSONObject obj = new JSONObject();
	// NewsContentData contentData = null;
	// try {
	// obj.put("sourceID", newsType);
	// obj.put("NewsID", newsID);
	//
	// StringBuffer strResult = new StringBuffer();
	// // 请求云端服务器
	// int tmpInt = HttpAsker
	// .Asker(strResult, Constants.BITKNOWTEST_CLOUDSERVER_STRING
	// + "servlet/" + url, obj);
	// if (tmpInt != 0) {
	// strResult = new StringBuffer();
	// // 请求本地服务器
	// int tempINT = HttpAsker.Asker(strResult,
	// Constants.BITKNOWTEST_SERVER_STRING + "servlet/" + url,
	// obj);
	// }
	// if (tmpInt != 0)
	// contentData = null;
	// else {
	// // System.out.printf(strResult.toString());
	// JSONObject obj2 = new JSONObject(strResult.toString());
	// String newsUrl = obj2.getString("NewsUrl"); // 获取新闻的url,用于分享
	// JSONArray jsonArray = obj2.getJSONArray("NewsContent");
	//
	// contentData = new NewsContentData(newsUrl); // 把新闻的内容装进该结构
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject obj3 = jsonArray.getJSONObject(i);
	// String typeStr = obj3.getString("type");
	// int type = 0;
	// if (typeStr.equals("img")) {
	// type = 1;
	// } else if (typeStr.equals("text")) {
	// type = 0;
	// } else if (typeStr.equals("atturl")) {
	// type = 2;
	// } else if (typeStr.equals("attname")) {
	// type = 3;
	// }
	// String detail = obj3.getString("detail");
	// NewsContent content = new NewsContent(type, detail);
	// contentData.newsContentDataList.add(content);
	// }
	// }
	// } catch (Exception e1) {
	// contentData = null;
	// }
	// return contentData;
	// }
	//
	// /**
	// * 获取校友报的具体信息
	// * */
	// public NewsContentData forMobileContent(int newsType, long newsID) {
	// String url = "GetMobileNewsContent";
	// JSONObject obj = new JSONObject();
	// NewsContentData contentData = null;// 把新闻的内容装进该结构
	//
	// try {
	// obj.put("id", newsID);
	// StringBuffer strResult = new StringBuffer();
	// // 请求云端服务器
	// int tmpInt = HttpAsker
	// .Asker(strResult, Constants.BITKNOWTEST_CLOUDSERVER_STRING
	// + "servlet/" + url, obj);
	// if (tmpInt != 0) {
	// strResult = new StringBuffer();
	// // 请求本地服务器
	// int tempINT = HttpAsker.Asker(strResult,
	// Constants.BITKNOWTEST_SERVER_STRING + "servlet/" + url,
	// obj);
	//
	// }
	// if (tmpInt != 0)
	// contentData = null;
	// else {
	// System.out.printf(strResult.toString());
	// JSONArray jsonArray = new JSONArray(strResult.toString());
	// contentData = new NewsContentData("");
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject obj3 = jsonArray.getJSONObject(i);
	// String typeStr = obj3.getString("type");
	// int type = 0;
	// if (typeStr.equals("img")) {
	// type = 1;
	// } else if (typeStr.equals("text")) {
	// type = 0;
	// } else if (typeStr.equals("atturl")) {
	// type = 2;
	// } else if (typeStr.equals("attname")) {
	// type = 3;
	// }
	// String value = null;
	// if (type == 1) { // 为图片，需要拼接地址
	// String base_value = obj3.getString("value");
	// if (base_value.startsWith("http")) {
	// value = base_value;
	// } else {
	// value = "http://1.202.222.148" + base_value;
	// }
	// } else {
	// value = obj3.getString("value");
	// }
	// NewsContent content = new NewsContent(type, value);
	// contentData.newsContentDataList.add(content);
	// }
	// }
	// } catch (Exception e1) {
	// contentData = null;
	// }
	// return contentData;
	// }
}
