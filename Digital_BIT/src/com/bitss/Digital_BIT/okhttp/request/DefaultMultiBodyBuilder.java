package com.bitss.Digital_BIT.okhttp.request;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.RequestBody;

public class DefaultMultiBodyBuilder implements IHttpRequsetBodyBuilder {

	private Builder mBuilder;

	@Override
	public void init() {
		mBuilder = new MultipartBody.Builder();
		mBuilder.setType(MediaType.parse("multipart/form-data"));
	}

	@Override
	public void addPart(Parameter parameter) {
		if (parameter.isFile()) {
			mBuilder.addFormDataPart(parameter.getName(),
					parameter.getFilename(),
					RequestBody.create(parameter.getType(), (File) parameter.getValue()));
		} else {
			mBuilder.addFormDataPart(parameter.getName(),
					String.valueOf(parameter.getValue()));
		}
	}

	@Override
	public RequestBody body() {
		return mBuilder.build();
	}

}
