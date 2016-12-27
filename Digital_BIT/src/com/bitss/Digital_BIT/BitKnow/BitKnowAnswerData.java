package com.bitss.Digital_BIT.BitKnow;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class BitKnowAnswerData implements Serializable {
	private int id;
	private String text;
	private Integer number_of_zan;
	private String time;
	private String photoUrl;
	private String username;
	private Integer answernumber;
	private boolean isuse;
	private String zan = "no";

	public BitKnowAnswerData(JSONObject jsonObj, boolean isuse)
			throws JSONException {

		id = Integer.valueOf(jsonObj.getInt("id"));
		text = jsonObj.getString("text");
		number_of_zan = Integer.valueOf(jsonObj.getInt("number_of_zan"));
		time = jsonObj.getString("time");
		photoUrl = jsonObj.getString("photoUrl");
		// photoUrl = photoUrl.substring(1, photoUrl.length());
		username = jsonObj.getString("username");
		time = time.substring(0, time.length() - 2);
		if (isuse != true) {
			answernumber = Integer.valueOf(jsonObj.getInt("numberOfAnswer"));
		}
		zan = jsonObj.getString("hasZan");
		this.isuse = isuse;

	}

	public String getZan() {
		return zan;
	}

	public void setZan(String zan) {
		this.zan = zan;
	}

	public Integer getAnswernumber() {
		return answernumber;
	}

	public void setAnswernumber(Integer answernumber) {
		this.answernumber = answernumber;
	}

	public String getphotoUrl() {
		return photoUrl;
	}

	public String getusername() {
		return username;
	}

	public String gettime() {
		return time;
	}

	public Integer getzan() {
		return number_of_zan;
	}
	
	public void addZan() {
		number_of_zan++;
		zan = "yes";
	}

	public String gettext() {
		return text;
	}

	public int getid() {
		return id;
	}

	public boolean getisuse() {
		return isuse;
	}

	public void setisuse(boolean bool) {
		this.isuse = bool;
	}

}
