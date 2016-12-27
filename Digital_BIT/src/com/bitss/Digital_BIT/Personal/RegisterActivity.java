package com.bitss.Digital_BIT.Personal;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Guide.GuideActivity;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.StrUtils;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.TextHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.DefaultFormBodyBuilder;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.bitss.Digital_BIT.okhttp.request.DefaultJsonBodyBuilder;
import com.bitss.Digital_BIT.okhttp.request.Parameter;

public class RegisterActivity extends CustomBaseActivity {

	private static final String TAG = RegisterActivity.class
			.getSimpleName();

	private EditText et_phone;
	private EditText mEtCode;
	private EditText mEtPwd;
	private EditText mEtConfirm;
	private EditText mEtEmail;
	private EditText mEtUsername;
	private Button mBtnRegister;
	private Button mBtnCode;
	
	private String phone;
	private String pwd;
	private String pwdConfirm;
	private String code;
	private String email;
	private String username;
	
	private Dialog dialog;

	// private SecondHandMarketHttpConnection mConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);
		mTvNaviTitle.setText(getResources().getString(R.string.register));
		Init();
	}

	public void Init() {

		et_phone = (EditText) findViewById(R.id.userphone);
		mEtCode = (EditText) findViewById(R.id.et_code);
		mEtPwd = (EditText) findViewById(R.id.et_password);
		mEtConfirm = (EditText) findViewById(R.id.et_confirm);
		mEtUsername = (EditText) findViewById(R.id.et_username);
		mEtEmail = (EditText) findViewById(R.id.et_email);
		mBtnCode = (Button) findViewById(R.id.btn_code);
		mBtnRegister = (Button) findViewById(R.id.btn_commit);

		// mConnection = new SecondHandMarketHttpConnection(this);
		// mConnection.setTimeOut(11000);
		mBtnCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (StrUtils.isMobileNO(et_phone.getText().toString())) {
					// Intent intent = new Intent();
					// intent.setClass(
					// RegisterSendCodeActivity.this,
					// RegisterActivity.class);
					// intent.putExtra("phone", et_phone.getText().toString());
					// startActivity(intent);
					phone = et_phone.getText().toString().trim();
					getCode(phone);
				} else {
					Utils.showToast(RegisterActivity.this, "请输入正确的手机号",
							Toast.LENGTH_LONG);
				}
			}
		});

		mBtnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phone = et_phone.getText().toString().trim();
				code = mEtCode.getText().toString().trim();
				pwd = mEtPwd.getText().toString().trim();
				pwdConfirm = mEtConfirm.getText().toString().trim();
				email = mEtEmail.getText().toString().trim();
				username = mEtUsername.getText().toString().trim();
				if (phone.length() == 0) {
					Utils.showToast(mApp, "号码不能为空");
					return;
				}
				if (code.length() == 0) {
					Utils.showToast(mApp, "验证码不能为空");
					return;
				}
				if (pwd.length() == 0 || pwdConfirm.length() == 0) {
					Utils.showToast(mApp, "密码不能为空");
					return;
				}
				if (code.length() == 0) {
					Utils.showToast(mApp, "验证码不能为空");
					return;
				}
				if (username.length() == 0) {
					Utils.showToast(mApp, "用户名不能为空");
					return;
				}
				if (email.length() == 0) {
					Utils.showToast(mApp, "邮箱不能为空");
					return;
				}
				if (!pwd.equals(pwdConfirm)) {
					Utils.showToast(mApp, "两次密码不同");
					return;
				}
				mBtnRegister.setEnabled(false);
				verificationCode();
			}
		});
	}

	private void getCode(String phone) {
		Parameter parameter = new Parameter("phone", phone);
		HttpRequest request = new HttpRequest.Builder()
				.url("/user/front/userCheckCode")
				.bodyBuilder(new DefaultFormBodyBuilder()).add(parameter)
				.build();
		Logger.i(TAG, request.toString());
		HttpClient.getInstance(mApp).put(request.getUrl(), request.getBody(),
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						Logger.i(TAG, "status:" + status + ";data = " + data);
						switch (status) {
						case 202:
							break;
						case 409:
							Utils.showToast(mApp, "该手机号已经被注册！");
							break;
						default:
							break;
						}
					}

					@Override
					public void onFailure(Exception e) {
						e.printStackTrace();
					}
				});
	}

	private void verificationCode() {
		Parameter parameter = new Parameter("checkCode", code);
		HttpRequest request = new HttpRequest.Builder()
				.url("/user/front/userCheckCode").add(parameter).build();
		HttpClient.getInstance(mApp).get(request.getUrl(),
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						switch (status) {
						case 202:
							verificationEmail();
							break;
						case 406:
							Utils.showToast(mApp, "验证码错误", Toast.LENGTH_LONG);
							break;
						default:
							Utils.showToast(mApp, "注册失败", Toast.LENGTH_LONG);
							mBtnRegister.setEnabled(true);
							break;
						}
					}

					@Override
					public void onFailure(Exception e) {
						Utils.showToast(mApp, "注册失败");
						e.printStackTrace();
						mBtnRegister.setEnabled(true);
					}
				});
	}
	
	private void verificationEmail() {
		Parameter parameter = new Parameter("email", email);
		HttpRequest request = new HttpRequest.Builder().url("/user/front/userExistByEmail").add(parameter).build();
		HttpClient.getInstance(mApp).get(request.getUrl(), new TextHttpResponseHandler() {
			
			@Override
			public void onSuccess(int status, String data) {
				switch (status) {
				case 200:
					register();
					break;
				case 302:
					Utils.showToast(mApp, "该邮箱已被注册", Toast.LENGTH_LONG);
					break;
				default:
					Utils.showToast(mApp, "注册失败", Toast.LENGTH_LONG);
					mBtnRegister.setEnabled(true);
					break;
				}
			}
			
			@Override
			public void onFailure(Exception e) {
				e.printStackTrace();
				Utils.showToast(mApp, "注册失败");
				e.printStackTrace();
				mBtnRegister.setEnabled(true);
			}
		});
	}

	private void register() {
		List<Parameter> parameters = new ArrayList<Parameter>(3);
		parameters.add(new Parameter("password", pwd));
		parameters.add(new Parameter("name", username));
		parameters.add(new Parameter("email", email));
		HttpRequest request = new HttpRequest.Builder().url("/user/front/user")
				.bodyBuilder(new DefaultJsonBodyBuilder())
				.addList(parameters).build();
		HttpClient.getInstance(mApp).put(request.getUrl(), request.getBody(),
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						switch (status) {
						case 201:
							Utils.showToast(mApp, "注册成功");
							mApp.getPreferences().edit()
									.putString(Constants.USER_PHONE, phone)
									.putString(Constants.NICK_NAME, username)
									.putString(Constants.KEY_EMAIL, email)
									.commit();
							myselfDialog();
							break;
						case 406:
							Utils.showToast(mApp, "验证码错误");
							break;
						default:
							break;
						}
						mBtnRegister.setEnabled(true);
					}

					@Override
					public void onFailure(Exception e) {
						Utils.showToast(mApp, "注册失败");
						e.printStackTrace();
						mBtnRegister.setEnabled(true);
					}
				});
	}
	
	public void myselfDialog() {
		dialog = new Dialog(RegisterActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.register_success_dialog);

		Window diaWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = diaWindow.getAttributes();
		diaWindow.setGravity(Gravity.CENTER);
		lp.width = LayoutParams.MATCH_PARENT;
		diaWindow.setAttributes(lp);
		TextView tv_more = (TextView) dialog.findViewById(R.id.tv_more);
		TextView tv_skip = (TextView) dialog.findViewById(R.id.tv_skip);

		tv_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("type", "new");
				intent.setClass(RegisterActivity.this,
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
				Intent intent = new Intent(RegisterActivity.this, GuideActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
			}
		});

		dialog.show();

	}

	// private void GetCode() throws UnsupportedEncodingException {
	// JSONObject json = new JSONObject();
	// try {
	// json.put("phone", et_phone.getText().toString());
	// } catch (JSONException e) {
	//
	// }
	// mConnection.doPost(Constants.SERVER_URL + "SendCheckCode",
	// Constants.BITKNOWTEST_CLOUDSERVER_STRING + "SendCheckCode",
	// json, new SecondHandMarketNetworkHandler() {
	//
	// @Override
	// public void onSuccess(String str) {
	// try {
	// JSONObject object = new JSONObject(str.toString());
	// if ((Boolean) object.get("success")) {
	// Toast.makeText(RegisterSendCodeActivity.this,
	// "验证码已发送，请查收", 0).show();
	// Intent intent = new Intent();
	// intent.setClass(RegisterSendCodeActivity.this,
	// RegisterActivity.class);
	// intent.putExtra("phone", et_phone.getText()
	// .toString());
	// startActivity(intent);
	// finish();
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
	//
	// }

}
