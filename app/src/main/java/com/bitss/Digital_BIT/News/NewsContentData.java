package com.bitss.Digital_BIT.News;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新闻内容的结构体
 * 
 * 包括：新闻的url，新闻每一段的内容
 * */
public class NewsContentData implements Serializable {

	private static final long serialVersionUID = 3L;

	public String newsUrl; // 新闻url,用于分享
	public List<NewsContent> newsContentDataList; // 新闻内容

	public NewsContentData(String newsUrl) {
		this.newsUrl = newsUrl;
		this.newsContentDataList = new ArrayList<NewsContent>();
	}

	// 新闻每一段的结构
	public static class NewsContent implements Serializable {
		public int type; // 类型，0为文字，1为图片, 2为附件url， 3为附件名称
		public String detail; // 文字为具体内容，图片为url

		public NewsContent(int type, String detail) {
			this.type = type;
			this.detail = detail;
		}
	}

}
