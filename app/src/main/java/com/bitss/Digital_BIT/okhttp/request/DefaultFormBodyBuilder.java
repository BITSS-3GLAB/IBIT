package com.bitss.Digital_BIT.okhttp.request;

import okhttp3.FormBody.Builder;
import okhttp3.RequestBody;

public class DefaultFormBodyBuilder implements IHttpRequestBodyBuilder {

	private Builder mBuilder;

	@Override
	public void init() {
		mBuilder = new Builder();
	}

	@Override
	public void addPart(Parameter parameter) {
		mBuilder.add(parameter.getName(), String.valueOf(parameter.getValue()));
	}

	@Override
	public RequestBody body() {
		return mBuilder.build();
	}
}
