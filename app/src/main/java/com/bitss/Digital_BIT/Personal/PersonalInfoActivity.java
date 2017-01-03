package com.bitss.Digital_BIT.Personal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Guide.GuideActivity;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.ReLogin;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.JsonHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalInfoActivity extends CustomBaseActivity {

	private static final String TAG = PersonalMoreInfoActivity.class
			.getSimpleName();

	private ListView pullToRefreshListView;
	private TextView tv_sign, tv_userName;
	private RoundedImageView iv_photo;

	private List<Module> mData;
	private ArrayAdapter<Module> mAdapter;
	// private SecondHandMarketHttpConnection mConnection;
	// private static List<SecondHandMarketData> mData = new
	// ArrayList<SecondHandMarketData>();
	// private static List<SecondHandMarketData> mStoreData = new
	// ArrayList<SecondHandMarketData>();
	// private SecondHandMarketPersonalAdapter listViewAdapter;
	// private SecondHandMarketListViewAdapter mStoreAdapter;
	private SharedPreferences settings;

	private boolean isUserInfoUpdate = true;
	private boolean isMsgUpdate = true;
	private ReLogin reLogin;

	// private enum Type {
	// Goods, Store
	// };
	// private Type current = Type.Goods;

	public enum Module {

		BUS("MODULE_BUS", "班车通知"), FEEDBACK("MODULE_FEEDBACK", "反馈通知"), GOOD(
				"MODULE_GOOD", "二手市场"), LOSTFOUND("MODULE_LOSTFOUND", "失物招领"), NEWS(
				"MODULE_NEWS", "新闻订阅"), QUESTION("MODULE_QUESTION", "北理知道"), USER(
				"MODULE_USER", "用户管理"), VERSION("MODULE_VERSION", "版本更新");

		private String moduleName;
		private String moduleType;
		private int messageCount;

		private Module(String type, String name) {
			moduleType = type;
			moduleName = name;
		}

		public String getType() {
			return moduleType;
		}

		public String getName() {
			return moduleName;
		}

		public void setMsgCount(int count) {
			this.messageCount = count;
		}

		public int getMsgCount() {
			return messageCount;
		}

		@Override
		public String toString() {
			return new StringBuilder().append(moduleName).append(" ")
					.append(messageCount).toString();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_page);
		mTvNaviTitle.setText("我");
		Init();
		listener();
	}

	public void onResume() {
		super.onResume();
		if (isUserInfoUpdate) {
			getUserInfo();
		}
		if (isMsgUpdate) {
			getMessage();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (settings.getString(Constants.KEY_EMAIL, "").equals("")) {

			finish();
			Intent intent = new Intent().setClass(PersonalInfoActivity.this,
					GuideActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		// try {
		// getData("Goods");
		// getData("Store");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
	}

	private void Init() {
		mIvNaviShare.setVisibility(View.VISIBLE);
		mIvNaviShare.setImageResource(R.drawable.edit_personal);
		settings = mApp.getPreferences();
		reLogin = new ReLogin(this);
		// if (settings.getString(Constants.USER_PHONE, "").equals("")) {
		// Utils.haveNotLogin(PersonalInfoActivity.this);
		// finish();
		// }

		tv_sign = (TextView) findViewById(R.id.tv_mysign);
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		iv_photo = (RoundedImageView) findViewById(R.id.iv_icon);

		// mConnection = new SecondHandMarketHttpConnection(this);
		// mConnection.setTimeOut(11000);
		pullToRefreshListView = (ListView) findViewById(R.id.second_hand_pull_to_refresh_listview);
		pullToRefreshListView.setEmptyView(findViewById(R.id.empty_view));
		mData = new ArrayList<Module>();
		mAdapter = new ArrayAdapter<Module>(this, R.layout.item_list_simple,
				mData);
		pullToRefreshListView.setAdapter(mAdapter);
	}

	private void listener() {
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// if (current.equals(Type.Store)) {
				// Intent intent = new Intent();
				// intent.putExtra("data", mStoreData.get(position));
				// intent.setClass(PersonalInfoActivity.this,
				// SecondHandDetailActivity.class);
				// startActivity(intent);
				// }
				Intent intent = new Intent(PersonalInfoActivity.this,
						MessageListActivity.class);
				intent.putExtra("module", mData.remove(position));
				mAdapter.notifyDataSetChanged();
				startActivity(intent);
			}
		});

		mIvNaviShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent().setClass(
						PersonalInfoActivity.this,
						PersonalMoreInfoActivity.class);
				intent.putExtra("type", "edit");
				startActivity(intent);

			}
		});

	}

	private void getUserInfo() {
		HttpRequest request = new HttpRequest.Builder().url(
				"/user/front/userOnlineBasicInfo").build();
		HttpClient.getInstance(mApp).get(request.getUrl(),
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int status, JSONObject response) {
						switch (status) {
						case 200:
							try {
								String phone = response.getString("phone");
								String name = response.getString("name");
								String email = response.getString("email");
								tv_userName.setText(name);

								mApp.getPreferences().edit()
										.putString(Constants.USER_PHONE, phone)
										.putString(Constants.NICK_NAME, name)
										.putString(Constants.KEY_EMAIL, email)
										.commit();

								tv_sign.setText(response.getString("autograph"));
								String contempphotoUrl = response
										.getString("iconUrl");
								String photoUrl = contempphotoUrl.replace("\\",
										"/");
								ImageLoader.getInstance().displayImage(
										Constants.PHTOT_CLOUDSERVER_STRING
												+ photoUrl, iv_photo);
								isUserInfoUpdate = false;
							} catch (Exception e) {
								e.printStackTrace();
								isUserInfoUpdate = true;
							}
							break;
						case 401:
							reLogin.showDialog();
							isUserInfoUpdate = true;
							break;
						default:
							Utils.showToast(mApp, "获取用户信息失败");
							isUserInfoUpdate = true;
							break;
						}
					}

					@Override
					public void onSuccess(int status, String jsonString) {
						Logger.i(TAG, jsonString);
						if (status == 401) {
							reLogin.showDialog();
						}
						isUserInfoUpdate = true;
					}

					@Override
					public void onFailure(Exception e) {
						e.printStackTrace();
						Utils.showToast(mApp, "获取数据失败", Toast.LENGTH_LONG);
						isUserInfoUpdate = true;
					}
				});
	}

	private void getMessage() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<Module> listTmp = new ArrayList<Module>();
				for (Module module : Module.values()) {
					try {
						Response response = HttpClient.getInstance(mApp).get(
								String.format("%s%s%s", Constants.SERVER_URL,
										"/message/front/personalMessageNum/",
										module.getType()));
						switch (response.code()) {
						case 200:
							int num = Integer.valueOf(response.body().string());
//							if (num > 0) {
								module.setMsgCount(num);
								listTmp.add(module);
//							}
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
						return;
					}
				}
				mData.clear();
				mData.addAll(listTmp);
				mHandler.sendEmptyMessage(MSG_SUCCESS);
			}
		}).start();
	}

	private Handler mHandler = new MyHandler(this);

	private static final int MSG_SUCCESS = 0x00;
	private static final int MSG_ERROR = 0x10;
	private static final int MSG_NOT_LOGIN = 0x11;

	private static final class MyHandler extends Handler {
		private WeakReference<PersonalInfoActivity> mWeakReference;

		public MyHandler(PersonalInfoActivity act) {
			this.mWeakReference = new WeakReference<PersonalInfoActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				if (mWeakReference.get() != null) {
					Utils.showToast(mWeakReference.get().mApp, "获取消息成功");
					mWeakReference.get().mAdapter.notifyDataSetChanged();
					mWeakReference.get().isMsgUpdate = false;
				}
				break;
			case MSG_NOT_LOGIN:
				if (mWeakReference.get() != null) {
					mWeakReference.get().reLogin.showDialog();
					mWeakReference.get().isMsgUpdate = true;
				}
				break;
			case MSG_ERROR:
				if (mWeakReference.get() != null) {
					Utils.showToast(mWeakReference.get().mApp, "获取消息失败");
					mWeakReference.get().isMsgUpdate = true;
				}
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	}

	// private void getUserInfo() throws UnsupportedEncodingException {
	// JSONObject json = new JSONObject();
	// try {
	// json.put("phone", settings.getString(Constants.USER_PHONE, ""));
	// } catch (JSONException e) {
	//
	// }
	// mConnection.doPost(Constants.TEST_SERVER_STRING + "SendUserInfo",
	// Constants.BITKNOWTEST_CLOUDSERVER_STRING + "SendUserInfo",
	// json, new SecondHandMarketNetworkHandler() {
	//
	// @Override
	// public void onSuccess(String str) {
	// try {
	// JSONObject result = new JSONObject(str.toString());
	// if (result.getBoolean("success")) {
	// JSONObject pre_object = result
	// .getJSONObject("result");
	// JSONObject object = pre_object
	// .getJSONObject("userInfo");
	// tv_userName.setText(object
	// .getString("username"));
	//
	// tv_sign.setText(object.getString("sign"));
	// String contempphotoUrl = object
	// .getString("photoUrl");
	// String photoUrl = contempphotoUrl.replace(
	// "\\/", "/");
	// // ImageLoader.getInstance()
	// // .displayImage(
	// // Constants.PHTOT_CLOUDSERVER_STRING
	// // + object.getString(
	// // "photoUrl")
	// // .substring(1),
	// // iv_photo);
	// ImageLoader.getInstance().displayImage(
	// Constants.PHTOT_CLOUDSERVER_STRING
	// + photoUrl, iv_photo);
	//
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } finally {
	//
	// }
	// }
	//
	// @Override
	// public void onFailure() {
	//
	// }
	// });
	// }

	// private void getData(final String type) throws
	// UnsupportedEncodingException {
	//
	// // 刷新
	// JSONObject json = new JSONObject();
	// try {
	// json.put("phone", settings.getString(Constants.USER_PHONE, ""));
	//
	// } catch (JSONException e) {
	//
	// }
	// String url = Constants.TEST_SERVER_STRING;
	// String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING;
	// if (type.equals("Goods")) {
	// url += "SendUserGoodsInfo";
	// cloudurl += "SendUserGoodsInfo";
	// } else {
	// url += "SendCollectGoodsInfo";
	// cloudurl += "SendCollectGoodsInfo";
	// }
	// mConnection.doPost(url, cloudurl, json,
	// new SecondHandMarketNetworkHandler() {
	//
	// @Override
	// public void onSuccess(String str) {
	// try {
	// JSONObject json = new JSONObject(str);
	// System.out.println(str);
	// boolean isSuccess = json.getBoolean("success");
	// if (isSuccess) {
	// if (type.equals("Goods"))
	// mData.clear();
	// else {
	// mStoreData.clear();
	// }
	// JSONObject result = json
	// .getJSONObject("result");
	// JSONArray records = result
	// .getJSONArray("records");
	// for (int i1 = 0; i1 < records.length(); i1++) {
	// JSONObject item = records.getJSONObject(i1);
	// SecondHandMarketData data = new SecondHandMarketData(
	// item);
	// if (type.equals("Goods"))
	// mData.add(data);
	// else {
	// mStoreData.add(data);
	// }
	// }
	// }
	//
	// // list = pullToRefreshListView
	// // .getRefreshableView();
	// pullToRefreshListView.setVisibility(View.VISIBLE);
	// // if (listViewAdapter == null || mStoreAdapter ==
	// // null) {
	// if (current.equals(Type.Goods)) {
	// listViewAdapter = new SecondHandMarketPersonalAdapter(
	// PersonalInfoActivity.this, mData);
	// pullToRefreshListView
	// .setAdapter(listViewAdapter);// 为ListView控件绑定适配器
	// } else {
	// mStoreAdapter = new SecondHandMarketListViewAdapter(
	// PersonalInfoActivity.this, mStoreData);
	// pullToRefreshListView.setAdapter(mStoreAdapter);// 为ListView控件绑定适配器
	// }
	//
	// // } else {
	// // if (current.equals(Type.Goods)) {
	// // listViewAdapter.notifyDataSetChanged();
	// // } else {
	// // mStoreAdapter.notifyDataSetChanged();
	// // }
	// // }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } finally {
	//
	// }
	// }
	//
	// @Override
	// public void onFailure() {
	//
	// Toast.makeText(PersonalInfoActivity.this, "网络异常",
	// Toast.LENGTH_SHORT).show();
	//
	// }
	// });
	//
	// }
	//
	// public class SecondHandMarketPersonalAdapter extends BaseAdapter {
	// private Context context;
	// private List<SecondHandMarketData> thisData;
	// private SecondHandMarketHttpConnection mConnection;
	//
	// public SecondHandMarketPersonalAdapter(Context context,
	// List<SecondHandMarketData> mData) {
	// this.thisData = mData;
	// this.context = context;
	// mConnection.setTimeOut(11000);
	// }
	//
	// @Override
	// public int getCount() {
	// if (thisData == null)
	// return 0;
	// return thisData.size();
	// }
	//
	// @Override
	// public Object getItem(int arg0) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int arg0) {
	// return 0;
	// }
	//
	// @Override
	// public View getView(final int position, View convertView, ViewGroup arg2)
	// {
	//
	// ViewHolder holder = null;
	//
	// // if (convertView == null) {
	//
	// holder = new ViewHolder();
	// LayoutInflater flater = LayoutInflater.from(context);
	// convertView = flater.inflate(R.layout.item_personal_goods, null);
	//
	// holder.img_ObjImg = (ImageView) convertView
	// .findViewById(R.id.secondhanditemImage);
	// holder.txtVw_ObjName = (TextView) convertView
	// .findViewById(R.id.secondhanditemTitle);
	// holder.txtVw_ObjNameContent = (TextView) convertView
	// .findViewById(R.id.secondhanditemText1);
	// holder.txtVw_ObjDescribe = (TextView) convertView
	// .findViewById(R.id.secondhanditemText2);
	// holder.tv_price = (TextView) convertView
	// .findViewById(R.id.secondhandPrice);
	// holder.btn_edit = (TextView) convertView.findViewById(R.id.tv_edit);
	// holder.btn_get_down = (TextView) convertView
	// .findViewById(R.id.tv_get_out);
	// convertView.setTag(holder);
	//
	// // } else {
	// //
	// // holder = (ViewHolder) convertView.getTag();
	// // }
	//
	// // 这里是把数据送到绑定好的地方
	//
	// String aString = Constants.TEST_SERVER_STRING
	// + (String) thisData.get(position).getUrl1();
	// ImageLoader.getInstance().displayImage(
	// Constants.TEST_SERVER_STRING
	// + (String) thisData.get(position).getUrl1(),
	// holder.img_ObjImg);
	// holder.txtVw_ObjName.setText((String) thisData.get(position)
	// .getTitle());
	// if (thisData.get(position).getCampus().equals("0"))
	// holder.txtVw_ObjNameContent.setText("中关村校区");
	// else {
	// holder.txtVw_ObjNameContent.setText("良乡校区");
	// }
	//
	// holder.txtVw_ObjDescribe.setText((String) thisData.get(position)
	// .getTime().substring(0, 10));
	// holder.tv_price.setText(thisData.get(position).getPrice()
	// .toString()
	// + "元");
	// holder.btn_edit.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// Intent intent = new Intent();
	// intent.putExtra("data", thisData.get(position));
	// intent.setClass(context, SecondHandPublishActivity.class);
	// context.startActivity(intent);
	// }
	// });
	// holder.btn_get_down.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// context);
	// builder.setTitle("商品下架之后该条数据将被删除，您确定将商品下架吗？");
	// builder.setPositiveButton("确定",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// // 通过相机获取
	// try {
	// getDown(thisData.get(position)
	// .getStringId(), context,
	// position);
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// builder.setNegativeButton("取消",
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// dialog.cancel();
	// }
	// });
	//
	// builder.create().show();
	//
	// }
	// });
	// // convertView.setOnClickListener(new OnClickListener() {
	// //
	// // @Override
	// // public void onClick(View arg0) {
	// // // Bundle dataBundle = new Bundle();
	// // // dataBundle.putInt("position", position);
	// // // Intent intent = new Intent(context, DetailActivity.class);
	// // // intent.putExtras(dataBundle);
	// // // context.startActivity(intent);
	// //
	// // }
	// // });
	//
	// return convertView;
	// }
	//
	// private void getDown(String id, final Context context,
	// final int position) throws UnsupportedEncodingException {
	//
	// // 刷新
	// JSONObject json = new JSONObject();
	// try {
	// json.put("id", id);
	// } catch (JSONException e) {
	//
	// }
	// mConnection
	// .doPost(Constants.TEST_SERVER_STRING + "DeleteGoodInfo",
	// Constants.BITKNOWTEST_CLOUDSERVER_STRING
	// + "DeleteGoodInfo", json,
	// new SecondHandMarketNetworkHandler() {
	//
	// @Override
	// public void onSuccess(String str) {
	// try {
	// JSONObject json = new JSONObject(str);
	// System.out.println(str);
	// boolean isSuccess = json
	// .getBoolean("success");
	// if (isSuccess) {
	// Toast.makeText(context, "商品已下架", 0)
	// .show();
	// mData.remove(position);
	// listViewAdapter
	// .notifyDataSetChanged();
	//
	// } else {
	// Toast.makeText(context, "下架失败", 0)
	// .show();
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } finally {
	//
	// }
	// }
	//
	// @Override
	// public void onFailure() {
	//
	// Toast.makeText(context, "网络异常",
	// Toast.LENGTH_SHORT).show();
	//
	// }
	// });
	// }
	//
	// private class ViewHolder {
	// ImageView img_ObjImg;
	// TextView txtVw_ObjName;
	// TextView txtVw_ObjNameContent;
	// TextView txtVw_ObjDescribe;
	// TextView tv_price;
	// TextView btn_edit;
	// TextView btn_get_down;
	// }
	// }
}
