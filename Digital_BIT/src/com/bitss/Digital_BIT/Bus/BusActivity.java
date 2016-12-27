package com.bitss.Digital_BIT.Bus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.View.MyChoosePlaceDialog;
import com.bitss.Digital_BIT.View.MyChoosePlaceDialog.OnChooseSetListener;
import com.bitss.Digital_BIT.View.MyDateTimePickerDialog;
import com.bitss.Digital_BIT.View.MyDateTimePickerDialog.OnDateTimeSetListener;

public class BusActivity extends CustomBaseActivity {
	private static final String TAG = BusActivity.class.getSimpleName();

	private Context context;
	private String mtitleName = "校车查询";

	private ImageView changePlace;
	private TextView showTime;
	private ImageView changeTime;

	// 数据库控制类
	private BusManager busManager;
	private DateManager dateManager;

	// listview相关控件
	private LinkedList<BusInfo> dataList = new LinkedList<BusInfo>();
	private BusAdapter adapter;
	private ListView busListView;

	private String[] type; // date_type的类型（工作日、周五、周末、假期）
	private String[] num_week; // 周一到周日
	private String[] place_name; // 起点、终点位置

	// 当天的时间类型--起点--终点
	private static SimpleDateFormat format; // 对发给服务器的时间进行格式化
	public static String date; // 用来发给服务器的时间：2012-12-29
	private String date_type; // date_type的类型（工作日、周五、周末、假期）

	private String start_place; // 起点位置
	private String end_place;// 终点位置

	// 线程获取网络数据
	private Dialog dialog;

	// 改变起点、终点。默认：中关村（1）--> 良乡（0）
	private static final int DEFAULT_START_POSITION = 1;
	private static final int DEFAULT_END_POSITION = 0;

	private static final long ten_mins = 600000; // 10分钟
	private static final long five_mins = 300000; // 5分钟
	private static final long fifteen_mins = 900000; // 15分钟

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schoolbus);

		mTvNaviTitle.setText(mtitleName);
		init();
		setListener();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void init() {
		context = this;

		changePlace = (ImageView) findViewById(R.id.busplacechoose);
		showTime = (TextView) findViewById(R.id.bustimeshow);
		changeTime = (ImageView) findViewById(R.id.bustimechoose);
		busListView = (ListView) findViewById(R.id.busdatalistview);

		num_week = getResources().getStringArray(R.array.num_week); // 星期几
		type = getResources().getStringArray(R.array.date_type); // bus类型
		place_name = getResources().getStringArray(R.array.bus_place); // 起点、终点

		// 显示当前时间（yyyy年mm月dd日星期x）
		Calendar currentDate = Calendar.getInstance();
		showTime.setText(getNowTime(currentDate));

		busManager = new BusManager(this);
		dateManager = new DateManager(this);
		date_type = dateManager.getDateType(currentDate); // 当天的校车类型

		// 发向服务器的时间
		format = new SimpleDateFormat("yyyy-MM-dd");
		date = format.format(currentDate.getTime());

		start_place = place_name[DEFAULT_START_POSITION];
		end_place = place_name[DEFAULT_END_POSITION];

		// 先从网络获取，失败后再取本地的数据
		new busAskTask(this).execute();

	}

	public void setListener() {
		// 改变:起点--终点
		changePlace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new MyChoosePlaceDialog(context, new OnChooseSetListener() {

					@Override
					public void onChooseSet(String start, String end) {
						changeData(start, end);
					}
				}).show();
			}
		});

		changeTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar currentDate = Calendar.getInstance();
				int curr_year = currentDate.get(Calendar.YEAR);
				int curr_month = currentDate.get(currentDate.MONTH);
				int curr_day = currentDate.get(Calendar.DAY_OF_MONTH);
				Log.i(TAG, "curr_year:" + curr_year + " curr_month:"
						+ curr_month + " curr_day:" + curr_day);

				new MyDateTimePickerDialog(context, curr_year, curr_month,
						curr_day, new OnDateTimeSetListener() {

							@Override
							public void onDateTimeSet(int year, int month,
									int day) {

								Calendar c = Calendar.getInstance();
								c.set(Calendar.YEAR, year);
								c.set(Calendar.MONTH, month);
								c.set(Calendar.DAY_OF_MONTH, day);
								showTime.setText(getNowTime(c));
								date = format.format(c.getTime()); // 修改发送服务器的时间

								date_type = dateManager.getDateType(c);
								changeData(start_place, end_place);
							}
						}).show();
			}
		});
	}

	public class busAskTask extends AsyncTask<Void, Void, Integer> {
		HttpBusAsker busAsker;

		public busAskTask(Context mContext) {
			Log.i(TAG, "start_place: " + start_place + " end_place: "
					+ end_place);
			busAsker = new HttpBusAsker(mContext, date, start_place, end_place);
		}

		@Override
		protected void onPreExecute() {
			// 弹出对话框提示正在加载
			dialog = ProgressDialog.show(BusActivity.this, "",
					"数据加载中，请稍候......");
		}

		@Override
		protected Integer doInBackground(Void... params) {

			int ans = busAsker.askForBusList();

			dataList.clear();

			if (ans == -1) { // 网络失败，只加载本地数据
				dataList.addAll(busManager.getBusInfoList(date_type,
						start_place, end_place));
			} else if (ans == 0) {// 网络请求成功，返回数据是0，依旧加载本地数据
				int num = busAsker.date_type - 1;
				date_type = type[num];
				dataList.addAll(busManager.getBusInfoList(date_type,
						start_place, end_place));
				Log.d("bus_test", "本地数据库数据个数：" + dataList.size());

			} else if (ans > 0) {// 网络请求成功，返回所有当日校车信息

				dataList.addAll(busAsker.busData);

				Log.d("bus_test", "校车数据总数：" + dataList.size());
			}

			return ans;
		}

		@Override
		protected void onPostExecute(Integer result) {

			if (result == -1) {
				// Toast.makeText(getApplicationContext(),
				// "网络获取失败!由于校车变动频繁,本地数据不一定正确!",
				// Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(), "显示本地数据",
						Toast.LENGTH_LONG).show();
			}

			if (result == 0) {
				Toast.makeText(getApplicationContext(), "校车情况无变动!显示常规校车班次!",
						Toast.LENGTH_LONG).show();
			}

			if (result > 0) {
				Toast.makeText(getApplicationContext(), "校车情况变动!显示变动后的校车班次!",
						Toast.LENGTH_LONG).show();
			}

			if (adapter == null) {
				adapter = new BusAdapter(BusActivity.this, dataList);
				busListView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}

			dialog.dismiss();

			if (dataList.size() == 0) {
				warnNotBus();
			}
		}
	}

	// 当天没校车安排
	public void warnNotBus() {
		Toast.makeText(this, "抱歉，今天没有校车安排", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 改变listview的数据
	 * */
	public void changeData(String start, String end) {

		dataList.clear();
		start_place = start;
		end_place = end;

		if (start_place.equals(place_name[4])
				|| end_place.equals(place_name[4])) { // place_name[4] =
														// 良乡大学城北--地铁站
			try {
				setMetro(start_place, end_place);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} else {
			new busAskTask(this).execute();
		}
	}

	/**
	 * 获取良乡到地铁站的通行车数据
	 * 
	 * busInfo; startPlace; endPlace; startTime; endTime;
	 * 
	 * @throws ParseException
	 * */
	public void setMetro(String start, String end) throws ParseException {
		dataList.clear();
		LinkedList<BusInfo> metroList = new LinkedList<BusInfo>();

		String m_start, m_end;
		String a_start, a_end;
		String e_start, e_end;

		// 获取当前时间，比较此时校车是否已经开走
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String currentTime = sdf.format(calendar.getTime());

		// 0代表良乡
		if (start.equals(place_name[0])) { // 良乡校区-->地铁站
			m_start = "6:35";
			m_end = "10:50";
			appentMetroBusData(sdf, m_start, m_end, currentTime, metroList,
					start, end);

			a_start = "11:35";
			a_end = "16:50";
			appentMetroBusData(sdf, a_start, a_end, currentTime, metroList,
					start, end);

			e_start = "17:50";
			e_end = "22:50";
			appentMetroBusData(sdf, e_start, e_end, currentTime, metroList,
					start, end);

		} else {
			// 地铁站到--良乡校区

			m_start = "6:30";
			m_end = "11:00";
			appentMetroBusData(sdf, m_start, m_end, currentTime, metroList,
					start, end);

			a_start = "11:45";
			a_end = "17:00";
			appentMetroBusData(sdf, a_start, a_end, currentTime, metroList,
					start, end);

			e_start = "17:45";
			e_end = "23:00";
			appentMetroBusData(sdf, e_start, e_end, currentTime, metroList,
					start, end);

		}

		dataList.addAll(metroList);
		if (adapter == null) {
			adapter = new BusAdapter(BusActivity.this, dataList);
			busListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 获取当前时间（yyyy年mm月dd日星期x）
	 * */
	public String getNowTime(Calendar date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		String ymd = sdf.format(date.getTime());
		String week = num_week[date.get(Calendar.DAY_OF_WEEK) - 1];

		StringBuffer result = new StringBuffer();
		result.append(ymd).append(getString(R.string.day_of_week)).append(week);

		return result.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		super.onKeyDown(keyCode, event);
		return true;
	}

	private void appentMetroBusData(SimpleDateFormat sdf, String start_t,
			String end_t, String currentTime, LinkedList<BusInfo> metroList,
			String start, String end) throws ParseException {
		for (long i = sdf.parse(start_t).getTime(); i <= sdf.parse(end_t)
				.getTime();) {
			String startTime = sdf.format(new Date(i));
			String endTime = sdf.format(new Date(i + five_mins));

			int bus_status = 0;
			// 判断校车是否已经开走
			if (isItToday(date)) {
				int result = startTime.compareTo(currentTime);

				if (result == 0) { // 校车即将出发，status = 1
					bus_status = 1;
				} else if (result > 0) { // 需要等待较长时间，status = 2
					bus_status = 2;
				}
			} else {
				bus_status = 2;
			}

			BusInfo busInfo = new BusInfo("1辆(50座)", start, end, startTime,
					endTime, bus_status);
			metroList.add(busInfo);
			i = i + fifteen_mins;
		}
	}

	public static boolean isItToday(String date) {
		Calendar c = Calendar.getInstance();
		if (date.equals(format.format(c.getTime()))) {
			return true;
		}
		return false;
	}
}
