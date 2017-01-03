package com.bitss.Digital_BIT.Meeting;

import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Util.Constants;

class HttpMeetingAsker {
	private static final String serverMeeting = "GetMeetingInfo";

	public int askForMeeting(LinkedList<MeetingData> meetingListpre, int weekID) {
		LinkedList<MeetingData> meetingList = new LinkedList<MeetingData>();

		String url = serverMeeting;
		String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING+"servlet/"+"GetMeetingInfo"; 
		JSONObject obj = new JSONObject();
		try {
			obj.put("WeekID", weekID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			StringBuffer strResult = new StringBuffer();
			//请求云端服务器
			int tmpInt = HttpAsker.Asker(strResult, cloudurl, obj);
			if (tmpInt != 0){
				strResult = new StringBuffer();
				//请求本地服务器
				int tempINT=HttpAsker.Asker(strResult, url, obj);
				if(tempINT != 0){
					return tmpInt;
				}
			}
				

			JSONObject objAll = new JSONObject(strResult.toString());
			JSONArray jsonArray = objAll.getJSONArray("MeetingInfo");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj2 = jsonArray.getJSONObject(i);
				String MeetingName = obj2.getString("MeetingName");
				String Place = obj2.getString("Place");
				String Time = obj2.getString("Time");
				String Date = obj2.getString("Date");
				String PeopleInclude = obj2.getString("PeopleInclude");
				String DayOfWeek = obj2.getString("DayOfWeek");
				String Year = obj2.getString("Year");
				// 新加了会议备注部分
				String MeetingRemark = obj2.getString("Remarks");

				MeetingData data = new MeetingData(MeetingName, Place, Time,
						Date, PeopleInclude, DayOfWeek, Year, MeetingRemark);
				meetingList.add(data);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
		meetingListpre.clear();
		meetingListpre.addAll(meetingList);
		return 0;
	}
}