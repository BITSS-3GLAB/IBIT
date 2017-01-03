package com.bitss.Digital_BIT.SecondHandMarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.View.MyGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.viewpagerindicator.UnderlinePageIndicator;

public class SecondHandActivity extends CustomBaseActivity {
	private ImageView iv_back;
	private EditText et_search;
	private ViewPager viewPager;
	private ImageView viewPagerPhoto, iv_search;
	private UnderlinePageIndicator secondHandLinePageIndicator;
	private DisplayImageOptions displayImageOptions;
	private MyGridView secondHandGridView;
	private TextView tv_publish;
	private int currentItem = 0;
	private ArrayList<HashMap<String, Object>> secondHandArrayList;
	private ScheduledExecutorService scheduledExecutorService;
	private SecondHandMarketHttpConnection mConnection;
	SharedPreferences settings;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			// viewPager.setCurrentItem(currentItem);
			// secondHandLinePageIndicator.notifyDataSetChanged();
			secondHandLinePageIndicator.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};

	private ArrayList<String> imageUrl = new ArrayList<String>();
	private Context myActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_hand);
		mTvNaviTitle.setText(getResources().getString(
				R.string.second_hand_market));
		settings = getSharedPreferences("User", 0);
		init();
		SimpleAdapter saImageItems = new SimpleAdapter(this,
				secondHandArrayList, R.layout.secondhandmarket_item,
				new String[] { "ItemImage", "Name" }, new int[] {
						R.id.SecondHandItemImage, R.id.SecondHandItemText });
		secondHandGridView.setAdapter(saImageItems);
		secondHandGridView.setOnItemClickListener(new ItemClickListener());
		listener();
	}

	protected void onStop() {
		// 当Activity不可见的时候停止切换
		if (scheduledExecutorService != null)
			scheduledExecutorService.shutdown();
		super.onStop();
	}

	protected void onStart() {

		super.onStart();
	}

	private void initType() {

	}

	private void init() {

		iv_back = (ImageView) findViewById(R.id.iv_navi_back);
		et_search = (EditText) findViewById(R.id.phone_search_edittext);
		et_search.setHint("请输入物品名称");
		iv_search = (ImageView) findViewById(R.id.phone_btnforsearch);
		viewPager = (ViewPager) findViewById(R.id.dialog_classifyPager);
		tv_publish = (TextView) findViewById(R.id.tv_choose);
		tv_publish.setVisibility(View.VISIBLE);
		secondHandLinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.secondhandindictor);
		mConnection = new SecondHandMarketHttpConnection(this);
		getData();

		secondHandGridView = (MyGridView) findViewById(R.id.secondhandgridview);
		secondHandArrayList = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_all);
		map.put("Name", "全部");
		secondHandArrayList.add(map);
		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_bike);
		map.put("Name", "代步工具");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_book);
		map.put("Name", "书籍资料");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_clothes);
		map.put("Name", "服装鞋包");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_electronic);
		map.put("Name", "数码专区");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_basketball);
		map.put("Name", "体育用品");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_free);
		map.put("Name", "爱心赠送");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_music);
		map.put("Name", "音乐乐器");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_ticket);
		map.put("Name", "票券");
		secondHandArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.icon_other);
		map.put("Name", "其他");
		secondHandArrayList.add(map);

		displayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.uil_ic_stub)
				.showImageForEmptyUri(R.drawable.uil_ic_empty)
				.showImageOnFail(R.drawable.uil_ic_error).cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.build();
		myActivity = this;

	}

	private void listener() {
		iv_back.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (settings.getString(Constants.USER_PHONE, "").equals("")) {
					Utils.haveNotLogin(SecondHandActivity.this);
				} else {
					Intent intent = new Intent(SecondHandActivity.this,
							SecondHandPublishActivity.class);
					// JSONObject jsonObj = new JSONObject();
					// SecondHandMarketData data = null;
					// try {
					// jsonObj.put("campus", 0);
					// jsonObj.put("id", 2);
					// jsonObj.put("title", "自行车");
					// jsonObj.put("price", 100);
					// jsonObj.put("time", "2014-05-30 12:00:10");
					// jsonObj.put("picUrl1",
					// "http://photocdn.sohu.com/20111123/Img326603573.jpg");
					// jsonObj.put("picUrl2",
					// "http://photocdn.sohu.com/20111123/Img326603573.jpg");
					// jsonObj.put("picUrl3", "");
					// data = new SecondHandMarketData(jsonObj);
					// } catch (JSONException e) {
					// e.printStackTrace();
					// }
					// intent.putExtra("data", data);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

					// Intent intent = new Intent()
					// .setClass(SecondHandActivity.this,
					// PersonalInfoActivity.class);
					//
					// startActivity(intent);
				}

			}
		});

		iv_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (et_search.getText().toString().equals("")) {
					Toast.makeText(SecondHandActivity.this, "请输入需查询内容", 0)
							.show();

				} else {
					Bundle data = new Bundle();
					data.putInt("position", -1);
					data.putString("search", et_search.getText().toString());
					Intent intent = new Intent(SecondHandActivity.this,
							SecondHandMarketListViewActivity.class);
					intent.putExtras(data);
					startActivity(intent);
				}

			}
		});

	}

	private class ItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {

			Bundle data = new Bundle();
			data.putInt("position", position);
			Intent intent = new Intent(SecondHandActivity.this,
					SecondHandMarketListViewActivity.class);
			intent.putExtras(data);
			startActivity(intent);

		}
	}

	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				System.out.println("currentItem: " + currentItem);
				currentItem = (currentItem + 1) % 3;
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
			}
		}
	}

	private void getData() {
		mConnection.doGet(Constants.TEST_SERVER_STRING + "SendWelcomePics",
				Constants.BITKNOWTEST_CLOUDSERVER_STRING + "SendWelcomePics",
				new SecondHandMarketNetworkHandler() {

					@Override
					public void onSuccess(String str) {
						try {
							System.out.println(str);
							JSONObject json = new JSONObject(str);
							boolean isSuccess = json.getBoolean("success");
							if (isSuccess) {
								JSONObject result = json
										.getJSONObject("result");
								JSONArray urls = result
										.getJSONArray("titles_urls");
								for (int i = 0; i < urls.length(); i++) {
									String url = urls.getJSONObject(i)
											.getString("url");
									System.out.printf(url);
									String cloudurl;
									cloudurl = url.replace("123.57.41.214 ",
											"123.57.41.214:8080");
									imageUrl.add(cloudurl);
								}
								viewPager.setAdapter(new ImagePagerAdapter(
										imageUrl, SecondHandActivity.this));
								secondHandLinePageIndicator
										.setViewPager(viewPager);
								secondHandLinePageIndicator
										.setOnPageChangeListener(new OnPageChangeListener() {

											@Override
											public void onPageSelected(int arg0) {
												// stub
												currentItem = arg0;
											}

											@Override
											public void onPageScrolled(
													int arg0, float arg1,
													int arg2) {
												// stub

											}

											@Override
											public void onPageScrollStateChanged(
													int arg0) {
												// stub

											}
										});

								scheduledExecutorService = Executors
										.newSingleThreadScheduledExecutor();
								// 当Activity显示出来后，每两秒钟切换一次图片显示
								scheduledExecutorService.scheduleAtFixedRate(
										new ScrollTask(), 1, 3,
										TimeUnit.SECONDS);

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure() {
						Toast.makeText(myActivity, "网络异常", Toast.LENGTH_SHORT)
								.show();
					}
				});
	}
}
