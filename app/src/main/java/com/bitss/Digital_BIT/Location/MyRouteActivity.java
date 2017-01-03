package com.bitss.Digital_BIT.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitss.Digital_BIT.BaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Location.MapModel.ChildModel;
import com.bitss.Digital_BIT.Location.SearchActivity.ComparatorDistance;
import com.umeng.analytics.MobclickAgent;

public class MyRouteActivity extends BaseActivity implements OnClickListener {

	private int buttonType = 0; // 默认是起点

	private Button startButton;
	private Button endButton;
	private ImageView driveButton, walkButton;

	private ExpandableListView routeListView;
	private ExpandableListView lxrouteListView;
	private MyRouteAdapter adapter;
	private lxMyRouteAdapter lxadapter;
	private ImageView startRouteButton;

	private int routeMode = 0; // 默认为驾车
	private PositionData startPoint, endPoint;

	private ArrayList<MapModel.GroupModel> groupList = new ArrayList<MapModel.GroupModel>();
	private ArrayList<MapModel.GroupModel> lxgroupList = new ArrayList<MapModel.GroupModel>();
	private ArrayList[] childList;
	private ArrayList[] lxchildList;
	private Dialog dialog; // 加载数据弹出的对话框

	private boolean hasCurrentPoint = false; // 当前位置信息是否获取到
	private double myLatitude = 0, myLongitude = 0; // 当前位置的经纬度

	public void toMainActivity() {
		Intent intent = new Intent(this, IBitMapActivity.class);
		Bundle b = new Bundle();
		b.putSerializable("start_point", startPoint);
		b.putSerializable("end_point", endPoint);
		b.putInt("route_mode", routeMode);
		intent.putExtras(b);
		setResult(1, intent);
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			routeMode = -1; // 点击返回按钮
			toMainActivity();
		}
		super.onKeyDown(keyCode, event);
		return true;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			routeMode = -1; // 点击返回按钮
			toMainActivity();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);

		getSupportActionBar().hide();
		init();
	}

	public void init() {
		((ImageView) findViewById(R.id.iv_navi_back)).setOnClickListener(this);
		startButton = (Button) findViewById(R.id.btn_start);
		startButton.setOnClickListener(this);
		endButton = (Button) findViewById(R.id.btn_end);
		endButton.setOnClickListener(this);

		driveButton = (ImageView) findViewById(R.id.btn_drive);
		driveButton.setOnClickListener(this);
		walkButton = (ImageView) findViewById(R.id.btn_walk);
		walkButton.setOnClickListener(this);

		startRouteButton = (ImageView) findViewById(R.id.btn_start_route);
		startRouteButton.setOnClickListener(this);

		// 获取定位的经纬度
		myLatitude = getIntent().getDoubleExtra("latitude", 0);
		myLongitude = getIntent().getDoubleExtra("longitude", 0);
		if (myLatitude == 0) {
			hasCurrentPoint = false; // 没有定位，所以没有当前位置信息
		} else {
			hasCurrentPoint = true;
		}

		// 初始化分组的数据（名字+数字）
		String[] groupName = getResources().getStringArray(R.array.group_name);
		String[] groupNum = getResources().getStringArray(R.array.group_num);
		String[] lxgroupName = getResources().getStringArray(R.array.lxgroup_name);
		String[] lxgroupNum = getResources().getStringArray(R.array.lxgroup_num);
		for (int i = 0; i < groupNum.length; i++) {
			MapModel.GroupModel model = new MapModel.GroupModel(groupName[i],
					groupNum[i]);
			groupList.add(model);
		}
		for (int i = 0; i < lxgroupNum.length; i++) {
			MapModel.GroupModel model1 = new MapModel.GroupModel(lxgroupName[i],
					lxgroupNum[i]);
			lxgroupList.add(model1);
		}
		childList = new ArrayList[groupName.length];
		lxchildList = new ArrayList[lxgroupName.length];

		routeListView = (ExpandableListView) findViewById(R.id.route_list);
		lxrouteListView = (ExpandableListView) findViewById(R.id.lxroute_list);
		adapter = new MyRouteAdapter(this);
		lxadapter = new lxMyRouteAdapter(this);
		routeListView.setAdapter(adapter);
		routeListView.setGroupIndicator(null);
		lxrouteListView.setAdapter(lxadapter);
		lxrouteListView.setGroupIndicator(null);

		// ELV的点击处理
		routeListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (childList[groupPosition] == null) {
					if (groupPosition == 6 && !hasCurrentPoint) {
						// 如果点击生活助手，且当前位置没有获得定位，则弹出提示框
						Toast.makeText(MyRouteActivity.this,
								"您尚未定位，定位后可以获取距离信息", 0).show();
					}
					new AddChildContentTask().execute(groupPosition);
				} else {
					setELVGroup(parent, groupPosition);
				}
				return true;
			}
		});
		routeListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				MapModel.ChildModel model = (ChildModel) childList[groupPosition]
						.get(childPosition);
				PositionData data = new PositionData(model.name, model.address,
						model.latitude, model.longitude);
				if (buttonType == 0) {
					startPoint = data;
					startButton.setText(data.getName());
				} else {
					endPoint = data;
					endButton.setText(data.getName());
				}
				return true;
			}
		});
		lxrouteListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (lxchildList[groupPosition] == null) {
					if (groupPosition == 6 && !hasCurrentPoint) {
						// 如果点击生活助手，且当前位置没有获得定位，则弹出提示框
						Toast.makeText(MyRouteActivity.this,
								"您尚未定位，定位后可以获取距离信息", 0).show();
					}
					new lxAddChildContentTask().execute(groupPosition);
				} else {
					lxsetELVGroup(parent, groupPosition);
				}
				return true;
			}
		});
		lxrouteListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				MapModel.ChildModel model = (ChildModel) lxchildList[groupPosition]
						.get(childPosition);
				PositionData data = new PositionData(model.name, model.address,
						model.latitude, model.longitude);
				if (buttonType == 0) {
					startPoint = data;
					startButton.setText(data.getName());
				} else {
					endPoint = data;
					endButton.setText(data.getName());
				}
				return true;
			}
		});
	}
	

	public void setELVGroup(ExpandableListView parent, int groupPosition) {
		if (parent.isGroupExpanded(groupPosition)) {
			routeListView.collapseGroup(groupPosition);
		} else {
			routeListView.expandGroup(groupPosition);
			routeListView.setSelectedGroup(groupPosition);
		}
	}
	public void lxsetELVGroup(ExpandableListView parent, int groupPosition) {
		if (parent.isGroupExpanded(groupPosition)) {
			lxrouteListView.collapseGroup(groupPosition);
		} else {
			lxrouteListView.expandGroup(groupPosition);
			lxrouteListView.setSelectedGroup(groupPosition);
		}
	}

	public class MyRouteAdapter extends BaseExpandableListAdapter {

		private LayoutInflater inflater;

		public MyRouteAdapter(Activity activity) {
			inflater = activity.getLayoutInflater();
		}

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (childList[groupPosition] != null) {
				return childList[groupPosition].size();
			}
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (childList[groupPosition] != null) {
				return childList[groupPosition].get(childPosition);
			}
			return null;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.group_item, parent, false);
			TextView title = (TextView) convertView.findViewById(R.id.tv_group);
			TextView num = (TextView) convertView
					.findViewById(R.id.tv_group_num);
//
//			if (groupPosition == 6) {
//				// 生活助手时显示new图标
//				Drawable newIcon = getResources()
//						.getDrawable(R.drawable.bg_new);
//				newIcon.setBounds(0, 0, newIcon.getMinimumWidth(),
//						newIcon.getMinimumHeight());
//				title.setCompoundDrawables(newIcon, null, null, null);
//			}

			title.setText(groupList.get(groupPosition).name);
			num.setText(groupList.get(groupPosition).num);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			convertView = inflater.inflate(R.layout.child_item, parent, false);

			MapModel.ChildModel model = (ChildModel) childList[groupPosition]
					.get(childPosition);
			TextView name = (TextView) convertView.findViewById(R.id.tv_child);
			TextView distance = (TextView) convertView
					.findViewById(R.id.tv_distance);
			name.setText(model.name);
			if (model.distance != 0) {
				distance.setVisibility(View.VISIBLE);
				distance.setText(model.distance + "m");
			} else {
				distance.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return true;
		}

	}
	public class lxMyRouteAdapter extends BaseExpandableListAdapter {

		private LayoutInflater inflater;

		public lxMyRouteAdapter(Activity activity) {
			inflater = activity.getLayoutInflater();
		}

		@Override
		public int getGroupCount() {
			return lxgroupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (lxchildList[groupPosition] != null) {
				return lxchildList[groupPosition].size();
			}
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return lxgroupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (lxchildList[groupPosition] != null) {
				return lxchildList[groupPosition].get(childPosition);
			}
			return null;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.group_item, parent, false);
			TextView title = (TextView) convertView.findViewById(R.id.tv_group);
			TextView num = (TextView) convertView
					.findViewById(R.id.tv_group_num);
//
//			if (groupPosition == 6) {
//				// 生活助手时显示new图标
//				Drawable newIcon = getResources()
//						.getDrawable(R.drawable.bg_new);
//				newIcon.setBounds(0, 0, newIcon.getMinimumWidth(),
//						newIcon.getMinimumHeight());
//				title.setCompoundDrawables(newIcon, null, null, null);
//			}

			title.setText(lxgroupList.get(groupPosition).name);
			num.setText(lxgroupList.get(groupPosition).num);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			convertView = inflater.inflate(R.layout.child_item, parent, false);

			MapModel.ChildModel model = (ChildModel) lxchildList[groupPosition]
					.get(childPosition);
			TextView name = (TextView) convertView.findViewById(R.id.tv_child);
			TextView distance = (TextView) convertView
					.findViewById(R.id.tv_distance);
			name.setText(model.name);
			if (model.distance != 0) {
				distance.setVisibility(View.VISIBLE);
				distance.setText(model.distance + "m");
			} else {
				distance.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return true;
		}

	}

	/**
	 * 加载子集的数据
	 * */
	public class AddChildContentTask extends
	AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			if (dialog == null) {
				dialog = ProgressDialog.show(MyRouteActivity.this, "",
						"数据加载中...");
			} else {
				dialog.show();
			}
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int rt = params[0];
			try {
				String[] name = null;
				String[] address = null;
				String[] latitude = null;
				String[] longitude = null;

				int groupPosition = params[0];
				if (groupPosition == 6) {

					// 绑定打印室
					buildItem(
							getResources().getStringArray(R.array.copy_name),
							getResources().getStringArray(R.array.copy_address),
							getResources()
							.getStringArray(R.array.copy_latitude),
							getResources().getStringArray(
									R.array.copy_longitude), groupPosition,
									true);
					// 绑定修车
					buildItem(
							getResources().getStringArray(R.array.repair_name),
							getResources().getStringArray(
									R.array.repair_address),
									getResources().getStringArray(
											R.array.repair_latitude),
											getResources().getStringArray(
													R.array.repair_longitude), groupPosition,
													true);
					// 绑定配钥匙
					buildItem(
							getResources()
							.getStringArray(R.array.make_key_name),
							getResources().getStringArray(
									R.array.make_key_address),
									getResources().getStringArray(
											R.array.make_key_latitude),
											getResources().getStringArray(
													R.array.make_key_longitude), groupPosition,
													true);

				} else {
					if (groupPosition == 0) {
						name = getResources().getStringArray(
								R.array.category_one_name);
						address = getResources().getStringArray(
								R.array.category_one_address);
						latitude = getResources().getStringArray(
								R.array.category_one_latitude);
						longitude = getResources().getStringArray(
								R.array.category_one_longitude);
					} else if (groupPosition == 1) {
						name = getResources().getStringArray(
								R.array.category_two_name);
						address = getResources().getStringArray(
								R.array.category_two_address);
						latitude = getResources().getStringArray(
								R.array.category_two_latitude);
						longitude = getResources().getStringArray(
								R.array.category_two_longitude);
					} else if (groupPosition == 2) {
						name = getResources().getStringArray(
								R.array.category_three_name);
						address = getResources().getStringArray(
								R.array.category_three_address);
						latitude = getResources().getStringArray(
								R.array.category_three_latitude);
						longitude = getResources().getStringArray(
								R.array.category_three_longitude);
					} else if (groupPosition == 3) {
						name = getResources().getStringArray(
								R.array.category_four_name);
						address = getResources().getStringArray(
								R.array.category_four_address);
						latitude = getResources().getStringArray(
								R.array.category_four_latitude);
						longitude = getResources().getStringArray(
								R.array.category_four_longitude);
					} else if (groupPosition == 4) {
						name = getResources().getStringArray(
								R.array.category_five_name);
						address = getResources().getStringArray(
								R.array.category_five_address);
						latitude = getResources().getStringArray(
								R.array.category_five_latitude);
						longitude = getResources().getStringArray(
								R.array.category_five_longitude);
					} else if (groupPosition == 5) {
						name = getResources().getStringArray(
								R.array.category_six_name);
						address = getResources().getStringArray(
								R.array.category_six_address);
						latitude = getResources().getStringArray(
								R.array.category_six_latitude);
						longitude = getResources().getStringArray(
								R.array.category_six_longitude);
					}

					buildItem(name, address, latitude, longitude,
							groupPosition, false);
				}

			} catch (Exception e) {
				rt = -1;
			}
			return rt;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (result != -1) {
				routeListView.expandGroup(result);
				routeListView.setSelectedGroup(result);
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	public class lxAddChildContentTask extends
	AsyncTask<Integer, Integer, Integer> {

		@Override
		protected void onPreExecute() {
			if (dialog == null) {
				dialog = ProgressDialog.show(MyRouteActivity.this, "",
						"数据加载中...");
			} else {
				dialog.show();
			}
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int rt = params[0];
			try {
				String[] name = null;
				String[] address = null;
				String[] latitude = null;
				String[] longitude = null;

				int groupPosition = params[0];

				if (groupPosition == 0) {
					name = getResources().getStringArray(
							R.array.lxcategory_one_name);
					address = getResources().getStringArray(
							R.array.lxcategory_one_address);
					latitude = getResources().getStringArray(
							R.array.lxcategory_one_latitude);
					longitude = getResources().getStringArray(
							R.array.lxcategory_one_longitude);
				} else if (groupPosition == 1) {
					name = getResources().getStringArray(
							R.array.lxcategory_two_name);
					address = getResources().getStringArray(
							R.array.lxcategory_two_address);
					latitude = getResources().getStringArray(
							R.array.lxcategory_two_latitude);
					longitude = getResources().getStringArray(
							R.array.lxcategory_two_longitude);
				} else if (groupPosition == 2) {
					name = getResources().getStringArray(
							R.array.lxcategory_three_name);
					address = getResources().getStringArray(
							R.array.lxcategory_three_address);
					latitude = getResources().getStringArray(
							R.array.lxcategory_three_latitude);
					longitude = getResources().getStringArray(
							R.array.lxcategory_three_longitude);
				} else if (groupPosition == 3) {
					name = getResources().getStringArray(
							R.array.lxcategory_four_name);
					address = getResources().getStringArray(
							R.array.lxcategory_four_address);
					latitude = getResources().getStringArray(
							R.array.lxcategory_four_latitude);
					longitude = getResources().getStringArray(
							R.array.lxcategory_four_longitude);
				} 
				lxbuildItem(name, address, latitude, longitude,
						groupPosition, false);


			} catch (Exception e) {
				rt = -1;
			}
			return rt;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (result != -1) {
				lxrouteListView.expandGroup(result);
				lxrouteListView.setSelectedGroup(result);
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	/**
	 * 构建item数据
	 * 
	 * @param needSort
	 *            :是否需要排序
	 * */
	public void buildItem(String[] name, String[] address, String[] latitude,
			String[] longitude, int position, boolean needSort) {

		ArrayList<MapModel.ChildModel> item = new ArrayList<MapModel.ChildModel>();
		int distance = 0;
		for (int j = 0; j < name.length; j++) {
			if (needSort) {
				double lat = Double.valueOf(latitude[j]);
				double lng = Double.valueOf(longitude[j]);
				if (hasCurrentPoint) {
					distance = MapTools.GetDistance(myLatitude, myLongitude,
							lat, lng);
				} else {
					distance = 0;
				}
				item.add(new MapModel.ChildModel(name[j], address[j],
						latitude[j], longitude[j], distance));
			} else {
				item.add(new MapModel.ChildModel(name[j], address[j],
						latitude[j], longitude[j], 0));
			}
		}

		// 是否要排序
		if (needSort) {
			// 生活服务类数据显示
			Collections.sort(item, new ComparatorDistance());
			if (childList[position] == null) {
				childList[position] = item;
			} else {
				childList[position].addAll(item);
			}
		} else {
			childList[position] = item;
		}
	}
	public void lxbuildItem(String[] name, String[] address, String[] latitude,
			String[] longitude, int position, boolean needSort) {

		ArrayList<MapModel.ChildModel> item = new ArrayList<MapModel.ChildModel>();
		int distance = 0;
		for (int j = 0; j < name.length; j++) {
			if (needSort) {
				double lat = Double.valueOf(latitude[j]);
				double lng = Double.valueOf(longitude[j]);
				if (hasCurrentPoint) {
					distance = MapTools.GetDistance(myLatitude, myLongitude,
							lat, lng);
				} else {
					distance = 0;
				}
				item.add(new MapModel.ChildModel(name[j], address[j],
						latitude[j], longitude[j], distance));
			} else {
				item.add(new MapModel.ChildModel(name[j], address[j],
						latitude[j], longitude[j], 0));
			}
		}

		// 是否要排序
		if (needSort) {
			// 生活服务类数据显示
			Collections.sort(item, new ComparatorDistance());
			if (lxchildList[position] == null) {
				lxchildList[position] = item;
			} else {
				lxchildList[position].addAll(item);
			}
		} else {
			lxchildList[position] = item;
		}
	}

	// 根据距离排序
	public class ComparatorDistance implements Comparator {

		@Override
		public int compare(Object ob1, Object ob2) {
			MapModel.ChildModel model1 = (ChildModel) ob1;
			MapModel.ChildModel model2 = (ChildModel) ob2;

			if (model1.distance >= model2.distance) {
				return 1;
			}
			return -1;
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.iv_navi_back) {
			routeMode = -1;
			toMainActivity();
		} else if (v.getId() == R.id.btn_start) {
			buttonType = 0;
			startButton.setBackgroundResource(R.color.location_select);
			endButton.setBackgroundResource(R.color.location_select_2);
		} else if (v.getId() == R.id.btn_end) {
			buttonType = 1;
			startButton.setBackgroundResource(R.color.location_select_2);
			endButton.setBackgroundResource(R.color.location_select);
		} else if (v.getId() == R.id.btn_drive) {
			routeMode = 0;
			driveButton.setImageResource(R.drawable.bg_drive_pressed);
			walkButton.setImageResource(R.drawable.bg_walk_normal);
		} else if (v.getId() == R.id.btn_walk) {
			routeMode = 1;
			driveButton.setImageResource(R.drawable.bg_drive_normal);
			walkButton.setImageResource(R.drawable.bg_walk_pressed);
		} else if (v.getId() == R.id.btn_start_route) {
			toMainActivity();
		}
	}

}
