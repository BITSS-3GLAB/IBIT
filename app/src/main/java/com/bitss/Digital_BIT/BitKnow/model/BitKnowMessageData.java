package com.bitss.Digital_BIT.BitKnow.model;

import java.io.Serializable;

import javax.security.auth.PrivateCredentialPermission;

import org.json.JSONException;
import org.json.JSONObject;

public class BitKnowMessageData implements Serializable {
	private String name;
	private String text;
	
	public BitKnowMessageData(JSONObject json) throws JSONException
	{
		name = json.getString("username");
		text = json.getString("text");
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getText()
	{
		return text;
	}
}
