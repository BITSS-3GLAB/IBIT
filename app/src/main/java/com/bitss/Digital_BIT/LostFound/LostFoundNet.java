package com.bitss.Digital_BIT.LostFound;

import java.io.File;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bitss.Digital_BIT.Util.Constants;

public class LostFoundNet extends Thread {

	private static final int NET_PUBLISH = 1100;
	private static final int NET_GET_FRONT = 1101;
	private static final int NET_GET_AFTER = 1102;
	private static final String TAG = HttpConnection.class.getSimpleName();

	private static HttpClient client;

	private String des;
	private String place;
	private String phone;
	private String fileUrl;

	public LostFoundNet(String des, String place, String phone, String fileUrl) {
		this.des = des;
		this.place = place;
		this.phone = phone;
		this.fileUrl = fileUrl;

		initClient();
	}

	@Override
	public void run() {
		super.run();
		if (publish()) {
			Log.e("Net", "cheng gong");
		}
	}

	public void initClient() {
		if (client == null) {
			client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					30000);
		}
	}

	/**
	 * 发布招领信息
	 * 
	 * @param des
	 *            :描述
	 * @param place
	 *            :地点
	 * @param phone
	 *            : 电话
	 * @param fileUrl
	 *            :图片位置
	 * */
	@SuppressWarnings("deprecation")
	public boolean publish() {

		boolean rt = false;
		// 本地服务器url
		String url = "http://10.1.112.231/Digital_BIT_Server/servlet/GetLostFoundInfo";
		// 云端服务器url
		String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING
				+ "servlet/GetLostFoundInfo";

		HttpPost cloudpost = new HttpPost(cloudurl);
		MultipartEntity entity = new MultipartEntity();
		try {
			// 构建参数
			File file = new File(fileUrl);
			// entity.addPart("FormFileItem ", new FileBody(file));
			entity.addPart("Description", new StringBody(des));
			entity.addPart("Time",
					new StringBody(String.valueOf(System.currentTimeMillis())));
			entity.addPart("Location", new StringBody(place));
			entity.addPart("Contact", new StringBody(phone));
			cloudpost.setEntity(entity);

			// 请求云端服务器
			HttpResponse urlresponse = client.execute(cloudpost);
			if (urlresponse.getStatusLine().getStatusCode() == 200) {
				rt = true;
			} else {
				HttpPost post = new HttpPost(url);
				post.setEntity(entity);
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					rt = true;
				} else {
					rt = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "connect failure! e: " + e.toString());
		}

		return rt;
	}

	/**
	 * 获取数据
	 * 
	 * @param id
	 *            :请求起始id
	 * @param isFront
	 *            :是否请求最新数据 GetUpdateLostFoundInfo:新数据 GetPostLostFoundInfo:旧数据
	 * 
	 * */
	public void getContent(String id, boolean isFront, Handler callBackHandler) {
		Message message = new Message();
		JSONObject param = new JSONObject();
		// try {
		// param.put("ID", Long.valueOf(id));
		//
		// post.addHeader("Content-Type", "application/json");
		// post.addHeader("charset", HTTP.UTF_8);
		// post.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));
		//
		// HttpResponse response = client.execute(post);
		// if (response.getStatusLine().getStatusCode() == 200) {
		// message.what = isFront ? GET_NEW_SUCCESS : GET_OLD_SUCCESS;
		// message.obj = EntityUtils.toString(response.getEntity());
		// } else {
		// message.what = GET_FAILURE;
		// }
		// } catch (Exception e) {
		// message.what = GET_FAILURE;
		// } finally {
		// handler.sendMessage(message);
		// }
	}

}
