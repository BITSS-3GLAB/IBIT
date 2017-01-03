package com.bitss.Digital_BIT.More;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;

public class AboutUsActivity extends CustomBaseActivity {

	private TextView appVersion;
	private static TextView aboutUsMail;
	private static TextView aboutUsPhone;
	private ContactListener mContactListener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutuslayout);

		mTvNaviTitle.setText("关于我们");

		appVersion = (TextView) findViewById(R.id.about_us_version);
		aboutUsMail = (TextView) findViewById(R.id.about_us_mail);
		aboutUsPhone = (TextView) findViewById(R.id.about_us_phone);

		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String version = "北京理工大学校园通" + packInfo.versionName;
			appVersion.setText(version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		mContactListener = new ContactListener(this);
		aboutUsMail.setOnClickListener(mContactListener);
		aboutUsPhone.setOnClickListener(mContactListener);
	}

	private class ContactListener implements View.OnClickListener {

		private AlertDialog.Builder dialog;
		private String mailString, phoneString;
		private ContactListener mContactListener = null;
		private Uri uri = null;

		public ContactListener(Context context) {
			dialog = new AlertDialog.Builder(context);
		}

		@Override
		public void onClick(View v) {

			// 获取电话号码
			mailString = (String) aboutUsMail.getText();

			dialog.setTitle("联系我们");
			dialog.setMessage("如有问题或者需求，可以给我们打电话或者发送邮件。");

			// 呼叫按钮
			dialog.setPositiveButton("呼叫我们", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					uri = Uri.parse("tel:" + "01068914044");
					Intent intent1 = new Intent(Intent.ACTION_DIAL, uri);
					startActivity(intent1);
				}
			});
			// 存储按钮
			dialog.setNeutralButton("发送邮件", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String[] reciver = new String[] { "wcwbit@gmail.com" };
					Intent myIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					myIntent.setType("plain/text");
					myIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
							reciver);
					startActivity(Intent.createChooser(myIntent,
							"给北理校园通项目组发送邮件"));
				}
			});
			dialog.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}

	}

}