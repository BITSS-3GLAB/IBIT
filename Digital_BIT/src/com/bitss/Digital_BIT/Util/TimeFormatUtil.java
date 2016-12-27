package com.bitss.Digital_BIT.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatUtil {

	/**
	 * 把string的时间格式化为long
	 * 
	 * @param time
	 *            : 2012年12月8日14:30
	 * */
	public long formatToLong(String time) {
		long result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
		Date dt;
		try {
			dt = sdf.parse(time);
			result = dt.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			result = 0;
		}
		return result;
	}

	/**
	 * 把long的时间格式化为string
	 * 
	 * @param time
	 *            : long 毫秒
	 * @return string :12月8日14:30
	 * */
	public String format2String(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		Date dt = new Date(time);
		return sdf.format(dt);
	}

	/**
	 * 把分钟转化为long毫秒
	 * 
	 * @param min
	 * */
	public long minFormat(String min) {
		return Integer.valueOf(min) * 60000;
	}
}
