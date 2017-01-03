package com.bitss.Digital_BIT.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.StringUtil;
import com.bitss.Digital_BIT.Util.TimeFormatUtil;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.View.MyTimePickerDialog;
import com.bitss.Digital_BIT.View.MyTimePickerDialog.OnMyTimeSetListener;

public class BITMeetingActivity extends CustomBaseActivity {

	private Context context;
	private String mtitleName = "会议安排";

	private ListView mlistView;
	private ArrayList<LinkedList<MeetingData>> mListItems;
	private HttpMeetingAsker httpasker;
	protected int newsType = 0;
	private MyAdapter adapter;

	private int selectMonth = 1;

	// 记录listview中的每一项是否点击
	private ArrayList<boolean[]> isCurrentItems;
	// 顶部左右移动的选项
	private ImageAdapter imageAdapter;

	private TimeFormatUtil timeFormat;
	private RemindManager manager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meetinglist);
		if (manager == null) {
			manager = new RemindManager(this);
		}
		timeFormat = new TimeFormatUtil();
		init();

		// actionbar
		mTvNaviTitle.setText(mtitleName);

	}

	public void onDestroy() {
		new MeetingFileAsker(context).writeFile(mListItems);
		super.onDestroy();
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

	protected void init() {
		context = this;
		isCurrentItems = new ArrayList<boolean[]>();
		mListItems = new MeetingFileAsker(this).readFile();
		if (mListItems == null || mListItems.size() != 3) {
			mListItems = new ArrayList<LinkedList<MeetingData>>();
			for (int i = 0; i < 3; i++) {
				mListItems.add(new LinkedList<MeetingData>());
			}
		}
		for (int i = 0; i < 3; i++) {
			isCurrentItems.add(new boolean[30]);
			for (int count = 0; count < 10; count++) {
				isCurrentItems.get(i)[count] = false;
			}
		}
		httpasker = new HttpMeetingAsker();
		mlistView = (ListView) findViewById(R.id.schoolmeetinglist);

		adapter = new MyAdapter(newsType, this, mlistView);
		mlistView.setAdapter(adapter);

		if (Utils.isNetworkAvailable(this)) {
			new GetDataTask().execute();
		} else {
			Utils.showToast(this, Constants.ERROR_NETWORK_UNAVAILABLE);
		}

		// 顶部滑动选项
		Gallery g = (Gallery) findViewById(R.id.gallery);
		imageAdapter = new ImageAdapter(this);
		g.setAdapter(imageAdapter);
		g.setSelection(2);
		g.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(
					@SuppressWarnings("rawtypes") AdapterView parent, View v,
					int position, long id) {
				selectMonth = position % 3;
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		// 装载3个选项的图片
		private Integer[] mImageIds = { R.drawable.meetingheadbaralpha,
				R.drawable.meetingheadbaralpha, R.drawable.meetingheadbaralpha };
		// 选项的文字
		private String[] mStringIds = { getString(R.string.meeting_last_two),
				getString(R.string.meeting_last),
				getString(R.string.meeting_now) };

		public ImageAdapter(Context c) {
			this.inflater = LayoutInflater.from(c);
		}

		public int getCount() {
			// return mImageIds.length;
			return 3;
		}

		public void notifyDataSetChanged(int albumId) {
			selectMonth = albumId % mImageIds.length;
			super.notifyDataSetChanged();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View cellView = inflater.inflate(R.layout.meetinggallerycell, null);

			TextView textViewCell = (TextView) cellView
					.findViewById(R.id.meetinggallerytextview);
			textViewCell.setText(mStringIds[position % mImageIds.length]);

			return cellView;
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			int ans = -1;
			for (int i = 0; i < 3; i++) {
				ans = httpasker.askForMeeting(mListItems.get(i), i);
			}
			return ans;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// Call onRefreshComplete when the list has been refreshed.
			if (result >= 0) {
				adapter.notifyDataSetChanged();
			} else {
				HttpErrorToast.Show(context);
			}
			super.onPostExecute(result);
		}
	}

	class MyAdapter extends BaseAdapter {

		public LinkedList<MeetingData> meetingData;
		private LayoutInflater inflater;

		@SuppressWarnings("unused")
		private ListView listView;
		private int id;
		Context context;

		private HashMap<String, String> dayOfWeek = new HashMap<String, String>();
		private String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "June",
				"July", "Aug", "Sept", "Oct", "Nov", "Dec" };

		public MyAdapter(int _newsType, Context context, ListView _listView) {
			newsType = _newsType;
			this.context = context;
			this.inflater = LayoutInflater.from(context);
			listView = _listView;

			dayOfWeek.put("一", "MON");
			dayOfWeek.put("二", "TUE");
			dayOfWeek.put("三", "WED");
			dayOfWeek.put("四", "THU");
			dayOfWeek.put("五", "FRI");
			dayOfWeek.put("六", "SAT");
			dayOfWeek.put("日", "SUN");
		}

		@SuppressWarnings("unchecked")
		public int getCount() {
			id = 2 - selectMonth;
			if (mListItems == null) {
				return 0;
			}
			meetingData = (LinkedList<MeetingData>) mListItems.get(id).clone();
			return meetingData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View myView = inflater.inflate(R.layout.layout_meeting, parent,
					false);

			RelativeLayout meetingOutlineLayout = (RelativeLayout) myView
					.findViewById(R.id.meeting_info_outline);
			TextView week = (TextView) myView.findViewById(R.id.meeting_week);
			TextView day = (TextView) myView.findViewById(R.id.meeting_day);
			TextView month = (TextView) myView.findViewById(R.id.meeting_month);

			TextView title = (TextView) myView.findViewById(R.id.meeting_title);
			TextView time = (TextView) myView.findViewById(R.id.meeting_time);
			TextView place = (TextView) myView.findViewById(R.id.meeting_place);

			// setting remind time button
			ImageView setRemindTime = (ImageView) myView
					.findViewById(R.id.btn_set_remind);
			setRemindTime.setVisibility(View.INVISIBLE); // if meeting is
															// overtime that
															// invisible

			// detail of meeting and default invisibility
			final LinearLayout meetingDetailLayout = (LinearLayout) myView
					.findViewById(R.id.meeting_detail_layout);
			TextView detailTitle = (TextView) myView
					.findViewById(R.id.meeting_detail_title);
			TextView detailPerson = (TextView) myView
					.findViewById(R.id.meeting_detail_person);
			meetingDetailLayout.setVisibility(View.GONE);

			// set data and listener
			MeetingData data = meetingData.get(position);
			String date = data.MeetingDate; // 2013年12月6日
			week.setText(dayOfWeek.get(data.DayOfWeek));
			day.setText(date.substring(date.indexOf("月") + 1, date.indexOf("日"))
					+ "th");
			month.setText(months[Integer.parseInt(date.substring(
					date.indexOf("年") + 1, date.indexOf("月"))) - 1]);

			final String name = data.MeetingName;
			final String place_str = data.Place;
			title.setText(name);
			time.setText(data.Time);
			place.setText(place_str);

			detailTitle.setText(data.MeetingName);
			detailPerson.setText(data.PeopleInclude);

			meetingOutlineLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (meetingDetailLayout.getVisibility() == View.VISIBLE) {
						meetingDetailLayout.setVisibility(View.GONE);
					} else {
						meetingDetailLayout.setVisibility(View.VISIBLE);
					}

				}
			});

			// build the date time: 2013年12月8日14:30
			final String dateTime = data.Year + date + data.Time;
			Log.e("meeting", "meeting time:" + dateTime);

			if (timeFormat.formatToLong(dateTime) > new Date().getTime()) {
				setRemindTime.setVisibility(View.VISIBLE);
				setRemindTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						selectTime(name, place_str, dateTime);
					}
				});
			}

			return myView;
		}
	}

	/**
	 * 弹出提醒选择框
	 * */
	public void selectTime(final String name, final String place,
			final String dateTime) {
		new MyTimePickerDialog(context, new OnMyTimeSetListener() {

			@Override
			public void onMyTimeSet(String ahead_minute) {

				long time = timeFormat.formatToLong(dateTime);
				long remind_time = time - timeFormat.minFormat(ahead_minute);

				if (manager == null) {
					manager = new RemindManager(context);
				}
				long id = manager.insertData(name, place, time, remind_time);

				// 设置闹铃
				Intent mIntent = new Intent(
						StringUtil.ACTION_MEETING_REMIND_STRING);
				mIntent.putExtra("type", "normal");
				mIntent.putExtra("remind_id", String.valueOf(id));
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						context, (int) id, mIntent, 0);
				AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, remind_time, pendingIntent);
			}
		}).show();
	}
}
