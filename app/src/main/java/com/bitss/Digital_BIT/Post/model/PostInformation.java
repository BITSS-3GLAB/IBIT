package com.bitss.Digital_BIT.Post.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.Util.JSONUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class PostInformation implements Parcelable {

	private JSONObject data;

	public PostInformation() {
		this.data = new JSONObject();
	}

	public PostInformation(JSONObject obj) {
		this.data = obj;
	}

	public JSONObject getJSONData() {
		return data;
	}

	public int getPostId() {
		return JSONUtils.gets(data, "post_id", 0);
	}

	public String getPostHost() {
		return JSONUtils.gets(data, "post_host", "");
	}

	public String getPostTitle() {
		return JSONUtils.gets(data, "post_title", "");
	}

	public String getPostTimestamp() {
		return JSONUtils.gets(data, "post_timestamp", "");
	}

	public String getPostLocation() {
		return JSONUtils.gets(data, "post_location", "");
	}

	public String getPostImageUri() {
		return JSONUtils.gets(data, "post_image_uri", "");
	}

	public int getPostHeight() {
		return JSONUtils.gets(data, "post_height", 01);
	}

	public int getPostWidth() {
		return JSONUtils.gets(data, "post_width", 01);
	}

	public String getPostPhone() {
		return JSONUtils.gets(data, "post_phone", "");
	}

	public int getCorId() {
		return JSONUtils.gets(data, "cor_id", 01);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(data.toString());
	}

	public static final Parcelable.Creator<PostInformation> CREATOR = new Parcelable.Creator<PostInformation>() {
		@Override
		public PostInformation createFromParcel(Parcel source) {
			return new PostInformation(source);
		}

		@Override
		public PostInformation[] newArray(int size) {
			return new PostInformation[size];
		}
	};

	private PostInformation(Parcel in) {
		try {
			data = new JSONObject(in.readString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
