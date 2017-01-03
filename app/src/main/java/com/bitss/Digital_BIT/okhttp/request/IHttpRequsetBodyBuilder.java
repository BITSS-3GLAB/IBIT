package com.bitss.Digital_BIT.okhttp.request;

import okhttp3.RequestBody;

public interface IHttpRequsetBodyBuilder {

	public void init();
	
	public void addPart(Parameter parameter);
	
	public RequestBody body();
}
