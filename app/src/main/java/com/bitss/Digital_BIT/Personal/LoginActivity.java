package com.bitss.Digital_BIT.Personal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandMarketHttpConnection;
import com.bitss.Digital_BIT.Tools.HttpAsker;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.TextHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.DefaultFormBodyBuilder;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.bitss.Digital_BIT.okhttp.request.Parameter;

public class LoginActivity extends CustomBaseActivity {

	private EditText et_email, et_password;
	private TextView tv_login, tv_register;
	private SecondHandMarketHttpConnection mConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mTvNaviTitle.setText(getResources().getString(R.string.login));
		Init();
		listener();
	}

	private void Init() {
		et_password = (EditText) findViewById(R.id.et_password);
		et_email = (EditText) findViewById(R.id.et_email);
		tv_login = (TextView) findViewById(R.id.tv_login);
		tv_register = (TextView) findViewById(R.id.tv_register);

		mConnection = new SecondHandMarketHttpConnection(this);
		mConnection.setTimeOut(11000);
	}

	private void listener() {
		tv_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String pwd = et_password.getText().toString().trim();
				String email = et_email.getText().toString().trim();
				if (pwd.equals("") || email.equals("")) {
					Toast.makeText(LoginActivity.this, "请输入手机号和密码",
							Toast.LENGTH_LONG).show();
					return;

				}
				login(email, pwd);
				// new RefreshTask().execute();

			}
		});
		tv_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);

			}
		});
	}

	private void login(final String email, String pwd) {
		List<Parameter> parameters = new ArrayList<Parameter>(2);
		parameters.add(new Parameter("email", email));
		parameters.add(new Parameter("password", pwd));

		HttpRequest request = new HttpRequest.Builder()
				.url("/user/front/login")
				.bodyBuilder(new DefaultFormBodyBuilder()).addList(parameters)
				.build();

		HttpClient.getInstance(mApp).post(request.getUrl(), request.getBody(),
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						switch (status) {
						case 202:
							mApp.getPreferences().edit()
									.putString(Constants.KEY_EMAIL, email)
									.commit();
							Utils.showToast(mApp, "登录成功");
							finish();
							break;
						case 401:
							Utils.showToast(mApp, "密码错误");
							break;
						default:
							Utils.showToast(mApp, "登录失败");
							break;
						}
					}

					@Override
					public void onFailure(Exception e) {
						e.printStackTrace();
						Utils.showToast(mApp, "登录失败");
					}
				});
	}

	// public class RefreshTask extends AsyncTask<Void, Void, StringBuffer> {
	//
	// @Override
	// protected StringBuffer doInBackground(Void... arg0) {
	//
	// StringBuffer buffer = new StringBuffer();
	// // HttpAsker.Asker(buffer, Constants.BITKNOWTEST_SERVER_STRING +
	// // "Login",
	// // json);
	// int tmpInt = HttpAsker.Asker(buffer,
	// Constants.BITKNOWTEST_CLOUDSERVER_STRING + "Login", json);
	// if (tmpInt != 0) {
	// buffer = new StringBuffer();
	// // 请求本地服务器
	// int tempINT = HttpAsker.Asker(buffer,
	// Constants.BITKNOWTEST_SERVER_STRING + "Login", json);
	// return buffer;
	// }
	// return buffer;
	// }
	//
	// @Override
	// protected void onPostExecute(StringBuffer result) {
	// super.onPostExecute(result);
	// JSONObject json;
	// try {
	// json = new JSONObject(result.toString());
	// boolean isSuccess = json.getBoolean("success");
	// if (isSuccess) {
	// Toast.makeText(LoginActivity.this, "登录成功", 0).show();
	// // 将手机号存储起来表示已经登录了
	// SharedPreferences userInfo = getSharedPreferences("User", 0);
	// userInfo.edit()
	// .putString(Constants.USER_PHONE,
	// et_telephone.getText().toString()).commit();
	//
	// finish();
	// } else {
	// Toast.makeText(LoginActivity.this,
	// json.getString("message"), 0).show();
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }

}
