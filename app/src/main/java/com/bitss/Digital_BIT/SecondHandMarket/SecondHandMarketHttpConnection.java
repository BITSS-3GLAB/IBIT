package com.bitss.Digital_BIT.SecondHandMarket;

import java.io.IOException;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;

public class SecondHandMarketHttpConnection {
	private static final String TAG = HttpConnection.class.getSimpleName();
	private static int CONNECTION_TIMEOUT = 20000;
	private static int SO_TIMEOUT = 20000;

	private HttpClient getClient;
	private Context mContext;

	public SecondHandMarketHttpConnection(Context context) {
		getClient = new DefaultHttpClient();
		this.mContext = context;
	}

	public void doGet(final String url, final String cloudurl,
			final SecondHandMarketNetworkHandler mHandler) {

		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(mContext)) {
			Utils.showToast(mContext, Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				Message message = new Message();
				try {
					getClient.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONNECTION_TIMEOUT);
					getClient.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);

					// 得到HttpGet对象
					HttpGet cloudrequest = new HttpGet(cloudurl);

					// 客户端使用GET方式执行请教，获得服务器端的回应response
					HttpResponse cloudresponse = getClient
							.execute(cloudrequest);
					// 判断请求是否成功

					if (cloudresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						Log.i(TAG, "connect success!");
						// 获得输入流
						String strResult = EntityUtils.toString(cloudresponse
								.getEntity());
						Bundle bundle = new Bundle();
						bundle.putString("data", strResult);
						message.setData(bundle);
						message.what = SecondHandMarketNetworkHandler.SUCCESS;
					} else {

						HttpGet request = new HttpGet(url);
						HttpResponse response = getClient.execute(request);
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							Log.i(TAG, "connect success!");
							// 获得输入流
							String strResult = EntityUtils
									.toString(cloudresponse.getEntity());
							Bundle bundle = new Bundle();
							bundle.putString("data", strResult);
							message.setData(bundle);
							message.what = SecondHandMarketNetworkHandler.SUCCESS;
						} else {
							// 判断请求是否成功
							Log.i(TAG, "connect failure!");
							message.what = SecondHandMarketNetworkHandler.FAILURE;
						}
					}

				} catch (ClientProtocolException e) {

					e.printStackTrace();
					Log.i(TAG, "connect failure! e: " + e.toString());
					message.what = SecondHandMarketNetworkHandler.FAILURE;

				} catch (IOException e) {

					e.printStackTrace();
					Log.i(TAG, "connect failure! e: " + e.toString());
					message.what = SecondHandMarketNetworkHandler.FAILURE;

				} finally {

					mHandler.sendMessage(message);
				}

			}
		}).start();
	}

	public void doPost(final String url, final String couldurl,
			final JSONObject se, final SecondHandMarketNetworkHandler mHandler) {

		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(mContext)) {
			Utils.showToast(mContext, Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				Message message = new Message();

				try {

					StringBuffer buffer = new StringBuffer();
					HttpAsker.Asker(buffer, couldurl, se);
					Bundle bundle = new Bundle();
					bundle.putString("data", buffer.toString());
					message.setData(bundle);

					message.what = SecondHandMarketNetworkHandler.SUCCESS;

				} catch (Exception e) {
					// 请求本地服务器
					try {
						StringBuffer buffer = new StringBuffer();
						HttpAsker.Asker(buffer, url, se);
						Bundle bundle = new Bundle();
						bundle.putString("data", buffer.toString());
						message.setData(bundle);
						message.what = SecondHandMarketNetworkHandler.SUCCESS;
					} catch (Exception e2) {
						e.printStackTrace();
						Log.i(TAG, "connect failure! e: " + e.toString());
						message.what = SecondHandMarketNetworkHandler.FAILURE;
					}
				} finally {

					mHandler.sendMessage(message);
				}

			}

		}).start();
	}

	public void setTimeOut(int time) {
		CONNECTION_TIMEOUT = time;
		SO_TIMEOUT = time;
	}
}
