package com.bitss.Digital_BIT.News;

import java.io.Serializable;

/*
 * 新闻列表数据类
 */
public class NewsData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 2L;
	public String title; // 标题
	public String url; // url
	public String pubtime; // 发布日期
	public boolean newTag; // 是否为new的标记
	public boolean readTag; // 是否已读的标记
	public long id; // 新闻id

	public NewsData(String _title, String _pubtime, boolean _newTag,
			boolean _readTag, long _id, String _url) {
		title = _title;
		pubtime = _pubtime;
		newTag = _newTag;
		readTag = _readTag;
		id = _id;
		url = _url;
	}
}
