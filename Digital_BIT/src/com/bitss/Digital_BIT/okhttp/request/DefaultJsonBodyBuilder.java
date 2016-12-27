package com.bitss.Digital_BIT.okhttp.request;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

public class DefaultJsonBodyBuilder implements IHttpRequsetBodyBuilder {

	private static final MediaType JSON = MediaType.parse("application/json");
	private JSONObject json;

	@Override
	public void init() {
		json = new JSONObject();
	}

	@Override
	public void addPart(Parameter parameter) {
		try {
			json.put(parameter.getName(), parameter.getValue());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public RequestBody body() {
		return RequestBody.create(JSON, json.toString());
	}

}
