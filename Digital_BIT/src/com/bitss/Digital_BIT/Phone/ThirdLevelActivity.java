package com.bitss.Digital_BIT.Phone;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;

public class ThirdLevelActivity extends CustomBaseActivity {
	private Button mTitleButton;
	private Button mGoBackButton;
	private ListView mListView;
	private List<Map<String, Object>> mDataList;
	private String nameOfSuperior;
	private String pidThirdLevel;
	private ImageView searchBtn;
	private RelativeLayout mRelativeLayout;
	private EditText mEditText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitphone);

		Intent cIntent = this.getIntent();
		// 获取Title名称
		nameOfSuperior = cIntent.getStringExtra("nameSecondLevel");
		// 将上级类的sid作为本级类的pid
		pidThirdLevel = cIntent.getStringExtra("sidSecondLevel");

		init();
	}

	public void init() {

		// mTitleButton = (Button) findViewById(R.id.phone_title_btn1);
		// 设置Title名称
		// mTitleButton.setText(nameOfSuperior);
		mTvNaviTitle.setText(nameOfSuperior);
		mListView = (ListView) findViewById(R.id.phone_list1);
		// 以pidSecondLevel为pid在数据库中查询，返回所有子节点
		mDataList = new PhoneInfoManager(this).getDataListByPid(pidThirdLevel);
		MyThirdAdapter thirdAdapter = new MyThirdAdapter(this);
		mListView.setAdapter(thirdAdapter);
		mListView.setSelection(1);
	}

	/**
	 * 自定义ListView
	 */
	class MyThirdAdapter extends BaseAdapter {
		private String theMessage;
		private String theTitle;
		private LayoutInflater inflater;
		private AlertDialog.Builder dialog;

		public MyThirdAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
			dialog = new AlertDialog.Builder(context);
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

		public View getView(int position, View convertView, ViewGroup parent) {
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
				searchBtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent toSearchIntent = new Intent(
								ThirdLevelActivity.this, SearchActivity.class);
						// 向查询界面传入用户输入
						toSearchIntent.putExtra("input", mEditText.getText()
								.toString());
						// 向查询界面传入当前界面的pid
						toSearchIntent.putExtra("pidForSearch", pidThirdLevel);
						// 向查询界面传入查询动作的来源标识
						toSearchIntent.putExtra("signOfSource", "3");
						startActivity(toSearchIntent);

						// overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
						// overridePendingTransition(R.anim.animzoomin,
						// R.anim.animzoomout);
					}
				});
			}
			// 在后面的位置放入信息内容，每个单位的名称、电话等
			else {
				convertView = inflater.inflate(R.layout.phone_listitem_c, null);
				RelativeLayout layout = (RelativeLayout) convertView
						.findViewById(R.id.phone_listitem_layout_c);
				final TextView nameTextView = (TextView) convertView
						.findViewById(R.id.phone_listitem_nametxt_c);
				final TextView phoneNumTextView = (TextView) convertView
						.findViewById(R.id.phone_listitem_numbertxt_c);

				nameTextView.setText((String) mDataList.get(position - 1).get(
						PhoneColumns.KEY_DESCRIPTION));
				phoneNumTextView.setText((String) mDataList.get(position - 1)
						.get(PhoneColumns.KEY_PHONENUM));

				// 点击一个单位弹出操作对话框
				layout.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						// 获取单位名称
						theTitle = (String) nameTextView.getText();
						// 获取电话号码
						theMessage = (String) phoneNumTextView.getText();
						// 去掉号码中的“—”以便直接呼叫
						final String numToCall = theMessage.replace("-0", "0")
								.replace("-1", "1").replace("-2", "2")
								.replace("-3", "3").replace("-4", "4")
								.replace("-5", "5").replace("-6", "6")
								.replace("-7", "7").replace("-8", "8")
								.replace("-9", "9");
						dialog.setTitle(theTitle);
						dialog.setMessage(theMessage);

						// 呼叫按钮
						dialog.setPositiveButton("编辑呼叫", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Uri uri = Uri.parse("tel:" + numToCall);
								Intent intent1 = new Intent(Intent.ACTION_DIAL,
										uri);
								startActivity(intent1);
								// overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
							}
						});
						// 存储按钮
						dialog.setNeutralButton("存入通讯录", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
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
								getContentResolver().insert(
										ContactsContract.Data.CONTENT_URI,
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
								getContentResolver().insert(
										ContactsContract.Data.CONTENT_URI,
										values);

								// 保存后Toast提示保存成功，防止重复保存
								Toast toast = Toast.makeText(getBaseContext(),
										"成功加入至联系人", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.BOTTOM, 0, 0);
								toast.show();
							}
						});
						dialog.setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				});
			}

			return convertView;
		}
	}
}
