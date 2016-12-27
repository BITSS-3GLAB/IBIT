package com.bitss.Digital_BIT.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpAsker {

	/**
	 * Parameter:returnData返回的结果
	 * 
	 * */
	public static int Asker(StringBuffer returnData, String url, Object data) {

		if (!url.contains("http://")) {
			if (url.equals("GetNewsList")) {
				url = ParamUtil.SERVER_MOBILE_URL + url;
			} else if (url.equals("GetMobileNewsContent")) {
				url = ParamUtil.SERVER_MOBILE_URL + "GetNewsContent";
			} else if (url.equals("SetNewsComment")
					|| url.equals("GetNewsComment")
					|| url.equals("GetNewsCommentNum")) {
				url = ParamUtil.SERVER_URL + url;
			} else {
				url = ParamUtil.SERVER_URL + url;
			}
		}

		// 生成一个http的客户端对象
		DefaultHttpClient client = new DefaultHttpClient();
		// 请求超时
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		// 读取超时
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

		// 生成一个请求对象
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("charset", HTTP.UTF_8);

		try {
			// 把需要发送的数据放到httpPost对象里，同时规定编码格式
			httpPost.setEntity(new StringEntity(data.toString(), HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		HttpResponse response = null;
		try {
			// 使用客户端对象发送请求
			response = client.execute(httpPost);

			// 获得返回的状态码
			int id = response.getStatusLine().getStatusCode();
			if (id == 200) {

				returnData.append(EntityUtils.toString(response.getEntity()));
				// 获取的字符串
				Log.d("bus_test", "返回的json串" + returnData.toString());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			return -2;
		}

		return 0;
	}

	/**
	 * 获取启动页面信息
	 * 
	 * @param url
	 *            :服务器地址
	 * @return :返回获取的参数
	 * */
	public static String getGuideParam(String url) {

		StringBuffer result = new StringBuffer();

		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);

		// 生成一个请求对象
		HttpGet httpGet = new HttpGet(ParamUtil.SERVER_URL + url);
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("charset", HTTP.UTF_8);

		HttpResponse response = null;
		try {
			// 使用客户端对象发送请求
			response = client.execute(httpGet);
			// 获得返回的状态码
			int id = response.getStatusLine().getStatusCode();
			if (id == 200) {
				result.append(EntityUtils.toString(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = null;
		} catch (IOException e) {
			result = null;
		}

		return (result == null) ? null : result.toString();
	}

	/**
	 * 获取图片数据，并保存sd卡 返回图片的bmp
	 * 
	 * @param param
	 *            :图片下载地址
	 * */
	public static Bitmap getGuideBmp(String param) {
		Bitmap bitmap;

		try {
			URL url = new URL(param);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream inputStream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			bitmap = null;
		}

		return bitmap;
	}
}
