package com.bitss.Digital_BIT.News;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.bitss.Digital_BIT.R;

public class MenuDataBuilder {

	private Context context;
	private NewsFileAsker newsFileAsker;

	private List<NewsTypeModel> list = null; // 存放订阅的分类的数据（id、名字）
	private ArrayList<String> newsIdArrayList = new ArrayList<String>(); // 记录
	private String[] newsId;
	private String[] newsName;

	public MenuDataBuilder(Context context) {
		this.context = context;
		list = new ArrayList<NewsTypeModel>();
		newsFileAsker = new NewsFileAsker(context);
	}

	/**
	 * @param isAlumni
	 *            校友会则绑定校友会的值
	 * */
	public List<NewsTypeModel> getMenuData(boolean isAlumni) {
		if (!isAlumni) {
			buildMenuData();
		} else {
			buildAlumniData();
		}

		return list;
	}

	public void buildMenuData() {
		// 获取学院编号、学院名称
		newsName = context.getResources().getStringArray(R.array.newsName);
		newsId = context.getResources().getStringArray(R.array.newsId);
		Collections.addAll(newsIdArrayList, newsId);// 把string-array中newsId放进arrayList，便于查找下标

		// 001,002,003,010,014这四类新闻默认存在（新闻头条、通知公告、教务处、校招聘会、校友报）
		list.clear();
		for (int i = 0; i < newsName.length; i++) {
			NewsTypeModel model = new NewsTypeModel(newsId[i], newsName[i]);
			list.add(model);
		}
		
//		String fixedNewsStr = "[001,002,003,010]";
//		newsToBar(fixedNewsStr);
//
//		// 添加已订阅的新闻分类
//		String orderStr = newsFileAsker.readOrderFromFile("order");
//		if (orderStr != null)
//			newsToBar(orderStr);
	}

	/**
	 * 绑定校友会菜单数据
	 * */
	public void buildAlumniData() {
		list.clear();
		list.add(new NewsTypeModel("021", "校友网动态"));
		list.add(new NewsTypeModel("022", "校友网通知"));
		list.add(new NewsTypeModel("023", "走访校友"));
	}

	public void newsToBar(String newsStr) {

		// 字符串以"["开始
		int start = 1, end = 4;
		String idStr, nameStr;
		int length = newsStr.length();

		while (end < length) {
			idStr = newsStr.substring(start, end); // 获取新闻编号

			if (idStr.equals("200")) {
				start = start + 4;
				end = end + 4;
				continue;
			} else if (idStr.equals("201")) { // 结束标记
				break;
			} else {
				nameStr = newsName[newsIdArrayList.indexOf(idStr)]; // 获取新闻编号对应的名字
				NewsTypeModel model = new NewsTypeModel(idStr, nameStr);
				list.add(model);
				start = start + 4;
				end = end + 4;
			}
		}
	}

}
