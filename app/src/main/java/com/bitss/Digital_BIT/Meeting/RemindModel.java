package com.bitss.Digital_BIT.Meeting;

public class RemindModel {

	public String id;

	public String name;

	public String place;

	public long time;

	public long remind_time;

	public RemindModel(String id, String name, String place, long time,
			long remind_time) {
		this.id = id;
		this.name = name;
		this.place = place;
		this.time = time;
		this.remind_time = remind_time;
	}

}
