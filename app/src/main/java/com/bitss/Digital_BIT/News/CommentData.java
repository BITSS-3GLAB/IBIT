package com.bitss.Digital_BIT.News;

import java.io.Serializable;

/*
 * 评论列表数据类
 */
public class CommentData implements Serializable {
	/**
		 *
		 */
	private static final long serialVersionUID = 2L;
	public String username; // 用户名
	public String content; // 评论内容
	public String pubtime; // 发布日期
	public String peopleUrl = "";
	public long newsId; // 新闻id
	public long newsType; // 所属的新闻类别
	public long id; // 评论id

	public CommentData(String _username, String _content, String _pubtime,
			String _url, long _newsId, long _newsType, long _id) {
		username = _username;
		content = _content;
		pubtime = _pubtime;
		newsId = _newsId;
		newsType = _newsType;
		id = _id;
		try {
			peopleUrl = _url.substring(1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
