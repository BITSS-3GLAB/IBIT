package com.bitss.Digital_BIT.Admission;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Admission.SearchPickerDialog.OnSearchChangeSetListener;
import com.bitss.Digital_BIT.Admission.database.AdmissionDBManager;
import com.inqbarna.tablefixheaders.TableFixHeaders;

/**
 * 招生计划、分数查询页面
 * */
public class AdmissionSearchActivity extends CustomBaseActivity {

	private Context context;
	private AdmissionDBManager mDBManager;
	private String searchType; // 招生计划或者历年分数

	private TextView mEmptyTextView; // 显示提示信息
	private TextView mYearTextView; // 年份
	private TextView mProvinceTextView;// 省份
	private TextView mTypeTextView;// 理工、文史

	private TableFixHeaders tableView;
	private TableAdapter adapter;

	private ArrayList<ArrayList<String>> searchResultList = new ArrayList<ArrayList<String>>();

	private String year;
	private String province;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admission_search);

		context = this;
		mDBManager = new AdmissionDBManager(this);
		searchType = getIntent().getExtras().getString("search_type");

		mTvNaviTitle.setText((searchType.equals("plan") ? getString(R.string.str_admission_plan)
		        : getString(R.string.str_admission_score)));
		mIvNaviShare.setImageDrawable(getResources().getDrawable(R.drawable.bg_navi_college_style));
		mIvNaviShare.setVisibility(View.VISIBLE);
		mIvNaviShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SearchPickerDialog(context, new OnSearchChangeSetListener() {

					@Override
					public void onDataChangeSet(String year, String province, String type) {
						searchData(year, province, type);
					}
				}).show();
			}
		});

		mYearTextView = (TextView) findViewById(R.id.tv_admission_year);
		mProvinceTextView = (TextView) findViewById(R.id.tv_admission_province);
		mTypeTextView = (TextView) findViewById(R.id.tv_admission_type);
		mEmptyTextView = (TextView) findViewById(R.id.tv_empty_view);
		tableView = (TableFixHeaders) findViewById(R.id.admission_table);
		tableView.setVisibility(View.GONE);
		mEmptyTextView.setText(getString(R.string.str_admission_search_empty_warning));
		mEmptyTextView.setVisibility(View.VISIBLE);
	}

	public void searchData(String year, String province, String type) {
		this.year = year;
		this.province = province;
		this.type = type;

		mYearTextView.setText(year);
		mProvinceTextView.setText(province);
		mTypeTextView.setText(type);

		// query new data
		new searchTask().execute();
	}

	// 查询线程
	public class searchTask extends AsyncTask<Void, Void, Void> {

		ArrayList<ArrayList<String>> resultList;

		@Override
		protected void onPreExecute() {
			tableView.setVisibility(View.GONE);
			mEmptyTextView.setText(getString(R.string.str_admission_searching));
			mEmptyTextView.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (searchType.equals("plan")) {
				resultList = mDBManager.getAdmissionPlan(year, province, type);
			} else {
				resultList = mDBManager.getAdmissionScore(year, province, type);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (resultList.size() > 0) {
				searchResultList.clear();
				searchResultList.addAll(resultList);

				adapter = new TableAdapter(context, searchResultList);
				tableView.setAdapter(adapter);

				mEmptyTextView.setVisibility(View.INVISIBLE);
				tableView.setVisibility(View.VISIBLE);
			} else {
				mEmptyTextView.setText(getString(R.string.str_admission_searching_not_result));
			}
		}
	}
}
