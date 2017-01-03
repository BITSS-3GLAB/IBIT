package com.bitss.Digital_BIT.News;

import java.io.Serializable;
/*
 * 新闻列表数据类
 */
public class NewsData3 implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 2L;
	public String title;	// 标题
	public String content;	// 简介
	public String url;		// url
	public String pubtime;	// 发布日期
	public boolean newTag;	// 是否为new的标记
	public boolean readTag;	// 是否已读的标记
	public long id;			// 新闻id

	public NewsData3(String _title, String _content, String _pubtime,
			boolean _newTag, boolean _readTag, long _id){
		title = _title;
		content = _content;
		pubtime = _pubtime;
		newTag = _newTag;
		readTag = _readTag;
		id = _id;
	}
}
