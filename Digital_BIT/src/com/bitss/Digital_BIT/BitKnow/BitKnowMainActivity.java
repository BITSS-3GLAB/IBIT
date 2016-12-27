package com.bitss.Digital_BIT.BitKnow;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.BitKnow.BitKnowNetworkHandler;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class BitKnowMainActivity extends Activity implements OnClickListener {

	private Context myActivity;

	private ImageView backToGuide;
	private PullToRefreshListView pullToRefreshListView;

	private View bitknow_message, bitknow_add;

	private RelativeLayout left, right;
	private TextView suggest;
	private ImageView suggest_bar;
	private TextView lastest;
	private ImageView lastest_bar;

	private EditText search_keyWords;
	private ImageView search;

	private ImageView photo;
	private TextView name;
	private TextView time;

	private ImageView image1, image2, image3;

	private TextView content;

	private TextView label1, label2, label3;
	private TextView number_of_answer;

	private SharedPreferences settings;
	private String phone;

	// 排序的两种方式，zan表示推荐，time表示最新
	private final static String timeOrder = "time";
	private final static String suggestOrder = "suggest";

	private BitKnowMainAdapter BitKnowMainAdapter;
	private BitKnowHttpConnect mConnection;
	private static List<BitKnowMainData> mDatas = new ArrayList<BitKnowMainData>();
	private static boolean isDataChanged = false;
	private static final int DoRefresh = 1;
	private static final int OnMore = 0;
	private int number = 0;// 记录已经加载多少页

	// 此时下拉刷新是否在进行
	private boolean mIsRefresh = false;
	private String order = suggestOrder;
	private int position = -1;
	private String searchData = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitknow_main);
		position = getIntent().getIntExtra("position", -1);
		if (position == -1)
			searchData = getIntent().getStringExtra("search");
		init();

		try {
			getData(DoRefresh, "time");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		listeners();
	}

	private void init() {
		settings = getSharedPreferences("User", 0);
		phone = settings.getString(Constants.USER_PHONE, "");
		backToGuide = (ImageView) findViewById(R.id.bitknow_main_back);
		bitknow_message = (View) findViewById(R.id.bitknow_message);
		bitknow_add = (View) findViewById(R.id.bitknow_add);

		left = (RelativeLayout) findViewById(R.id.left);
		right = (RelativeLayout) findViewById(R.id.right);
		suggest = (TextView) findViewById(R.id.suggest);
		suggest_bar = (ImageView) findViewById(R.id.suggest_bar);
		lastest = (TextView) findViewById(R.id.lastest);
		lastest_bar = (ImageView) findViewById(R.id.lastest_bar);

		search_keyWords = (EditText) findViewById(R.id.search_keywords);
		search = (ImageView) findViewById(R.id.search);

		photo = (ImageView) findViewById(R.id.photo);
		name = (TextView) findViewById(R.id.name);
		time = (TextView) findViewById(R.id.time);

		image1 = (ImageView) findViewById(R.id.image1);
		image2 = (ImageView) findViewById(R.id.image2);
		image3 = (ImageView) findViewById(R.id.image3);

		content = (TextView) findViewById(R.id.content);

		label1 = (TextView) findViewById(R.id.label1);
		label2 = (TextView) findViewById(R.id.label2);
		label3 = (TextView) findViewById(R.id.label3);
		number_of_answer = (TextView) findViewById(R.id.answerNumber);

		mConnection = new BitKnowHttpConnect(this);
		mConnection.setTimeOut(11000);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.bitknow_know_pull_to_refresh_listview);
		pullToRefreshListView.setShowIndicator(false);
		pullToRefreshListView
				.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				try {
					doRefresh();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				try {
					onMore();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});

		myActivity = this;

		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("data", mDatas.get(position - 1));
				intent.putExtra(Constants.KEY_ITEM_POSITION, position);
				intent.setClass(BitKnowMainActivity.this,
						BitKonwDetailActivity.class);
				startActivityForResult(intent, Constants.ANSWER_COUNT_REFRESH);
			}
		});
	}

	private void doRefresh() throws UnsupportedEncodingException {
		if (!mIsRefresh) {
			mIsRefresh = true;
			getData(DoRefresh, order.toString());
		} else {
			return;
		}
	}

	private void onMore() throws UnsupportedEncodingException {
		if (!mIsRefresh) {
			mIsRefresh = true;
			getData(OnMore, order.toString());
		} else {
			return;
		}
	}

	private void getData(int refresh, String type)
			throws UnsupportedEncodingException {
		if (refresh == DoRefresh) {
			// 下拉刷新
			JSONObject json = new JSONObject();
			try {
				json.put("offset", "0");
				json.put("number", "10");
				json.put("key", searchData);
				json.put("orderBy", order);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			mConnection.doPost(Constants.BITKNOWTEST_SERVER_STRING
					+ "SendQuestionsInfo",
					Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "SendQuestionsInfo", json,
					new BitKnowNetworkHandler() {
						@Override
						public void onSuccess(String str) {
							try {
								JSONObject json = new JSONObject(str);
								System.out.println(str);
								// Log.v("json", str);
								boolean isSucess = json.getBoolean("success");
								if (isSucess) {
									mDatas.clear();
									JSONObject result = json
											.getJSONObject("result");
									JSONArray records = result
											.getJSONArray("records");
									// Log.v(TAG, str);
									for (int i = 0; i < records.length(); i++) {
										JSONObject item = records
												.getJSONObject(i);
										BitKnowMainData data = new BitKnowMainData(
												item);
										mDatas.add(data);
									}
								}

								if (BitKnowMainAdapter == null) {
									BitKnowMainAdapter = new BitKnowMainAdapter(
											myActivity, mDatas);
									pullToRefreshListView
											.setAdapter(BitKnowMainAdapter);
								} else {
									BitKnowMainAdapter.notifyDataSetChanged();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								number = 0;
								pullToRefreshListView.onRefreshComplete();
								mIsRefresh = false;
							}
						}

						@Override
						public void onFailure() {
							pullToRefreshListView.onRefreshComplete();
							Toast.makeText(myActivity, "网络异常",
									Toast.LENGTH_SHORT).show();
							mIsRefresh = false;
						}
					});
		} else {
			// 上滑加载更多
			JSONObject json = new JSONObject();
			try {
				json.put("offset", mDatas.size());
				json.put("number", "10");
				json.put("key", searchData);
				json.put("orderBy", order);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			mConnection.doPost(Constants.BITKNOWTEST_SERVER_STRING
					+ "SendQuestionsInfo",
					Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "SendQuestionsInfo", json,
					new BitKnowNetworkHandler() {

						@Override
						public void onSuccess(String str) {
							try {
								JSONObject json = new JSONObject(str);
								System.out.println(str);
								boolean isSucess = json.getBoolean("sucess");
								if (isSucess) {
									JSONObject result = json
											.getJSONObject("result");
									JSONArray records = result
											.getJSONArray("records");
									for (int i = 0; i < records.length(); i++) {
										JSONObject item = records
												.getJSONObject(i);
										BitKnowMainData data = new BitKnowMainData(
												item);
										mDatas.add(data);
									}
								}

								BitKnowMainAdapter.notifyDataSetChanged();
								Toast.makeText(myActivity, "成功加载更多数据",
										Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								pullToRefreshListView.onRefreshComplete();
								mIsRefresh = false;
							}
						}

						@Override
						public void onFailure() {
							pullToRefreshListView.onRefreshComplete();
							Toast.makeText(myActivity, "网络异常",
									Toast.LENGTH_SHORT).show();
							mIsRefresh = false;
						}
					});
		}
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.bitknow_main_back:
			// Intent guideIntent = new Intent();
			// guideIntent.setClass(this, GuideItemView.class);
			// startActivity(guideIntent);
			finish();
		case R.id.left:
		case R.id.suggest:
			suggest.setTextColor(getResources().getColor(R.color.font_4));
			suggest_bar.setVisibility(View.VISIBLE);
			lastest.setTextColor(getResources().getColor(R.color.font_3));
			lastest_bar.setVisibility(View.GONE);
			order = suggestOrder;
			try {
				getData(DoRefresh, order.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case R.id.right:
		case R.id.lastest:
			suggest.setTextColor(getResources().getColor(R.color.font_3));
			suggest_bar.setVisibility(View.GONE);
			lastest.setTextColor(getResources().getColor(R.color.font_4));
			lastest_bar.setVisibility(View.VISIBLE);
			order = timeOrder;
			try {
				getData(DoRefresh, order.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		case R.id.bitknow_message:
			phone = settings.getString(Constants.USER_PHONE, "");
			if (phone.equals("")) {
				Utils.haveNotLogin(BitKnowMainActivity.this);
			} else {
				Intent messageIntent = new Intent();
				messageIntent.setClass(this, BitKnowMessageActivity.class);
				startActivity(messageIntent);
				break;
			}
		case R.id.bitknow_add:
			phone = settings.getString(Constants.USER_PHONE, "");
			if (phone.equals("")) {
				Utils.haveNotLogin(BitKnowMainActivity.this);
			} else {
				Intent addIntent = new Intent();
				addIntent.setClass(this, KnowDiscribeQActivity.class);
				startActivityForResult(addIntent, 5);
			}
			break;
		case R.id.search:
			searchData = search_keyWords.getText().toString();
			try {
				getData(DoRefresh, order.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void listeners() {
		backToGuide.setOnClickListener(this);
		bitknow_message.setOnClickListener(this);
		bitknow_add.setOnClickListener(this);
		left.setOnClickListener(this);
		suggest.setOnClickListener(this);
		right.setOnClickListener(this);
		lastest.setOnClickListener(this);
		search.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 5) {
			try {
				getData(DoRefresh, order.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if (requestCode == Constants.ANSWER_COUNT_REFRESH) {
			if (data != null) {
				int pos = data.getIntExtra(Constants.KEY_ITEM_POSITION, -1) - 1;
				int answer = data.getIntExtra(Constants.KEY_ANSWER_COUNT, -1);

				if (pos >= 0 && answer != -1) {
					mDatas.get(pos).setNum(answer);
					BitKnowMainAdapter.notifyDataSetChanged();
				}
			}
		}
	}

}
