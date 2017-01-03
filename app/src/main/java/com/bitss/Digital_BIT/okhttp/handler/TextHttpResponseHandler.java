package com.bitss.Digital_BIT.okhttp.handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class TextHttpResponseHandler extends BaseHttpResponseHandler {

	@Override
	public void onFailure(Call call, IOException e) {
		onFailure(e);
	}

	@Override
	public void onSuccess(Call call, Response response) {
		int status = response.code();
		ResponseBody body = response.body();
		try {
			String data = body.string();
			onSuccess(status, data);
		} catch (IOException e) {
			onFailure(e);
		}
	}

	public abstract void onSuccess(int status, String data);

	public abstract void onFailure(Exception e);

}
