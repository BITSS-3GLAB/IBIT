package com.bitss.Digital_BIT.Personal;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandMarketHttpConnection;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandMarketNetworkHandler;
import com.bitss.Digital_BIT.Util.Constants;

public class RegisterActivity2 extends CustomBaseActivity {

	private Button btn_send_code;
	private EditText et_userName, et_code, et_password;
	String phoneString;
	private TextView tv_more, tv_skip;

	private Dialog dialog;
	private SecondHandMarketHttpConnection mConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_register);
		mTvNaviTitle.setText(getResources().getString(R.string.register));
		Init();
		listener();
	}

	public void Init() {
		btn_send_code = (Button) findViewById(R.id.btn_commit);
		et_userName = (EditText) findViewById(R.id.et_username);
		et_code = (EditText) findViewById(R.id.et_code);

		et_password = (EditText) findViewById(R.id.et_password);

		mConnection = new SecondHandMarketHttpConnection(this);
		mConnection.setTimeOut(11000);

		phoneString = getIntent().getStringExtra("phone");

	}

	private void listener() {
		btn_send_code.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (et_code.getText().toString().length() == 0
						|| et_password.getText().toString().length() == 0) {
					Toast.makeText(RegisterActivity2.this, "验证码和密码为必填项", 0)
							.show();
					return;

				}
				try {
					register();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void myselfDialog() {
		dialog = new Dialog(RegisterActivity2.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.register_success_dialog);

		Window diaWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = diaWindow.getAttributes();
		diaWindow.setGravity(Gravity.CENTER);
		lp.width = LayoutParams.FILL_PARENT;
		diaWindow.setAttributes(lp);
		tv_more = (TextView) dialog.findViewById(R.id.tv_more);
		tv_skip = (TextView) dialog.findViewById(R.id.tv_skip);

		tv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("type", "new");
				intent.setClass(RegisterActivity2.this,
						PersonalMoreInfoActivity.class);
				startActivity(intent);
				finish();
			}
		});

		tv_skip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 将评论的信息发送到数据库进行存储

				// 每次将评论发表之后都要刷新一次得到最新的评论信息
				dialog.dismiss();
				finish();

			}
		});

		dialog.show();

	}

	private void register() throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {

			json.put("password", et_password.getText().toString());
			json.put("phone", phoneString);
			json.put("checkCode", et_code.getText().toString());
		} catch (JSONException e) {

		}
		mConnection.doPost(Constants.TEST_SERVER_STRING + "Register",
				Constants.BITKNOWTEST_CLOUDSERVER_STRING + "Register", json,
				new SecondHandMarketNetworkHandler() {

					@Override
					public void onSuccess(String str) {
						try {
							JSONObject object = new JSONObject(str.toString());
							if (object.getBoolean("success")) {
								Toast.makeText(RegisterActivity2.this, "注册成功", 0)
										.show();
								SharedPreferences userInfo = getSharedPreferences(
										"User", 0);
								userInfo.edit()
										.putString(Constants.USER_PHONE,
												phoneString).commit();
								myselfDialog();
							} else {
								Toast.makeText(RegisterActivity2.this,
										object.getString("message"), 0).show();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						} finally {

						}
					}

					@Override
					public void onFailure() {

					}
				});

	}

}
