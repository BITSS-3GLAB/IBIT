package com.bitss.Digital_BIT.okhttp;

import java.io.IOException;

import com.bitss.Digital_BIT.okhttp.cookie.CookieManager;
import com.bitss.Digital_BIT.okhttp.handler.BaseHttpResponseHandler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.content.Context;

public class HttpClient {
	private static OkHttpClient mOkHttpClient;
	private static HttpClient mHttpClient;

	private HttpClient(Context ctx) {
		OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
		// mBuilder.connectTimeout(20, TimeUnit.SECONDS);
		mBuilder.cookieJar(new CookieManager(ctx));
		mOkHttpClient = mBuilder.build();
	}
	
	public static HttpClient getInstance(Context ctx) {
		if (mHttpClient == null) {
			synchronized (HttpClient.class) {
				if (mHttpClient == null) {
					mHttpClient = new HttpClient(ctx);
				}
			}
		}
		return mHttpClient;
	}

	public void get(String url, BaseHttpResponseHandler handler) {
		Request request = new Request.Builder().url(url).get().build();
		mOkHttpClient.newCall(request).enqueue(new MyCallback(handler));
	}
	
	public Response get(String url) throws IOException {
		Request request = new Request.Builder().url(url).get().build();
		return mOkHttpClient.newCall(request).execute();
	}

	public void put(String url, RequestBody body, BaseHttpResponseHandler handler) {
		Request request = new Request.Builder().url(url).put(body).build();
		mOkHttpClient.newCall(request).enqueue(new MyCallback(handler));
	}
	
	public Response put(String url, RequestBody body) throws IOException {
		Request request = new Request.Builder().url(url).put(body).build();
		return mOkHttpClient.newCall(request).execute();
	}

	public void post(String url, RequestBody body, BaseHttpResponseHandler handler) {
		Request request = new Request.Builder().url(url).post(body).build();
		mOkHttpClient.newCall(request).enqueue(new MyCallback(handler));
	}
	
	public Response post(String url, RequestBody body) throws IOException {
		Request request = new Request.Builder().url(url).post(body).build();
		return mOkHttpClient.newCall(request).execute();
	}
	
	public Response delete(String url, RequestBody body) throws IOException {
		Request.Builder builder = new Builder();
		builder.url(url);
		if (body != null) {
			builder.put(body);
		}
		Request request = builder.delete().build();
		return mOkHttpClient.newCall(request).execute();
	}
	
	public void delete(String url, RequestBody body, BaseHttpResponseHandler handler) {
		Request.Builder builder = new Builder();
		builder.url(url);
		if (body != null) {
			builder.put(body);
		}
		Request request = builder.delete().build();
		mOkHttpClient.newCall(request).enqueue(new MyCallback(handler));
	}

	private static class MyCallback implements Callback {

		private BaseHttpResponseHandler mHandler;

		public MyCallback(BaseHttpResponseHandler handler) {
			this.mHandler = handler;
		}

		@Override
		public void onFailure(Call call, IOException e) {
			mHandler.sendFailureMessage(call, e);
		}

		@Override
		public void onResponse(Call call, Response response) throws IOException {
			mHandler.sendSuccessMessage(call, response);
		}

	}
}
