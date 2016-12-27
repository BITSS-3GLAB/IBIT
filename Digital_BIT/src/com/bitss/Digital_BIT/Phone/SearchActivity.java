package com.bitss.Digital_BIT.Phone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.ParamUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends CustomBaseActivity {
	private ListView mListView;
	private Button mGoBackButton;
	private Button mTitleButton;
	private List<Map<String, Object>> itemList;
	private List<Map<String, Object>> tagList;
	private String pidForSearch;
	private String input;
	private String signOfSource;
	private int intSignOfSource;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitphone);

		init();

		// 获取用户输入、pid和动作来源，动作来源signOfSource的作用是为查询方式的选择提供依据
		input = new String();
		Intent getInfoIntent = this.getIntent();
		input = getInfoIntent.getStringExtra("input");
		if (input == null || input.trim().equals("")) {
			Toast toast = Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 100);
			toast.show();
			// 避免进入查询界面
			SearchActivity.this.finish();
			// overridePendingTransition(R.anim.push_right_in,
			// R.anim.push_right_out);
			// overridePendingTransition(R.anim.animzoomin, R.anim.animzoomout);
		} else {
			pidForSearch = getInfoIntent.getStringExtra("pidForSearch");
			signOfSource = getInfoIntent.getStringExtra("signOfSource");
			intSignOfSource = Integer.parseInt(signOfSource);

			// 为了区别和标识ListView中不同类型的单位，itemList中只包含所有单位，tagList中包含所有单位及其对应父类单位
			itemList = new ArrayList<Map<String, Object>>();
			tagList = new ArrayList<Map<String, Object>>();
			// 不同来源的查询请求使用不同的查询方法
			switch (intSignOfSource) {
			// 在全范围内仅依据用户输入查询
			case 1:
				itemList = new PhoneInfoManager(this).getDataListByInput(input);
				break;
			// 例如在“学院”范围内查询
			case 2:
				List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
				// tempList包含当前pid对应的所有sid，即获取父节点的所有子节点，如“学院”中的所有学院
				tempList = new PhoneInfoManager(this).getSidByPid(pidForSearch);
				// 获取每一个子节点的子节点列表，将所有列表合为一个列表itemList，包含每个学院中的每个单位的信息
				for (int i = 0; i < tempList.size(); i++) {
					String tempString = (String) tempList.get(i).get(
							PhoneColumns.KEY_SID);
					List<Map<String, Object>> list = new PhoneInfoManager(this)
							.getDataListByInput(tempString, input);
					if (!list.get(0).containsKey("isWrong")) {
						itemList.addAll(list);
					}
				}
				break;
			case 3:
				// 获取当前范围的用户过滤后的子节点列表
				itemList = new PhoneInfoManager(this).getDataListByInput(
						pidForSearch, input);
				break;
			default:
				break;
			}
			// map对象用来检测用户输入的查询目标是否在当前范围中或是否合法
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isWrong", "wrong");
			// 不合法或无结果则提示
			if (itemList.contains(map) || itemList.isEmpty()) {
				Toast toast = Toast.makeText(this, "在该机构内无此单位: " + input,
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 100);
				toast.show();
				// 避免进入查询界面
				SearchActivity.this.finish();
				// overridePendingTransition(R.anim.push_right_in,
				// R.anim.push_right_out);
				// overridePendingTransition(R.anim.animzoomin,
				// R.anim.animzoomout);
			}
			// 构造tagList，并使每一个父节点名称总在其所有子节点之前
			else {
				String sidString = "value";
				String pidString = null;
				for (int i = 0; i < itemList.size(); i++) {
					// 获取叶节点的pid
					pidString = (String) itemList.get(i).get(
							PhoneColumns.KEY_PID);
					// 添加itemList的每一个元素到tagList中
					if (i > 0) {
						tagList.add(itemList.get(i - 1));
					}
					// 获取同类叶节点的父节点名称
					if (pidString.equals(sidString)) {
						continue;
					}
					sidString = pidString;
					String parentDescription = (String) new PhoneInfoManager(
							this).getDataListBySid(sidString).get(0)
							.get(PhoneColumns.KEY_DESCRIPTION);
					// 将名称添加到tagList中
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("parentDescription", parentDescription);
					tagList.add(tempMap);
				}
				// 补上最后一个叶节点
				tagList.add(itemList.get(itemList.size() - 1));
			}

			mListView = (ListView) findViewById(R.id.phone_list1);
			MyAdapter myAdapter = new MyAdapter(this);
			mListView.setAdapter(myAdapter);
		}
	}


	public void init() {
		mTvNaviTitle.setText("查找单位");
		// mTitleButton = (Button) findViewById(R.id.phone_title_btn1);
		// mTitleButton.setText("查找单位");
		//
		//
		// mGoBackButton = (Button) findViewById(R.id.phone_goback_btn1);
		// mGoBackButton.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// finish();
		// }
		// });
	}

	class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private AlertDialog.Builder dialog;

		public MyAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
			dialog = new AlertDialog.Builder(context);
		}

		public int getCount() {
			return tagList.size();
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// 判断是否为标题（父节点名称）
			if (itemList.contains(tagList.get(position))) {
				// 不为标题，获取当前节点的sid
				String tempSidString = (String) tagList.get(position).get(
						PhoneColumns.KEY_SID);
				// 判断当前节点是否为叶节点
				if (new PhoneInfoManager(getBaseContext())
						.isIdHolding(tempSidString)) {
					// 不为叶节点
					convertView = inflater.inflate(R.layout.phone_listitem_b,
							null);

					RelativeLayout layout = (RelativeLayout) convertView
							.findViewById(R.id.phone_listitem_layout_b);
					final TextView nameTextView = (TextView) convertView
							.findViewById(R.id.phone_listitem_nametxt_b);
					final TextView addressTextView = (TextView) convertView
							.findViewById(R.id.phone_listitem_addresstxt_b);
					final TextView emailTextView = (TextView) convertView
							.findViewById(R.id.phone_listitem_emailtxt_b);

					nameTextView.setText((String) tagList.get(position).get(
							PhoneColumns.KEY_DESCRIPTION));
					addressTextView.setText((String) tagList.get(position).get(
							PhoneColumns.KEY_LOCATION));
					emailTextView.setText((String) tagList.get(position).get(
							PhoneColumns.KEY_EMAIL));

					// 点击可进入其子节点列表的新界面
					layout.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							String sidSecondLevel = (String) tagList.get(
									position).get(PhoneColumns.KEY_SID);
							Intent taIntent = new Intent(SearchActivity.this,
									ThirdLevelActivity.class);
							taIntent.putExtra("nameSecondLevel",
									nameTextView.getText());
							// 将作为pid以获得子节点
							taIntent.putExtra("sidSecondLevel", sidSecondLevel);
							startActivity(taIntent);

							// overridePendingTransition(
							// R.anim.push_left_in,
							// R.anim.push_left_out);
							// overridePendingTransition(R.anim.animzoomin,
							// R.anim.animzoomout);
						}
					});
				} else {
					convertView = inflater.inflate(R.layout.phone_listitem_c,
							null);
					RelativeLayout layout = (RelativeLayout) convertView
							.findViewById(R.id.phone_listitem_layout_c);
					final TextView tNameTextView = (TextView) convertView
							.findViewById(R.id.phone_listitem_nametxt_c);
					final TextView tPhoneNumTextView = (TextView) convertView
							.findViewById(R.id.phone_listitem_numbertxt_c);

					tNameTextView.setText((String) tagList.get(position).get(
							PhoneColumns.KEY_DESCRIPTION));
					tPhoneNumTextView.setText((String) tagList.get(position)
							.get(PhoneColumns.KEY_PHONENUM));

					// 点击一个单位弹出操作对话框
					layout.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final String theTitle = (String) tNameTextView
									.getText();
							String theMessage = (String) tPhoneNumTextView
									.getText();
							// 去掉号码中的“—”以便直接呼叫
							final String numToCall = theMessage
									.replace("-0", "0").replace("-1", "1")
									.replace("-2", "2").replace("-3", "3")
									.replace("-4", "4").replace("-5", "5")
									.replace("-6", "6").replace("-7", "7")
									.replace("-8", "8").replace("-9", "9");
							dialog.setTitle(theTitle);
							dialog.setMessage(theMessage);

							// 呼叫按钮
							dialog.setPositiveButton("编辑呼叫",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub
											Uri uri = Uri.parse("tel:"
													+ numToCall);
											Intent intent1 = new Intent(
													Intent.ACTION_DIAL, uri);
											startActivity(intent1);
										}
									});

							// 存储按钮
							dialog.setNeutralButton("存入通讯录",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub
											ContentResolver cr = getContentResolver();
											ContentValues values = new ContentValues();
											Uri rawContactUri = cr
													.insert(ContactsContract.RawContacts.CONTENT_URI,
															values);
											long rawContactId = ContentUris
													.parseId(rawContactUri);
											values.clear();
											// 用户名称信息
											values.put(
													ContactsContract.Data.RAW_CONTACT_ID,
													rawContactId);
											values.put(
													ContactsContract.Data.MIMETYPE,
													ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
											values.put(
													ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
													theTitle);
											getContentResolver()
													.insert(ContactsContract.Data.CONTENT_URI,
															values);
											values.clear();
											// 电话数据
											values.put(
													ContactsContract.Data.RAW_CONTACT_ID,
													rawContactId);
											values.put(
													ContactsContract.Data.MIMETYPE,
													ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
											values.put(
													ContactsContract.CommonDataKinds.Phone.TYPE,
													ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
											values.put(
													ContactsContract.CommonDataKinds.Phone.NUMBER,
													numToCall);
											getContentResolver()
													.insert(ContactsContract.Data.CONTENT_URI,
															values);

											// 保存后Toast提示保存成功
											Toast toast = Toast.makeText(
													getBaseContext(),
													"成功加入至联系人",
													Toast.LENGTH_SHORT);
											toast.setGravity(Gravity.BOTTOM, 0,
													0);
											toast.show();
										}
									});
							dialog.setNegativeButton("取消",
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated
											// method stub
											dialog.dismiss();
										}
									});
							dialog.show();
						}
					});
				}
			}
			// 是标题则以对应xml显示其内容
			else {
				convertView = inflater.inflate(R.layout.phone_listitem_tag,
						null);
				TextView tagTextView = (TextView) convertView
						.findViewById(R.id.phone_itemtitle);
				tagTextView.setText((String) tagList.get(position).get(
						"parentDescription"));
			}
			return convertView;
		}
	}
}
