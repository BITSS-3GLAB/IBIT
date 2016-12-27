package com.bitss.Digital_BIT.okhttp.handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bitss.Digital_BIT.Util.Logger;

public class JsonHttpResponseHandler extends BaseHttpResponseHandler {

	private static final String TAG = JsonHttpResponseHandler.class
			.getSimpleName();

	@Override
	public void onFailure(Call call, IOException e) {
		onFailure(e);
	}

	@Override
	public final void onSuccess(Call call, Response response) {
		int status = response.code();
		ResponseBody body = response.body();
		try {
			String data = body.string();
			dispatch(status, data);
		} catch (IOException e) {
			onFailure(e);
		}
	}

	private void dispatch(int status, String jsonString) {
		try {
			Object data = parseString(jsonString);
			if (data == null) {
				onFailure(new JSONException("can not parse [" + jsonString
						+ "]"));
			} else if (data instanceof JSONObject) {
				onSuccess(status, (JSONObject) data);
			} else if (data instanceof JSONArray) {
				onSuccess(status, (JSONArray) data);
			} else {
				onSuccess(status, jsonString);
			}
		} catch (JSONException e) {
			onFailure(e);
		}
	}

	public void onFailure(Exception e) {
		Logger.i(TAG,
				"onFailure(Exception) was not overriden, but callback was received");
	};

	public void onSuccess(int status, JSONObject response) {
		Logger.i(TAG,
				"onSuccess(int, JSONObject) was not overriden, but callback was received");
	}

	public void onSuccess(int status, JSONArray response) {
		Logger.i(TAG,
				"onSuccess(int, JSONArray) was not overriden, but callback was received");
	}

	public void onSuccess(int status, String jsonString) {
		Logger.i(TAG,
				"onSuccess(int, String) was not overriden, but callback was received");
	}

	private Object parseString(String jsonString) throws JSONException {
		Object result = null;
		if (jsonString != null) {
			jsonString = jsonString.trim();
			if ((jsonString.startsWith("{") && jsonString.endsWith("}"))
					|| jsonString.startsWith("[") && jsonString.endsWith("]")) {
				result = new JSONTokener(jsonString).nextValue();
			} else {
				result = jsonString;
			}
		}
		return result;
	}
}
