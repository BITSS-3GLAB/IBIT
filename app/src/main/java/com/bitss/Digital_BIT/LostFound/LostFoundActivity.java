package com.bitss.Digital_BIT.LostFound;

import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.ParamUtil;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class LostFoundActivity extends Activity implements OnClickListener {

	private Context context;
	private static final String TITLE = "失物招领";

	private ImageView backImg;
	private ImageView writeImg;

	private PullToRefreshGridView listView;
	private LostFoundAdapter adapter;
	private View mListEmptyView; // 空列表时显示的提示
	private TextView emptyTextView; // 动态修上面的文案

	private LinkedList<LostFoundModel> dateList = new LinkedList<LostFoundModel>();
	private boolean isOnRefreshing = false; // 是否在获取数据

	private static final int REQUEST_EDIT = 10022; // 跳转到编辑页面
	private static final int GET_NEW_SUCCESS = 10023; // 获取新数据
	private static final int GET_OLD_SUCCESS = 10024;// 获取旧数据
	private static final int GET_FAILURE = 10025;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// 把刷新状态取消
			isOnRefreshing = false;
			cancelRefreshing();

			switch (msg.what) {
			case GET_NEW_SUCCESS:
				LinkedList<LostFoundModel> newList = (LinkedList<LostFoundModel>) msg.obj;
				for (int i = newList.size() - 1; i >= 0; i--) {
					dateList.addFirst(newList.get(i));
				}

				if (newList.size() != 0) {
					// 此时需要拼接list
					int size = newList.size();
					if (size > 0 && dateList.size() > size) {
						if (dateList.get(size - 1).id - dateList.get(size).id > 1) {
							while (dateList.size() > size)
								dateList.remove(dateList.get(size));
						}
					}
					adapter.notifyDataSetChanged();
				}

				// 无数据，界面提示没数据
				if (dateList.size() == 0) {
					emptyTextView.setText("暂时没有失物招领信息");
				}

				break;
			case GET_OLD_SUCCESS:
				LinkedList<LostFoundModel> oldList = (LinkedList<LostFoundModel>) msg.obj;
				if (oldList.size() == 0) {
					msgToast("没有更多数据咯!");
				} else {
					// 直接加到list尾部
					for (LostFoundModel model : oldList) {
						dateList.add(model);
					}
					adapter.notifyDataSetChanged();
				}

				// 无数据，界面提示没数据
				if (dateList.size() == 0) {
					emptyTextView.setText("暂时没有失物招领信息");
				}

				break;
			case GET_FAILURE:
				// 无数据，界面提示网络问题
				if (dateList.size() == 0) {
					emptyTextView.setText(getString(R.string.news_empty));
				}

				msgToast("抱歉，获取失败，请稍候再试...");
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void cancelRefreshing() {
		isOnRefreshing = false;
		if (listView.isRefreshing()) {
			listView.onRefreshComplete();
		}
		return;
	}

	public void msgToast(String msg) {
		Toast.makeText(context, msg, 0).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == REQUEST_EDIT) {
			// 如果发布成功，自动刷新
			onRefresh(true);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lost_found);
		context = this;

		((TextView) findViewById(R.id.tv_navi_title)).setText(TITLE);
		backImg = (ImageView) findViewById(R.id.iv_navi_back);
		writeImg = (ImageView) findViewById(R.id.iv_navi_write);
		backImg.setOnClickListener(this);
		writeImg.setOnClickListener(this);

		listView = (PullToRefreshGridView) findViewById(R.id.lf_listview);
		listView.setMode(Mode.BOTH);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase arg0) {
				onRefresh(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase arg0) {
				onRefresh(false);
			}
		});
		listView.setRefreshingLabel("数据加载中，请稍候...");
		listView.getRefreshableView().setSelector(new ColorDrawable());

		// empty view
		mListEmptyView = LayoutInflater.from(this).inflate(R.layout.list_empty,
				null);
		emptyTextView = (TextView) (mListEmptyView
				.findViewById(R.id.tv_emptyTitle));
		emptyTextView.setText(getString(R.string.news_is_loading));
		listView.setEmptyView(mListEmptyView);

		adapter = new LostFoundAdapter(this, dateList);
		listView.setAdapter(adapter);

		LinkedList<LostFoundModel> list = new FileHandle(context).readFile();
		if (list.size() > 0) {
			dateList.clear();
			dateList.addAll(list);
			adapter.notifyDataSetChanged();
		}

		onRefresh(true);
	}

	/**
	 * 获取服务器数据
	 * 
	 * @param isFront
	 *            ，获取新数据还是旧数据
	 * **/
	public void onRefresh(boolean isFront) {

		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(this)) {
			Utils.showToast(this, Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

		if (!isOnRefreshing) {
			isOnRefreshing = true;
			listView.setRefreshing();
			emptyTextView.setText(getString(R.string.news_is_loading));
			long id;
			if (isFront) {
				id = (dateList.size() == 0) ? 0 : dateList.get(0).id;
			} else {
				id = (dateList.size() == 0) ? 0 : dateList.getLast().id;
			}

			new Thread(new getRunnable(id, isFront)).start();
		} else {
			return;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.iv_navi_back:
			finish();
			break;
		case R.id.iv_navi_write:
			Intent intent = new Intent(this, LostFoundEditActivity.class);
			startActivityForResult(intent, REQUEST_EDIT);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		if (dateList.size() > 0) {
			int end = 10;
			if (dateList.size() < 10)
				end = dateList.size();

			FileHandle fileHandle = new FileHandle(context);
			fileHandle.writeFile(new LinkedList<LostFoundModel>(dateList
					.subList(0, end)));
		}

		super.onDestroy();
	}

	private HttpPost post;
	private HttpPost cloudpost;// 云端处理器

	private class getRunnable implements Runnable {

		long id;
		boolean isFront;
		String url = ParamUtil.SERVER_URL;
		String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING + "servlet/";

		public getRunnable(long id, boolean isFront) {
			Log.e("net_data", "request id = " + id);
			this.id = id;
			this.isFront = isFront;

			url += isFront ? "GetUpdateLostFoundInfo" : "GetPostLostFoundInfo";
			cloudurl += isFront ? "GetUpdateLostFoundInfo"
					: "GetPostLostFoundInfo";
			post = new HttpPost(url);
			cloudpost = new HttpPost(cloudurl);
		}

		@Override
		public void run() {
			Message message = new Message();
			JSONObject param = new JSONObject();
			try {
				param.put("ID", Long.valueOf(id));

				DefaultHttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 30000);
				cloudpost.addHeader("Content-Type", "application/json");
				cloudpost.addHeader("charset", HTTP.UTF_8);
				cloudpost.setEntity(new StringEntity(param.toString(),
						HTTP.UTF_8));
				System.out.println(param.toString());

				post.addHeader("Content-Type", "application/json");
				post.addHeader("charset", HTTP.UTF_8);
				post.setEntity(new StringEntity(param.toString(), HTTP.UTF_8));

				HttpResponse cloudresponse = client.execute(cloudpost);// 请求云端服务器
				if (cloudresponse.getStatusLine().getStatusCode() == 200) {
					message.what = isFront ? GET_NEW_SUCCESS : GET_OLD_SUCCESS;
					String contempString = EntityUtils.toString(cloudresponse
							.getEntity());
					System.out.println(contempString);
					message.obj = handleNetDate(contempString);
				} else {
					HttpResponse response = client.execute(post);// 请求本地
					if (response.getStatusLine().getStatusCode() == 200) {
						message.what = isFront ? GET_NEW_SUCCESS
								: GET_OLD_SUCCESS;
						message.obj = handleNetDate(EntityUtils
								.toString(response.getEntity()));
					} else {
						message.what = GET_FAILURE;
					}

				}
			} catch (Exception e) {
				message.what = GET_FAILURE;
			} finally {
				handler.sendMessage(message);
			}
		}
	}

	/**
	 * 处理服务器返回的数据
	 * */
	public LinkedList<LostFoundModel> handleNetDate(String json) {
		LinkedList<LostFoundModel> netDate = new LinkedList<LostFoundModel>();
		JSONArray jsonArray;
		try {
			JSONObject object = new JSONObject(json);
			jsonArray = object.getJSONArray("LostFoundInfoJSONArray");
			for (int i = jsonArray.length() - 1; i >= 0; i--) {
				JSONObject temp = jsonArray.getJSONObject(i);

				long id = temp.getLong("ID");
				String url = temp.getString("PicUrl");
				url = url.substring(26);
				url = "http://123.57.41.214:8080/" + url;
				String desc = temp.getString("Description");
				String loc = temp.getString("Location");
				String cont = temp.getString("Contact");
				String time = temp.getString("Time");

				Log.e("net_data", "id = " + id + " url =  " + url + " loc =  "
						+ loc + " cont =  " + cont + " time =  " + time);

				LostFoundModel data = new LostFoundModel(id, url, desc, loc,
						cont, time);
				netDate.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return netDate;
	}

}
