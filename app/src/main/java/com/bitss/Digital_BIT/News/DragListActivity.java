package com.bitss.Digital_BIT.News;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bitss.Digital_BIT.BaseActivity;
import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.ReLogin;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.JsonHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;

public class DragListActivity extends CustomBaseActivity {

	private static final String TAG = DragListActivity.class.getSimpleName();

	private static final NewsTypeModel SUBSCRIBE_MODEl = new NewsTypeModel(
			"200", "已订阅");
	private static final NewsTypeModel UNSUBSCRIBE_MODEl = new NewsTypeModel(
			"201", "未订阅");

	private BaseActivity mAct;

	private String[] newsId;
	private String[] newsName;

	private static final List<NewsTypeModel> NEWS_TYPE = new ArrayList<NewsTypeModel>();
	private List<NewsTypeModel> list = new ArrayList<NewsTypeModel>(); // 绑定的list
	private List<NewsTypeModel> subscribeList = new ArrayList<NewsTypeModel>(); // 已订阅新闻的列表
	private List<NewsTypeModel> unsubscribeList = new ArrayList<NewsTypeModel>();// 未订阅新闻的列表
	
	private DragListAdapter adapter = null;
	private DragListView dragListView;
	private ReLogin reLogin;
	private boolean isUpdate = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drag_list_activity);

		mAct = this;

		initActionBar();
		initData();

		dragListView = (DragListView) findViewById(R.id.drag_list);
		adapter = new DragListAdapter(this, list);
		dragListView.setAdapter(adapter);

		reLogin = new ReLogin(mAct);
	}
	
	public void onResume() {
		super.onResume();
		if (isUpdate) {
			getSubscribeList();
		}
	}

	private void initActionBar() {
		mTvNaviTitle.setText("编辑栏目");
		mTvRight.setText("确定");
		mTvRight.setVisibility(View.VISIBLE);
		mTvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveSubscribeState();
			}
		});
	}

	@Override
	public void backAction() {
		onBack();
	}

	/**
	 * 初始化数据
	 * */
	public void initData() {
		newsName = getResources().getStringArray(R.array.newsName);
		newsId = getResources().getStringArray(R.array.newsId);
		NEWS_TYPE.clear();
		for (int i = 0; i < newsId.length; i++) {
			NEWS_TYPE.add(new NewsTypeModel(newsId[i], newsName[i]));
		}
		unsubscribeList.clear();
		unsubscribeList.addAll(NEWS_TYPE);
		list.add(SUBSCRIBE_MODEl);
		list.add(UNSUBSCRIBE_MODEl);
		list.addAll(NEWS_TYPE);
	}
	
	private void setData(JSONArray data) throws JSONException {
		unsubscribeList.clear();
		subscribeList.clear();
		unsubscribeList.addAll(NEWS_TYPE);
		for (int i = 0; i < data.length(); i++) {
			JSONObject obj = data.getJSONObject(i);
			int newsType = obj.getInt("newsType");
			NewsTypeModel model = NEWS_TYPE.get(newsType);
			model.setId(obj.getInt("id"));
			subscribeList.add(model);
			unsubscribeList.remove(model);
		}
		
		list.clear();
		list.add(SUBSCRIBE_MODEl);
		list.addAll(subscribeList);
		list.add(UNSUBSCRIBE_MODEl);
		list.addAll(unsubscribeList);
	}

	private void getSubscribeList() {
		HttpRequest request = new HttpRequest.Builder().url(
				"/news/front/news/concernList").build();
		HttpClient.getInstance(mApp).get(request.getUrl(),
				new JsonHttpResponseHandler() {

					@Override
					public void onFailure(Exception e) {
						super.onFailure(e);
						Utils.showToast(mApp, "获取订阅列表失败");
					}

					@Override
					public void onSuccess(int status, JSONArray response) {
						try {
							setData(response);
							isUpdate = false;
							adapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onSuccess(int status, String jsonString) {
						Logger.i(TAG, jsonString);
						if (status == 401) {
							new ReLogin(mAct).showDialog();
						} else {
							Utils.showToast(mApp, "获取订阅列表失败");
						}
					}

				});
	}
	
	public void onBack() {
		if (isModify()) {
			showExitConfirmDialog();
		} else {
			finish();
		}
	}

	private boolean isModify() {
		List<NewsTypeModel> subscribeTmp = new ArrayList<NewsTypeModel>();
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).equals(UNSUBSCRIBE_MODEl)) {
				break;
			} else {
				subscribeTmp.add(list.get(i));
			}
		}

		return !(subscribeTmp.containsAll(subscribeList)
				&& subscribeList.containsAll(subscribeTmp));
	}

	private static final int MSG_NOT_LOGIN = 0x00;
	private static final int MSG_ERROR = 0x01;
	private static final int MSG_SUCCESS = 0x10;
	private Handler mHandler = new MyHandler(this);

	private boolean subscribe(String url) {
		try {
			Response response = HttpClient.getInstance(mApp).put(url, new FormBody.Builder().build());
			int status = response.code();
			if (status == 201) {
				return true;
			} else if (status == 401) {
				mHandler.sendEmptyMessage(MSG_NOT_LOGIN);
				return false;
			} else {
				mHandler.sendEmptyMessage(MSG_ERROR);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MSG_ERROR);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MSG_ERROR);
			return false;
		}
	}

	private boolean unsubscribe(String url) {
		try {
			Response response = HttpClient.getInstance(mApp).delete(url, null);
			int status = response.code();
			if (status == 200) {
				return true;
			} else if (status == 401) {
				mHandler.sendEmptyMessage(MSG_NOT_LOGIN);
				return false;
			} else {
				mHandler.sendEmptyMessage(MSG_ERROR);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			mHandler.sendEmptyMessage(MSG_ERROR);
			return false;
		}
	}

	private void saveSubscribeState() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NewsTypeModel> subscribeTmp = new ArrayList<NewsTypeModel>();
				for (int i = 1; i < list.size(); i++) {
					if (list.get(i).equals(UNSUBSCRIBE_MODEl)) {
						break;
					} else {
						subscribeTmp.add(list.get(i));
					}
				}

				for (int i = 0; i < subscribeTmp.size(); i++) {
					if (!subscribeList.contains(subscribeTmp.get(i))) {
						if (!subscribe(String.format("%s%s%s", Constants.SERVER_URL,
								"/news/front/news/concern/",
								subscribeTmp.get(i).newsType))) {
							return;
						}
					}
				}
				for (int i = 0; i < subscribeList.size(); i++) {
					if (!subscribeTmp.contains(subscribeList.get(i))) {
						if (!unsubscribe(String.format("%s%s%d", Constants.SERVER_URL,
								"/news/front/news/concern/",
								subscribeList.get(i).id))) {
							return;
						}
					}
				}
				
				HttpRequest request = new HttpRequest.Builder().url(
						"/news/front/news/concernList").build();
				try {
					Response response = HttpClient.getInstance(mApp).get(request.getUrl());
					switch (response.code()) {
					case 200:
						try {
							JSONArray array = new JSONArray(response.body().string());
							setData(array);
							mHandler.sendEmptyMessage(MSG_SUCCESS);
						} catch (Exception e) {
							e.printStackTrace();
							mHandler.sendEmptyMessage(MSG_ERROR);
						}
						
						break;
					case 401:
						mHandler.sendEmptyMessage(MSG_NOT_LOGIN);
						break;
					default:
						mHandler.sendEmptyMessage(MSG_ERROR);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(MSG_ERROR);
				}
			}
		}).start();
	}

	private void showExitConfirmDialog() {
		AlertDialog dialog = new AlertDialog.Builder(mAct)
				.setMessage("订阅未保存，是否离开")
				.setNegativeButton("离开", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mAct.finish();
					}
				}).setPositiveButton("保存", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						saveSubscribeState();
					}

				}).create();
		dialog.show();
	}

	private static class MyHandler extends Handler {

		private WeakReference<DragListActivity> mWeakReference;

		public MyHandler(DragListActivity act) {
			this.mWeakReference = new WeakReference<DragListActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				if (mWeakReference.get() != null) {
					mWeakReference.get().adapter.notifyDataSetChanged();
					Utils.showToast(mWeakReference.get(), "修改成功");
				}
				break;
			case MSG_NOT_LOGIN:
				if (mWeakReference.get() != null) {
					mWeakReference.get().reLogin.showDialog();
				}
				break;
			case MSG_ERROR:
				if (mWeakReference.get() != null) {
					Utils.showToast(mWeakReference.get(), "修改失败");
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	public static class DragListAdapter extends ArrayAdapter<NewsTypeModel> {

		public DragListAdapter(Context context, List<NewsTypeModel> objects) {
			super(context, 0, objects);
		}

		// public List<NewsTypeModel> getList() {
		// return list;
		// }

		@Override
		public boolean isEnabled(int position) {
			if (getItem(position).newsType.equals("200")
					|| getItem(position).newsType.equals("201")) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = convertView;
			String idStr = getItem(position).newsType;

			if (idStr.equals("200") || idStr.equals("201")) {
				view = LayoutInflater.from(getContext()).inflate(
						R.layout.drag_list_item_tag, parent, false);
			} else {
				view = LayoutInflater.from(getContext()).inflate(
						R.layout.drag_list_item, parent, false);

			}
			TextView textView = (TextView) view
					.findViewById(R.id.drag_list_item_text);
			textView.setText(getItem(position).newsName);

			return view;
		}
	}
}