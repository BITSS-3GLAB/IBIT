package com.bitss.Digital_BIT.Phone;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;

public class BITphonenumActivity extends CustomBaseActivity {
	private ListView mListView;
	private List<Map<String, Object>> mDataList;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		super.onKeyDown(keyCode, event);
		return true;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.abitphone);

		mTvNaviTitle.setText(getResources().getString(R.string.phone_search));
		init();
	}

	public void init() {

		mListView = (ListView) findViewById(R.id.phone_list2);

		// 以“1”为pid在数据库中查询，返回所有子节点
		mDataList = new PhoneInfoManager(this).getDataListByPid("1");

		MyAdapter mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setSelection(1);
	}

	/**
	 * 自定义ListView
	 */
	class MyAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mDataList.size() + 1;
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// 在第一个元素位置上放入查询功能，下拉后显示
			if (position == 0) {
				convertView = inflater.inflate(R.layout.phone_listitem_search,
						null);
				final EditText mEditText = (EditText) convertView
						.findViewById(R.id.phone_search_edittext);
				ImageView searchBtn = (ImageView) convertView
						.findViewById(R.id.phone_btnforsearch);

				searchBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent toSearchIntent = new Intent(
								BITphonenumActivity.this, SearchActivity.class);

						// 用户输入
						toSearchIntent.putExtra("input", mEditText.getText()
								.toString());
						// 当前界面的pid
						toSearchIntent.putExtra("pidForSearch", "1");
						// 查询动作的来源
						toSearchIntent.putExtra("signOfSource", "1");

						startActivity(toSearchIntent);
					}
				});
			}
			// 在后面的位置放入信息内容，即PhoneColumns.KEY_DESCRIPTION
			else {
				convertView = inflater.inflate(R.layout.phone_listitem_a,
						parent, false);
				RelativeLayout layout = (RelativeLayout) convertView
						.findViewById(R.id.phone_listitem_layout_a);
				final TextView sortTextView = (TextView) convertView
						.findViewById(R.id.phone_listitem_txt_a);

				sortTextView.setText((String) mDataList.get(position - 1).get(
						PhoneColumns.KEY_DESCRIPTION));

				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String sidFirstLevel = (String) mDataList.get(
								position - 1).get(PhoneColumns.KEY_SID);
						Intent intent = new Intent(context,
								SecondLevelActivity.class);
						// 向第二层传入所点击项的sid，其值在第二层中将被作为pid进行查询。
						intent.putExtra("sidFirstLevel", sidFirstLevel);
						// 向第二层传入所点击项的名称，即要显示的大类名称，如“学院”。
						intent.putExtra("nameFirstLevel",
								sortTextView.getText());
						startActivity(intent);
					}
				});
			}
			return convertView;
		}
	}
}