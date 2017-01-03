package com.bitss.Digital_BIT.Post.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.Util.JSONUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * HobbyGroupInformation共三个字段 hobby_group_id 标识唯一的ID hobby_group_post_count
 * 标识该社团的海报数量，用以排序 hobby_group_name 标识该社团的名称 hobby_group_image_uri 标识该社团的图标名称
 * 
 */

public class HobbyGroupInformation implements Parcelable {

	private JSONObject data;

	public HobbyGroupInformation() {
		this.data = new JSONObject();
	}

	public HobbyGroupInformation(JSONObject obj) {
		this.data = obj;
	}

	public JSONObject getJSONData() {
		return data;
	}

	public int getHobbyGroupId() {
		return JSONUtils.gets(data, "CorID", 0);
	}

	public int getHobbyGroupPostCount() {
		return JSONUtils.gets(data, "PubNum", 0);
	}

	public String getHobbyGroupName() {
		return JSONUtils.gets(data, "CorName", "");
	}

	public String getHobbyGroupImageUri() {
		// return JSONUtils.gets(data, "IconUrl", "");
		return "http://img3.douban.com/bpic/o634821.jpg";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(data.toString());
	}

	public static final Parcelable.Creator<HobbyGroupInformation> CREATOR = new Parcelable.Creator<HobbyGroupInformation>() {
		@Override
		public HobbyGroupInformation createFromParcel(Parcel source) {
			return new HobbyGroupInformation(source);
		}

		@Override
		public HobbyGroupInformation[] newArray(int size) {
			return new HobbyGroupInformation[size];
		}
	};

	private HobbyGroupInformation(Parcel in) {
		try {
			data = new JSONObject(in.readString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
