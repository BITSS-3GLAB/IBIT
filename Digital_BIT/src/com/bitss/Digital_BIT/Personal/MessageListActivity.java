package com.bitss.Digital_BIT.Personal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Personal.PersonalInfoActivity.Module;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.ReLogin;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.JsonHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MessageListActivity extends CustomBaseActivity {

	private static final String TAG = MessageListActivity.class.getSimpleName();

	private Module module;

	private PullToRefreshListView mListView;
	private TextView mEmptyView;

	private MessageListAdapter mAdapter;
	private List<Message> mData;
	private int page = 1;
	
	private ReLogin reLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		module = (Module) intent.getSerializableExtra("module");
		if (module == null) {
			finish();
		}

		setContentView(R.layout.fragment_listview);
		mListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
		mEmptyView = (TextView) findViewById(R.id.tv_empty);
		mListView.setEmptyView(mEmptyView);
		mEmptyView.setText("没有消息");

		mData = new ArrayList<Message>();
		mAdapter = new MessageListAdapter(mApp, mData);
		mListView.setAdapter(mAdapter);
		mListView.setShowIndicator(false);
		mListView.setMode(Mode.BOTH);
		
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				refresh();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				loadMore();
			}
		});

		mTvNaviTitle.setText(module.getName());
		
		reLogin = new ReLogin(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mData.isEmpty()) {
			refresh();
		}
	}

	private void refresh() {
		HttpRequest request = new HttpRequest.Builder().url(
				String.format("%s%s?page=%d",
						"/message/front/personalMessages/", module.getType(),
						page)).build();
		HttpClient.getInstance(mApp).get(request.getUrl(),
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(Exception e) {
						super.onFailure(e);
						
						Utils.showToast(mApp, "获取消息失败");
						mListView.onRefreshComplete();
					}

					@Override
					public void onSuccess(int status, JSONArray response) {
						super.onSuccess(status, response);
						try {
							List<Message> tmp = new ArrayList<Message>();
							for (int i = 0; i < response.length(); i++) {
								JSONObject obj = response.getJSONObject(i);

								Message message = new Message();
								message.setContent(obj.getString("content"));
								message.setContent(obj.getString("pubTime"));
								tmp.add(message);
							}

							mData.clear();
							mData.addAll(tmp);
							mAdapter.notifyDataSetChanged();
							page = 1;
						} catch (Exception e) {
							e.printStackTrace();
						}
						mListView.onRefreshComplete();
					}

					@Override
					public void onSuccess(int status, String jsonString) {
						super.onSuccess(status, jsonString);

						Logger.i(TAG, jsonString);
						if (status == 401) {
							reLogin.showDialog();
						} else {
							Utils.showToast(mApp, "获取消息失败");
						}
						mListView.onRefreshComplete();
					}

				});
	}

	private void loadMore() {
		HttpRequest request = new HttpRequest.Builder().url(
				String.format("%s%s?page=%d",
						"/message/front/personalMessages/", module.getType(),
						page + 1)).build();
		HttpClient.getInstance(mApp).get(request.getUrl(),
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(Exception e) {
						super.onFailure(e);
						
						mListView.onRefreshComplete();
						Utils.showToast(mApp, "获取消息失败");
					}

					@Override
					public void onSuccess(int status, JSONArray response) {
						super.onSuccess(status, response);
						try {
							for (int i = 0; i < response.length(); i++) {
								JSONObject obj = response.getJSONObject(i);

								Message message = new Message();
								message.setContent(obj.getString("content"));
								message.setContent(obj.getString("pubTime"));
								mData.add(message);
							}

							mAdapter.notifyDataSetChanged();
							if (response.length() == 0) {
								Utils.showToast(mApp, "没有更多数据");
							} else {
								page++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						mListView.onRefreshComplete();
					}

					@Override
					public void onSuccess(int status, String jsonString) {
						super.onSuccess(status, jsonString);

						Logger.i(TAG, jsonString);
						if (status == 401) {
							reLogin.showDialog();
						} else {
							Utils.showToast(mApp, "获取消息失败");
						}
						mListView.onRefreshComplete();
					}

				});
	}
}
