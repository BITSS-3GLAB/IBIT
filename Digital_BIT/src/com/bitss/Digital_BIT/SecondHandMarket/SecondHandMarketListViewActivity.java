package com.bitss.Digital_BIT.SecondHandMarket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SecondHandMarketListViewActivity extends CustomBaseActivity
		implements OnClickListener {

	private Context myActivity;

	private PullToRefreshListView pullToRefreshListView;
	private TextView tv_time, tv_price;

	private ImageView iv_late_bar, iv_price_bar, iv_price;
	private RelativeLayout rl_late, rl_price;
	private TextView tv_info;

	private enum Type {
		time, price, priceD
	};

	private String[] classify = { "all", "bicycle", "book", "clothes",
			"digital", "basketball", "donation", "music", "coupon", "other" };
	//

	private SecondHandMarketListViewAdapter listViewAdapter;
	private SecondHandMarketHttpConnection mConnection;
	private static List<SecondHandMarketData> mData = new ArrayList<SecondHandMarketData>();
	private static boolean isDataChanged = false;
	private static final int DoRefresh = 1;
	private static final int OnMore = 0;
	private int number = 0;// 记录已经加载多少页
	/** 此时下拉刷新是否在进行 */
	private boolean mIsRefresh = false;
	private Type current = Type.time;
	private int position = -1;
	private String searchData = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_hand_listview);
		position = getIntent().getIntExtra("position", -1);
		if (position == -1)
			searchData = getIntent().getStringExtra("search");

		init();
		try {
			getData(DoRefresh, "time");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		listener();

	}

	private void init() {

		tv_price = (TextView) findViewById(R.id.buttonprice);
		tv_time = (TextView) findViewById(R.id.buttontime);

		rl_late = (RelativeLayout) findViewById(R.id.rl_late);
		rl_price = (RelativeLayout) findViewById(R.id.rl_price);

		iv_late_bar = (ImageView) findViewById(R.id.iv_late_bar);
		iv_price = (ImageView) findViewById(R.id.iv_price_jiantou);
		iv_price_bar = (ImageView) findViewById(R.id.iv_price_bar);
		tv_info = (TextView) findViewById(R.id.tv_product_info);

		mTvNaviTitle.setText(getResources().getString(
				R.string.second_hand_market));
		mConnection = new SecondHandMarketHttpConnection(this);
		mConnection.setTimeOut(11000);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.second_hand_pull_to_refresh_listview);
		pullToRefreshListView.setShowIndicator(false);
		pullToRefreshListView
				.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				try {
					doRefresh();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				try {
					onMore();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		myActivity = this;

		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.putExtra("data", mData.get(arg2 - 1));
				intent.setClass(SecondHandMarketListViewActivity.this,
						SecondHandDetailActivity.class);
				startActivity(intent);

			}
		});
	}

	private void listener() {
		rl_late.setOnClickListener(this);
		rl_price.setOnClickListener(this);
		tv_time.setOnClickListener(this);
		tv_price.setOnClickListener(this);
	}

	private void getData(int refresh, String type)
			throws UnsupportedEncodingException {
		if (refresh == DoRefresh) {
			// 刷新
			JSONObject json = new JSONObject();
			try {
				json.put("offset", "0");
				json.put("number", "10");
				json.put("key", searchData);
				if (position != -1)
					json.put("category", classify[position]);
				json.put("order", type);
			} catch (JSONException e) {

			}
			mConnection.doPost(Constants.TEST_SERVER_STRING + "SendGoodsInfo",
					Constants.BITKNOWTEST_CLOUDSERVER_STRING + "SendGoodsInfo",
					json, new SecondHandMarketNetworkHandler() {
						@Override
						public void onSuccess(String str) {
							try {
								JSONObject json = new JSONObject(str);
								System.out.println(str);
								boolean isSuccess = json.getBoolean("success");
								if (isSuccess) {
									mData.clear();
									JSONObject result = json
											.getJSONObject("result");
									JSONArray records = result
											.getJSONArray("records");
									for (int i1 = 0; i1 < records.length(); i1++) {
										JSONObject item = records
												.getJSONObject(i1);
										SecondHandMarketData data = new SecondHandMarketData(
												item);
										mData.add(data);
									}
								}

								// list = pullToRefreshListView
								// .getRefreshableView();
								if (mData.size() > 0)
									pullToRefreshListView
											.setVisibility(View.VISIBLE);
								else {
									tv_info.setVisibility(View.VISIBLE);
								}
								if (listViewAdapter == null) {
									listViewAdapter = new SecondHandMarketListViewAdapter(
											myActivity, mData);
									pullToRefreshListView
											.setAdapter(listViewAdapter);// 为ListView控件绑定适配器
								} else {
									listViewAdapter.notifyDataSetChanged();
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
			// 加载更多
			JSONObject json = new JSONObject();
			try {
				json.put("offset", mData.size());
				json.put("number", "10");
				json.put("key", searchData);
				if (position != -1)
					json.put("category", classify[position]);
				json.put("order", type);
			} catch (JSONException e) {

			}
			mConnection.doPost(Constants.TEST_SERVER_STRING + "SendGoodsInfo",
					Constants.BITKNOWTEST_CLOUDSERVER_STRING + "SendGoodsInfo",
					json, new SecondHandMarketNetworkHandler() {
						@Override
						public void onSuccess(String str) {
							try {
								JSONObject json = new JSONObject(str);
								System.out.println(str);
								boolean isSuccess = json.getBoolean("success");
								if (isSuccess) {
									// mData.clear();
									JSONObject result = json
											.getJSONObject("result");
									JSONArray records = result
											.getJSONArray("records");
									for (int i1 = 0; i1 < records.length(); i1++) {
										JSONObject item = records
												.getJSONObject(i1);
										SecondHandMarketData data = new SecondHandMarketData(
												item);
										mData.add(data);
									}
								}
								if (mData.size() > 0)
									pullToRefreshListView
											.setVisibility(View.VISIBLE);
								else {
									tv_info.setVisibility(View.VISIBLE);
								}

								listViewAdapter.notifyDataSetChanged();
								Toast.makeText(myActivity, "成功获取加载更多数据",
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

	private void doRefresh() throws UnsupportedEncodingException {
		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;
			getData(DoRefresh, current.toString());

		} else {
			return;
		}
	}

	private void onMore() throws UnsupportedEncodingException {
		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;
			getData(OnMore, current.toString());

		} else {
			return;
		}

	}

	public void onResume() {
		super.onResume();
		// if (mData == null || mData.size() == 0) {
		// try {
		// getData(DoRefresh);
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// } else {
		// if (isDataChanged) {
		// listViewAdapter.notifyDataSetChanged();
		// isDataChanged = false;
		// }
		// }
	}

	public static void setDataChanged() {
		isDataChanged = true;
	}

	public static void removeData(int pos) {
		mData.remove(pos);
	}

	public static SecondHandMarketData getAData(int pos) {
		return mData.get(pos);
	}

	@Override
	public void onClick(View arg0) {
		int Id = arg0.getId();
		switch (Id) {
		case R.id.rl_price:
		case R.id.buttonprice:
			pullToRefreshListView.setVisibility(View.GONE);
			tv_price.setTextColor(getResources().getColor(R.color.font_4));
			tv_time.setTextColor(getResources().getColor(R.color.font_3));
			iv_late_bar.setVisibility(View.GONE);
			iv_price_bar.setVisibility(View.VISIBLE);
			iv_price.setVisibility(View.VISIBLE);
			if (current == Type.priceD) {
				iv_price.setBackgroundResource(R.drawable.price_big);
				current = Type.price;
			} else {
				iv_price.setBackgroundResource(R.drawable.price_small);
				current = Type.priceD;
			}

			try {
				getData(DoRefresh, current.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;

		case R.id.rl_late:
		case R.id.buttontime:
			pullToRefreshListView.setVisibility(View.GONE);
			tv_time.setTextColor(getResources().getColor(R.color.font_4));
			tv_price.setTextColor(getResources().getColor(R.color.font_3));
			iv_late_bar.setVisibility(View.VISIBLE);
			iv_price_bar.setVisibility(View.GONE);
			iv_price.setVisibility(View.GONE);
			current = Type.time;
			try {
				getData(DoRefresh, current.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}

}
