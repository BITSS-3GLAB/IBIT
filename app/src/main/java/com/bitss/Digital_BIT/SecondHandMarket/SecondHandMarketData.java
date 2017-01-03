package com.bitss.Digital_BIT.SecondHandMarket;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.entity.SerializableEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class SecondHandMarketData implements Serializable {
	// 接受的消息

	private static final long serialVersionUID = 2L;
	private Integer id;
	private String desc;
	private Integer price;
	private String location;
	private String campus;
	private String category;
	// private String year;
	// private String day;
	// private String mouth;
	private String now;
	private String title;
	private String picUrl1;
	private String picUrl2;
	private String picUrl3;
	private String owner;
	private String phone;

	public SecondHandMarketData(JSONObject jsonObj) throws JSONException {

		id = Integer.valueOf(jsonObj.getInt("id"));
		title = jsonObj.getString("title");
		campus = jsonObj.getString("campus");

		price = Integer.valueOf(jsonObj.getInt("price"));
		try {
			category = jsonObj.getString("category");
			location = jsonObj.getString("location");
			desc = jsonObj.getString("description");
		} catch (Exception e) {
			// TODO: handle exception
		}

		now = jsonObj.getString("time");

		now = now.substring(0, now.length() - 2);

		// campus = jsonObj.getString("campus");
		// location = jsonObj.getString("location");
		picUrl1 = jsonObj.getString("picUrl1");
		picUrl1 = picUrl1.substring(1);
		picUrl2 = jsonObj.getString("picUrl2");
		picUrl2 = picUrl2.substring(1);
		picUrl3 = jsonObj.getString("picUrl3");
		picUrl3 = picUrl3.substring(1);
		// owner=jsonObj.getString("owner");
		// phone=jsonObj.getString("phone");
	}

	public String getDesc() {
		return desc;
	}

	public String getUrl1() {
		return picUrl1;
	}

	public String getCategory() {
		return category;
	}

	public String getUrl2() {
		return picUrl2;
	}

	public String getUrl3() {
		return picUrl3;
	}

	public String getLocation() {
		return location;
	}

	public String getTime() {
		return now;
	}

	public String getStringId() {
		return "" + id;
	}

	public String getTitle() {
		return title;
	}

	public String getCampus() {
		return campus;
	}

	public Integer getPrice() {
		return price;
	}

}
