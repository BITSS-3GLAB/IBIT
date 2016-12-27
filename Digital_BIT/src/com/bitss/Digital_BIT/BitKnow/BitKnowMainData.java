package com.bitss.Digital_BIT.BitKnow;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class BitKnowMainData implements Serializable {
	private Integer id;

	private String photoUrl;
	private String name;
	private String time;

	private String picUrl1;
	private String picUrl2;
	private String picUrl3;

	private String content;

	private String labels;
	private int number_of_answer;

	public BitKnowMainData(JSONObject jsonObject) throws JSONException {
		id = Integer.valueOf(jsonObject.getInt("id"));

		photoUrl = jsonObject.getString("photoUrl");
		name = jsonObject.getString("username");
		time = jsonObject.getString("time");
		time = time.substring(0, time.length() - 2);

		picUrl1 = jsonObject.getString("picUrl1");
		picUrl2 = jsonObject.getString("picUrl2");
		picUrl3 = jsonObject.getString("picUrl3");

		content = jsonObject.getString("text");

		labels = jsonObject.getString("tag");
		number_of_answer = Integer.valueOf(jsonObject.getInt("answer_number"));
	}

	public Integer getId() {
		return id;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public String getName() {
		return name;
	}

	public String getTime() {
		return time;
	}

	public String getUrl1() {
		return picUrl1;
	}

	public String getUrl2() {
		return picUrl2;
	}

	public String getUrl3() {
		return picUrl3;
	}

	public String getContent() {
		return content;
	}

	public String getLabels() {
		return labels;
	}

	public int getNum() {
		return number_of_answer;
	}

	public void setNum(Integer number_of_answer) {
		this.number_of_answer = number_of_answer;
	}
	
	public void deleteAnswer() {
		this.number_of_answer--;
	}
}
