package com.bitss.Digital_BIT.News;

public class NewsTypeModel {

	public String newsType;
	public int id;
	public String newsName;

	public NewsTypeModel(String newsType, String newsName) {
		this.newsType = newsType;
		this.newsName = newsName;
	}

	public void setId(int id) {
		this.id = id;
	}
}
