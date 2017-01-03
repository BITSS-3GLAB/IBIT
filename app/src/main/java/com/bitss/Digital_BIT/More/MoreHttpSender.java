package com.bitss.Digital_BIT.More;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Util.Constants;

public class MoreHttpSender {
	private static final String serverSender = "servlet/GetFeedBack";

	public int sendMessage(String message) {
		String url = serverSender;
		String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING+"servlet/GetFeedBack"; 
		JSONObject obj = new JSONObject();
		try {
			message = new String(message.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			obj.put("Cnum", 1);
			obj.put("feedback", message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
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
			JSONObject obj2 = new JSONObject(strResult.toString());
			String ans = obj2.getString("result");
			if (ans.equals("true"))
				return 1;
			else
				return 2;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}
	}
}
