//package com.bitss.Digital_BIT.News;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.BaseAdapter;
//import android.widget.Gallery;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bitss.Digital_BIT.CustomBaseActivity;
//import com.bitss.Digital_BIT.R;
//
//public class DragListActivity2 extends CustomBaseActivity {
//
//	private static final int ORDER_CODE = 1001;
//
//	private int orderPosition = 8; // 目前订阅的位置，初始化为软院
//
//	// 2012/11/14修改了新闻编号和新闻名字的获取方法
//	private ArrayList<String> newsIdArrayList = new ArrayList<String>();
//	private String[] newsId;
//	private String[] newsName;
//
//	private static List<NewsTypeModel> list = new ArrayList<NewsTypeModel>(); // 绑定的list
//	private List<NewsTypeModel> softwareList = new ArrayList<NewsTypeModel>(); // 软院可订阅新闻的列表
//	private List<NewsTypeModel> employmentList = new ArrayList<NewsTypeModel>();// 就业订阅新闻的列表
//
//	private DragListAdapter adapter = null;
//	private DragListView dragListView;
//
//	// -------------------------学院、部门分类---------------------
//	private Gallery g;
//	private ImageAdapter imageAdapter;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.drag_list_activity);
//
//		mTvNaviTitle.setText("编辑栏目");
//
//		newsName = getResources().getStringArray(R.array.newsName);
//		newsId = getResources().getStringArray(R.array.newsId);
//		// 把string-array中newsId放进arrayList
//		for (int i = 0; i < newsId.length; i++) {
//			newsIdArrayList.add(newsId[i]);
//		}
//
//		initBar();
//		initData(); // 初始化就业新闻分类和软院新闻分类
//
//		list.clear();
//		list.addAll(softwareList);
//
//		dragListView = (DragListView) findViewById(R.id.drag_list);
//		adapter = new DragListAdapter(this, list);
//		dragListView.setAdapter(adapter);
//	}
//
//	// @Override
//	// public void onBackPressed() {
//	// onBack();
//	// }
//
//	@Override
//	public void backAction() {
//		onBack();
//	}
//
//	/**
//	 * 把所有已订阅的分类组合在一起(就业实习和软院)
//	 * */
//	public List<String> allOrderStr() {
//		List<String> rt = new ArrayList<String>();
//
//		for (int i = 0; i < employmentList.size(); i++) {
//			if (employmentList.get(i).newsType.equals("200")) {
//				continue;
//			} else if (employmentList.get(i).newsType.equals("201")) {
//				break;
//			} else {
//				rt.add(employmentList.get(i).newsType);
//			}
//		}
//
//		for (int i = 0; i < softwareList.size(); i++) {
//			if (softwareList.get(i).newsType.equals("200")) {
//				continue;
//			} else if (softwareList.get(i).newsType.equals("201")) {
//				break;
//			} else {
//				rt.add(softwareList.get(i).newsType);
//			}
//		}
//
//		return rt;
//	}
//
//	/**
//	 * 返回需要把订阅数据写入文件（1、订阅数据 2、软院数据 3、就业数据）
//	 * */
//	public void onBack() {
//		List<String> list = new ArrayList<String>();
//		NewsFileAsker newsFileAsker = new NewsFileAsker(DragListActivity2.this);
//
//		if (orderPosition == 8) { // 软院数据
//			softwareList.clear();
//			softwareList.addAll(this.list);
//		} else {
//			employmentList.clear();
//			employmentList.addAll(this.list);
//		}
//
//		// 所有订阅类别
//		String orderStr = allOrderStr().toString().replace(" ", "");
//		newsFileAsker.writeOrderStrToFile(orderStr, "order");
//
//		// 软院数据写入文件
//		for (NewsTypeModel aa : softwareList) {
//			list.add(aa.newsType);
//		}
//		String softwareStr = list.toString().replace(" ", "");
//		newsFileAsker.writeOrderStrToFile(softwareStr, "software");
//
//		// 就业数据写入文件
//		list.clear();
//		for (NewsTypeModel aa : employmentList) {
//			list.add(aa.newsType);
//		}
//		String employmentStr = list.toString().replace(" ", "");
//		newsFileAsker.writeOrderStrToFile(employmentStr, "employment");
//
//		setResult(ORDER_CODE);
//		finish();
//	}
//
//	// 初始化可订阅的学院
//	public void initBar() {
//		g = (Gallery) findViewById(R.id.college_gallery);
//		imageAdapter = new ImageAdapter(this);
//		g.setAdapter(imageAdapter);
//		g.setSelection(8);
//		g.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//
//				if (position == 8) { // 软院
//					dragListView.setVisibility(View.VISIBLE);
//
//					if (orderPosition == 7) { // 此时list绑定的是就业的数据，需要替换为软院数据
//						employmentList.clear();
//						employmentList.addAll(list);
//						list.clear();
//
//						list.addAll(softwareList);
//						adapter.notifyDataSetChanged();
//
//						orderPosition = 8;
//					}
//
//				} else if (position == 7) { // 就业
//					dragListView.setVisibility(View.VISIBLE);
//
//					if (orderPosition == 8) { // 此时list绑定的是软院的数据，需要替换为就业数据
//						softwareList.clear();
//						softwareList.addAll(list);
//						list.clear();
//
//						list.addAll(employmentList);
//						adapter.notifyDataSetChanged();
//
//						orderPosition = 7;
//					}
//
//				} else {
//					dragListView.setVisibility(View.INVISIBLE);
//					warn();
//				}
//			}
//		});
//
//	}
//
//	public void warn() {
//		Toast.makeText(this, this.getString(R.string.college_warning),
//				Toast.LENGTH_SHORT).show();
//	}
//
//	/**
//	 * 该生成指定学院可订阅新闻的列表
//	 * 
//	 * @param:mList需要绑定的新闻列表
//	 * @param:newsStr格式：[001,002]
//	 * */
//	public void dataToList(List<NewsTypeModel> mList, String newsStr) {
//
//		// 字符串以"["开始
//		int start = 1, end = 4;
//		String idStr, nameStr;
//		int length = newsStr.length();
//
//		while (end < length) {
//			idStr = newsStr.substring(start, end); // 获取新闻编号
//			nameStr = newsName[newsIdArrayList.indexOf(idStr)]; // 获取新闻编号对应的名字
//			NewsTypeModel model = new NewsTypeModel(idStr, nameStr);
//			mList.add(model);
//			start = start + 4;
//			end = end + 4;
//		}
//	}
//
//	/**
//	 * 初始化就业分类和软院新闻分类
//	 * */
//	public void initData() {
//		initEmploymentData();
//		initSoftwareData();
//	}
//
//	/*
//	 * 初始化软院的新闻分类 *
//	 */
//	public void initSoftwareData() {
//		// 软院可订阅的新闻类别
//		String initOrderStr = "[200,201,004,005,006,007,008]";
//		NewsFileAsker newsFileAsker = new NewsFileAsker(this);
//		String orderStr = newsFileAsker.readOrderFromFile("software");
//		if (orderStr == null)
//			orderStr = initOrderStr;
//
//		// 软院新闻的list
//		softwareList.clear();
//		dataToList(softwareList, orderStr);
//	}
//
//	/*
//	 * 初始化就业指导的新闻分类 *
//	 */
//	public void initEmploymentData() {
//		// 就业指导可订阅的新闻类别
//		String initOrderStr = "[200,201,011,012,013]";
//		NewsFileAsker newsFileAsker = new NewsFileAsker(this);
//		String orderStr = newsFileAsker.readOrderFromFile("employment");
//		if (orderStr == null)
//			orderStr = initOrderStr;
//
//		// 就业的list
//		employmentList.clear();
//		dataToList(employmentList, orderStr);
//	}
//
//	public static class DragListAdapter extends ArrayAdapter<NewsTypeModel> {
//
//		public DragListAdapter(Context context, List<NewsTypeModel> objects) {
//			super(context, 0, objects);
//		}
//
//		public List<NewsTypeModel> getList() {
//			return list;
//		}
//
//		@Override
//		public boolean isEnabled(int position) {
//			if (getItem(position).newsType.equals("200")
//					|| getItem(position).newsType.equals("201")) {
//				return false;
//			}
//			return super.isEnabled(position);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//
//			View view = convertView;
//			String idStr = getItem(position).newsType;
//
//			if (idStr.equals("200") || idStr.equals("201")) {
//				view = LayoutInflater.from(getContext()).inflate(
//						R.layout.drag_list_item_tag, null);
//			} else {
//				view = LayoutInflater.from(getContext()).inflate(
//						R.layout.drag_list_item, null);
//
//			}
//			TextView textView = (TextView) view
//					.findViewById(R.id.drag_list_item_text);
//			textView.setText(getItem(position).newsName);
//
//			return view;
//		}
//	}
//
//	public class ImageAdapter extends BaseAdapter {
//		private LayoutInflater inflater;
//
//		// 所有学院机构的名称
//		private String[] collegeNames = getResources().getStringArray(
//				R.array.college);
//
//		public ImageAdapter(Context c) {
//			this.inflater = LayoutInflater.from(c);
//		}
//
//		public int getCount() {
//			return collegeNames.length;
//		}
//
//		public void notifyDataSetChanged(int albumId) {
//			super.notifyDataSetChanged();
//		}
//
//		public Object getItem(int position) {
//			return collegeNames[position];
//		}
//
//		public long getItemId(int position) {
//			return position;
//		}
//
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View cellView = inflater.inflate(R.layout.meetinggallerycell, null);
//
//			TextView textViewCell = (TextView) cellView
//					.findViewById(R.id.meetinggallerytextview);
//			textViewCell.setText(collegeNames[position]);
//
//			return cellView;
//		}
//	}
//}