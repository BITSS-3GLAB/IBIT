package com.bitss.Digital_BIT.Personal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Guide.GuideActivity;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandEditTextActivity;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandMarketHttpConnection;
import com.bitss.Digital_BIT.Tools.HttpPictureAsker;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.FileUtils;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.ReLogin;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.handler.JsonHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.handler.TextHttpResponseHandler;
import com.bitss.Digital_BIT.okhttp.request.DefaultMultiBodyBuilder;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.bitss.Digital_BIT.okhttp.request.Parameter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalMoreInfoActivity extends CustomBaseActivity {

	private static final String TAG = PersonalMoreInfoActivity.class
			.getSimpleName();

	private EditText et_nickName, et_sex, et_campus;
	private TextView et_sign, tv_sign, tv_userName, tv_logout, et_telephone,
			et_email;
	private EditText et_pwd;
	private String phone;
	private String username;
	private String email;
	private RoundedImageView iv_photo;

	private int campus = 0, gender = 0;
	private String flag;

	private static final int REQUEST_CAMERA = 1024;
	private static final int REQUEST_PHOTO_ALBUM = 1025;

	private SecondHandMarketHttpConnection mConnection;
	SharedPreferences settings;
	private Bitmap bitmap = null;
	private HttpPost post;
	private HttpPost cloudpost;

	// private static final int POST_SUCCESS = 10024;
	// private static final int POST_FAILURE = 10025;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		mTvNaviTitle.setText("我的资料");
		settings = mApp.getPreferences();
		Init();
		listener();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (settings.getString(Constants.KEY_EMAIL, "").equals("")) {
			exit();
		}
	}

	private void Init() {
		flag = getIntent().getStringExtra("type");
		tv_logout = (TextView) findViewById(R.id.tv_logout);
		phone = settings.getString(Constants.USER_PHONE, "");
		username = settings.getString(Constants.NICK_NAME, "");
		email = settings.getString(Constants.KEY_EMAIL, "");

		mConnection = new SecondHandMarketHttpConnection(this);
		mConnection.setTimeOut(11000);
		if (flag.equals("edit")) {
			getData();
		} else {
			tv_logout.setVisibility(View.GONE);
		}

		String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING
				+ "GetUpdateUserInfo";
		String url = Constants.BITKNOWTEST_SERVER_STRING + "GetUpdateUserInfo";
		Log.e("net_data", "url = " + url);
		if (post == null) {
			post = new HttpPost(url);
		}
		if (cloudpost == null) {
			cloudpost = new HttpPost(cloudurl);
		}

		et_email = (TextView) findViewById(R.id.et_mail);
		et_nickName = (EditText) findViewById(R.id.et_nickName);
		et_sex = (EditText) findViewById(R.id.et_gender);
		et_sign = (TextView) findViewById(R.id.et_sign);
		tv_sign = (TextView) findViewById(R.id.tv_mysign);
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		et_telephone = (TextView) findViewById(R.id.et_phone);
		iv_photo = (RoundedImageView) findViewById(R.id.iv_icon);
		et_pwd = (EditText) findViewById(R.id.et_pwd);

		et_telephone.setText(phone);
		et_email.setText(email);
		et_nickName.setText(username);
		tv_userName.setText(username);

		mTvRight.setVisibility(View.VISIBLE);
		mTvRight.setText("提交");

	}

	/**
	 * 获取照片
	 * */
	public void takePhoto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				PersonalMoreInfoActivity.this);
		builder.setTitle("请选择");
		builder.setPositiveButton("拍照", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 通过相机获取
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CAMERA);
			}
		});
		builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
			}
		});

		builder.create().show();
	}

	public void startPhotoZoom(Uri uri) {
		/*
		 * 图片裁剪
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null && resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CAMERA) {
				// 调用相机
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Toast.makeText(this, "sd不可用", Toast.LENGTH_SHORT).show();
					return;
				}
				Bundle bundle = data.getExtras();
				bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			} else if (requestCode == REQUEST_PHOTO_ALBUM) {
				// 从相册来
				ContentResolver resolver = getContentResolver();
				Uri uri = data.getData();
				try {
					byte[] pic = new Utils().readStream(resolver
							.openInputStream(Uri.parse(uri.toString())));
					bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);

					// 获取相册的路径
					String[] projection = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, projection, null, null,
							null);
					cursor.moveToFirst();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				et_sign.setText(data.getStringExtra("content"));
				tv_sign.setText(data.getStringExtra("content"));
			}
			if (bitmap != null) {
				iv_photo.setImageBitmap(bitmap);
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			// 获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private void listener() {

		iv_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				takePhoto();
			}
		});
		tv_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PersonalMoreInfoActivity.this);
				builder.setTitle("是否注销");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								logout();
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				builder.create().show();
				// settings.edit().putString(Constants.USER_PHONE, "").commit();
				// finish();
				// Intent intent = new Intent().setClass(
				// PersonalMoreInfoActivity.this, LoginActivity.class);
				// startActivity(intent);

			}
		});
		et_sign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent().setClass(
						PersonalMoreInfoActivity.this,
						SecondHandEditTextActivity.class);
				intent.putExtra("type", "info");
				if (et_sign.getText().toString().equals("这个家伙很懒，什么都没留下"))
					intent.putExtra("pre_data", "");
				else {
					intent.putExtra("pre_data", et_sign.getText().toString());
				}

				startActivityForResult(intent, 80);

			}
		});
		mTvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(PersonalMoreInfoActivity.this, "您的数据正在提交...",
						Toast.LENGTH_LONG).show();
				// new Thread(new postLostFound()).start();
				modify();
			}
		});
		// et_campus.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(
		// PersonalMoreInfoActivity.this);
		// builder.setTitle("请选择校区");
		// builder.setPositiveButton("中关村校区",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // 通过相机获取
		// et_campus.setText("中关村校区");
		// campus = 0;
		// }
		// });
		// builder.setNegativeButton("良乡校区",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// et_campus.setText("良乡校区");
		// campus = 1;
		// }
		// });
		//
		// builder.create().show();
		//
		// }
		// });

		et_sex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						PersonalMoreInfoActivity.this);
				builder.setTitle("请选择您的性别");
				builder.setPositiveButton("男",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								et_sex.setText("男");
								gender = 0;
							}
						});
				builder.setNegativeButton("女",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								et_sex.setText("女");
								gender = 1;
							}
						});
//				builder.setNeutralButton("保密",
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								et_sex.setText("保密");
//								gender = 2;
//							}
//						});

				builder.create().show();

			}
		});
	}

	// /**
	// * 数据异步上传
	// */
	// private Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// if (msg.what == POST_SUCCESS) {
	// try {
	// JSONObject object = new JSONObject(msg.obj.toString());
	// if (object.getBoolean("success")) {
	// if ((!et_nickName.getText().toString().equals("无"))
	// && (!et_nickName.getText().toString()
	// .equals("无"))) {
	// settings.edit()
	// .putString(Constants.NICK_NAME,
	// et_nickName.getText().toString())
	// .commit();
	// }
	// Toast.makeText(PersonalMoreInfoActivity.this,
	// "个人详细信息提交成功", Toast.LENGTH_LONG).show();
	// finish();
	// } else {
	// Toast.makeText(PersonalMoreInfoActivity.this,
	// "个人详细信息提交失败", Toast.LENGTH_LONG).show();
	//
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// } else {
	// Toast.makeText(PersonalMoreInfoActivity.this, "提交失败",
	// Toast.LENGTH_LONG).show();
	// }
	//
	// }
	// };

	private void getData() {
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
								et_telephone.setText(phone);
								et_nickName.setText(name);
								tv_userName.setText(name);
								et_email.setText(email);

								mApp.getPreferences().edit()
										.putString(Constants.USER_PHONE, phone)
										.putString(Constants.NICK_NAME, name)
										.putString(Constants.KEY_EMAIL, email)
										.commit();

								switch (response.getInt("gender")) {
								case 0:
									gender = 0;
									et_sex.setText("男");
									break;
								case 1:
									gender = 1;
									et_sex.setText("女");
									break;
								default:
									break;
								}
								et_sign.setText(response.getString("autograph"));
								tv_sign.setText(response.getString("autograph"));
								String contempphotoUrl = response
										.getString("iconUrl");
								String photoUrl = contempphotoUrl.replace(
										"\\", "/");
								// ImageLoader.getInstance()
								// .displayImage(
								// Constants.TEST_SERVER_STRING
								// + object.getString(
								// "photoUrl")
								// .substring(1),
								// iv_photo);
								ImageLoader.getInstance().displayImage(
										Constants.PHTOT_CLOUDSERVER_STRING
												+ photoUrl, iv_photo);
								new getImageData()
										.execute(Constants.PHTOT_CLOUDSERVER_STRING
												+ photoUrl);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							break;
						case 401:
							new ReLogin(PersonalMoreInfoActivity.this).showDialog();
							break;
						default:
							Utils.showToast(mApp, "获取用户信息失败");
							break;
						}
					}

					@Override
					public void onSuccess(int status, String jsonString) {
						Logger.i(TAG, jsonString);
						if (status == 401) {
							new ReLogin(PersonalMoreInfoActivity.this).showDialog();
						}
					}

					@Override
					public void onFailure(Exception e) {
						e.printStackTrace();
						Utils.showToast(mApp, "获取数据失败", Toast.LENGTH_LONG);
					}
				});
	}

	private void modify() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		username = et_nickName.getText().toString().trim();
		if (!username.equals("")) {
			parameters.add(new Parameter("name", username));
		}
		parameters.add(new Parameter("autograph", et_sign.getText().toString()
				.trim()));
		parameters.add(new Parameter("gender", gender));
		String pwd = et_pwd.getText().toString().trim();
		if (!pwd.equals("")) {
			parameters.add(new Parameter("password", pwd));
		}
		File icon = null;
		if (bitmap == null) {
			iv_photo.setDrawingCacheEnabled(true);
			bitmap = iv_photo.getDrawingCache();
			iv_photo.setDrawingCacheEnabled(false);
		}
		icon = FileUtils.bitmap2file(bitmap, 80, 0);
		parameters.add(new Parameter("icon", icon));
		HttpRequest request = new HttpRequest.Builder().url("/user/front/user")
				.bodyBuilder(new DefaultMultiBodyBuilder()).addList(parameters)
				.build();
		HttpClient.getInstance(mApp).post(request.getUrl(), request.getBody(),
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						switch (status) {
						case 201:
							mApp.getPreferences().edit()
									.putString(Constants.NICK_NAME, username)
									.putString(Constants.KEY_EMAIL, email)
									.commit();
							exit();
							break;
						case 401:
							new ReLogin(PersonalMoreInfoActivity.this).showDialog(); 
							break;
						default:
							Utils.showToast(mApp, "修改用户信息失败");
							break;
						}
					}

					@Override
					public void onFailure(Exception e) {
						Utils.showToast(mApp, "修改用户信息失败");
						e.printStackTrace();
					}
				});
	}

	private void logout() {
		HttpRequest request = new HttpRequest.Builder()
				.url("/user/front/login").build();
		HttpClient.getInstance(mApp).delete(request.getUrl(), null,
				new TextHttpResponseHandler() {

					@Override
					public void onSuccess(int status, String data) {
						switch (status) {
						case 200:
							settings.edit().putString(Constants.USER_PHONE, "")
									.putString(Constants.NICK_NAME, "")
									.putString(Constants.KEY_EMAIL, "")
									.commit();
							finish();
							Intent intent = new Intent().setClass(
									PersonalMoreInfoActivity.this,
									LoginActivity.class);
							startActivity(intent);
							break;
						default:
							Utils.showToast(mApp, "注销失败，请再次尝试");
							break;
						}
					}

					@Override
					public void onFailure(Exception e) {
						Utils.showToast(mApp, "注销失败，请再次尝试");
					}
				});
	}

	// /**
	// * 上传数据
	// *
	// * @author wufangxue
	// *
	// */
	// private class postLostFound implements Runnable {
	//
	// // String title , String desc, String price, String loc, String square ,
	// // String campus
	//
	// private postLostFound() {
	//
	// }
	//
	// @Override
	// public void run() {
	//
	// Message message = new Message();
	// MultipartEntity entity = new MultipartEntity();
	// try {
	// // 构建参数
	// File file = null;
	// if (bitmap != null) {
	// file = FileUtils.bitmap2file(bitmap, 80, 0);
	// entity.addPart("file.jpg", new FileBody(file));
	// }
	//
	// entity.addPart("username", new StringBody(et_nickName.getText()
	// .toString(), Charset.forName(HTTP.UTF_8)));
	// entity.addPart("phone",
	// new StringBody(phone, Charset.forName(HTTP.UTF_8)));
	// entity.addPart(
	// "gender",
	// new StringBody(gender + "", Charset.forName(HTTP.UTF_8)));
	// // 中关村校区为0，良乡校区为1
	// entity.addPart(
	// "campus",
	// new StringBody(campus + "", Charset.forName(HTTP.UTF_8)));
	//
	// entity.addPart("email", new StringBody(et_email.getText()
	// .toString(), Charset.forName(HTTP.UTF_8)));
	// entity.addPart("sign", new StringBody(et_sign.getText()
	// .toString(), Charset.forName(HTTP.UTF_8)));
	//
	// post.setEntity(entity);
	// cloudpost.setEntity(entity);
	//
	// // 请求服务器
	// BasicHttpParams httpParams = new BasicHttpParams();
	// HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
	// HttpConnectionParams.setSoTimeout(httpParams, 30000);
	//
	// HttpResponse cloudresponse = new DefaultHttpClient(httpParams)
	// .execute(cloudpost);
	// if (cloudresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// message.what = POST_SUCCESS;
	// String strResult = EntityUtils.toString(cloudresponse
	// .getEntity());
	// message.obj = strResult;
	//
	// } else {
	// HttpResponse response = new DefaultHttpClient(httpParams)
	// .execute(cloudpost);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// message.what = POST_SUCCESS;
	// String strResult = EntityUtils.toString(response
	// .getEntity());
	// message.obj = strResult;
	//
	// } else {
	// message.what = POST_FAILURE;
	// }
	// }
	//
	// } catch (Exception e) {
	// message.what = POST_FAILURE;
	// } finally {
	// handler.sendMessage(message);
	// }
	// }
	// }

	// private void getData() throws UnsupportedEncodingException {
	// JSONObject json = new JSONObject();
	// try {
	// json.put("phone", phone);
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
	//
	// et_telephone.setText(phone);
	// et_nickName.setText(object
	// .getString("username"));
	// tv_userName.setText(object
	// .getString("username"));
	// switch (object.getInt("gender")) {
	// case 0:
	// gender = 0;
	// et_sex.setText("男");
	// break;
	// case 1:
	// gender = 1;
	// et_sex.setText("女");
	// break;
	// case 2:
	// gender = 2;
	// et_sex.setText("保密");
	// break;
	// default:
	// break;
	// }
	// if (object.getInt("campus") == 0) {
	// campus = 0;
	// et_campus.setText("中关村校区");
	// } else {
	// campus = 1;
	// et_campus.setText("良乡校区");
	// }
	// et_sign.setText(object.getString("sign"));
	// tv_sign.setText(object.getString("sign"));
	// et_email.setText(object.getString("email"));
	// String contempphotoUrl = object
	// .getString("photoUrl");
	// String photoUrl = contempphotoUrl.replace(
	// "\\/", "/");
	// // ImageLoader.getInstance()
	// // .displayImage(
	// // Constants.TEST_SERVER_STRING
	// // + object.getString(
	// // "photoUrl")
	// // .substring(1),
	// // iv_photo);
	// ImageLoader.getInstance().displayImage(
	// Constants.PHTOT_CLOUDSERVER_STRING
	// + photoUrl, iv_photo);
	// new getImageData()
	// .execute(Constants.PHTOT_CLOUDSERVER_STRING
	// + object.getString("photoUrl")
	// .substring(1));
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

	class getImageData extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {
			bitmap = new HttpPictureAsker().getSecondHandUrl(arg0[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}

	}

	public void onBackPressed() {
		exit();
	}

	private void exit() {
		if (flag.equals("edit")) {
			finish();
			return;
		}
		Intent intent = new Intent().setClass(PersonalMoreInfoActivity.this,
				GuideActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}