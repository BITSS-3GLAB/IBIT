package com.bitss.Digital_BIT.Meeting;

import java.io.Serializable;

class MeetingData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String MeetingName;
	public String Place;
	public String Time;
	public String MeetingDate;
	public String PeopleInclude;
	public String DayOfWeek;
	public String Year; //

	// 会议备注字段（新加）
	public String MeetingRemark;

	public MeetingData(String _MeetingName, String _Place, String _Time,
			String _Date, String _PeopleInclude, String _DayOfWeek,
			String _year, String _MeetingRemark) {
		MeetingName = _MeetingName;
		Place = _Place;
		Time = _Time;
		MeetingDate = _Date;
		PeopleInclude = _PeopleInclude;
		DayOfWeek = _DayOfWeek;
		this.Year = _year;
		MeetingRemark = _MeetingRemark;
	}
}
