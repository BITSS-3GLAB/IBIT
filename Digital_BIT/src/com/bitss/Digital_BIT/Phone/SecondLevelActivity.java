package com.bitss.Digital_BIT.Phone;

import java.util.List;
import java.util.Map;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.ParamUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SecondLevelActivity extends CustomBaseActivity {
	private ListView mListView;
	private Button mTitleButton;
	private Button mGoBackButton;
	private List<Map<String, Object>> mDataList;
	private String nameOfSuperior;
	private String pidSecondLevel;
	private ImageView searchBtn;
	private RelativeLayout mRelativeLayout;
	private EditText mEditText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitphone);

		Intent aIntent = this.getIntent();
		// 获取Title名称
		nameOfSuperior = aIntent.getStringExtra("nameFirstLevel");
		// 将上级类的sid作为本级类的pid
		pidSecondLevel = aIntent.getStringExtra("sidFirstLevel");

		init();
	}

	public void init() {
		mTvNaviTitle.setText(nameOfSuperior);
		mListView = (ListView) findViewById(R.id.phone_list1);
		// 以pidSecondLevel为pid在数据库中查询，返回所有子节点
		mDataList = new PhoneInfoManager(this).getDataListByPid(pidSecondLevel);
		MySecAdapter mySecAdapter = new MySecAdapter(this);
		mListView.setAdapter(mySecAdapter);
		mListView.setSelection(1);
	}

	/**
	 * 自定义ListView
	 */
	class MySecAdapter extends BaseAdapter {
		LayoutInflater inflater;

		public MySecAdapter(Context context) {
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
			// 在ListView的第一个元素位置上放入查询功能，下拉后显示
			if (position == 0) {
				convertView = inflater.inflate(R.layout.phone_listitem_search,
						null);
				mRelativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.phone_search_layout);
				mEditText = (EditText) convertView
						.findViewById(R.id.phone_search_edittext);

				// 点击后执行查询
				searchBtn = (ImageView) convertView
						.findViewById(R.id.phone_btnforsearch);
				searchBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent toSearchIntent = new Intent(
								SecondLevelActivity.this, SearchActivity.class);

						// 向查询界面传入用户输入
						toSearchIntent.putExtra("input", mEditText.getText()
								.toString());
						// 向查询界面传入当前界面的pid
						toSearchIntent.putExtra("pidForSearch", pidSecondLevel);
						// 向查询界面传入查询动作的来源标识
						toSearchIntent.putExtra("signOfSource", "2");

						startActivity(toSearchIntent);
					}
				});

			}
			// 在后面的位置放入信息内容，每个单位的名称、地址、邮箱等
			else {
				convertView = inflater.inflate(R.layout.phone_listitem_b, null);
				RelativeLayout layout = (RelativeLayout) convertView
						.findViewById(R.id.phone_listitem_layout_b);
				final TextView nameTextView = (TextView) convertView
						.findViewById(R.id.phone_listitem_nametxt_b);
				final TextView addressTextView = (TextView) convertView
						.findViewById(R.id.phone_listitem_addresstxt_b);
				final TextView emailTextView = (TextView) convertView
						.findViewById(R.id.phone_listitem_emailtxt_b);

				nameTextView.setText((String) mDataList.get(position - 1).get(
						PhoneColumns.KEY_DESCRIPTION));
				addressTextView.setText((String) mDataList.get(position - 1)
						.get(PhoneColumns.KEY_LOCATION));
				emailTextView.setText((String) mDataList.get(position - 1).get(
						PhoneColumns.KEY_EMAIL));

				// 点击可进入其子节点列表的新界面
				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String sidSecondLevel = (String) mDataList.get(
								position - 1).get(PhoneColumns.KEY_SID);

						Intent bIntent = new Intent(SecondLevelActivity.this,
								ThirdLevelActivity.class);
						// 向第三层传入所点击项的sid，其值在第三层中将被作为pid进行查询。
						bIntent.putExtra("nameSecondLevel",
								nameTextView.getText());
						// 向第三层传入所点击项的名称，即要显示的大类名称，如“软件学院”。
						bIntent.putExtra("sidSecondLevel", sidSecondLevel);
						startActivity(bIntent);
					}
				});
			}

			return convertView;
		}
	}
}
