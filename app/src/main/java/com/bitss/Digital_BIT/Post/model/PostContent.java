package com.bitss.Digital_BIT.Post.model;

import org.json.JSONObject;

import com.bitss.Digital_BIT.Util.JSONUtils;


public class PostContent {
	private final  JSONObject data;

	public PostContent(JSONObject obj) {
		this.data = obj;
	}

	public JSONObject getJSONData() {
		return data;
	}

	public String getType() {
		return JSONUtils.gets(data, "type", "");
	}

	public String getValue() {
		return JSONUtils.gets(data, "value", "");
	}
}
