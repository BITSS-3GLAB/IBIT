package com.bitss.Digital_BIT.LostFound;

import java.io.Serializable;

public class LostFoundModel implements Serializable {

	public long id;
	public String url; // 图片url
	public String desc;// 描述
	public String loc;// 位置
	public String cont;// 联系方式
	public String time;// 时间

	public LostFoundModel(long id, String url, String desc, String loc,
			String cont, String time) {
		this.id = id;
		this.url = url;
		this.desc = desc;
		this.loc = loc;
		this.cont = cont;
		this.time = time;
	}

}
