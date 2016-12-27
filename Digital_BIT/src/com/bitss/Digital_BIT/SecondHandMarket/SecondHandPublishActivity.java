package com.bitss.Digital_BIT.SecondHandMarket;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.FileUtil;
import com.bitss.Digital_BIT.Tools.HttpPictureAsker;
import com.bitss.Digital_BIT.Tools.ImageTools;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.FileUtils;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.View.HorizontalListView;
import com.bitss.Digital_BIT.View.HorizontalListView.OnListItemClickListener;

public class SecondHandPublishActivity extends CustomBaseActivity {

	View convertView;
	// private LinearLayout ll_image_group;
	// private RelativeLayout rl_add_phone;
	private Button btn_publish;
	private EditText et_title, et_content, et_price;
	private TextView et_square, et_classify;
	private HorizontalListView gv_image;
	private TextView tv_more_info;

	private static final int REQUEST_CAMERA = 1024;
	private static final int REQUEST_PHOTO_ALBUM = 1025;
	private static final String SD_PATH = "/sdcard/IBIT/LF_PHOTO/";
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private HttpPost post;
	private HttpPost cloudpost;
	private ImageAdapter imageAdapter;

	private String imgPath = null; // 图片的路径

	private static final int REQUEST_EDIT = 10022;
	private static final int POST_SUCCESS = 10024;
	private static final int POST_FAILURE = 10025;

	private SecondHandMarketData data;
	private int campus_flag = 0;
	private int classify_position = 0;
	private String[] classify = { "all", "bicycle", "book", "clothes",
			"digital", "basketball", "donation", "music", "coupon", "other" };
	private String[] classify_type = { "全部", "代步工具", "书籍资料", "服装鞋包", "数码专区",
			"体育用品", "爱心赠送", "音乐乐器", "票券", "其他" };
	SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sencond_hand_publish);
		mTvNaviTitle.setText(getResources().getString(
				R.string.second_hand_publish));
		settings = getSharedPreferences("User", 0);
		try {
			data = (SecondHandMarketData) getIntent().getSerializableExtra(
					"data");
		} catch (Exception e) {
			// TODO: handle exception
		}
		Init();

		listener();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		String path = FileUtils.getImageCachePath().toString();
		// .png是将要存储的图片的名称

		// SimpleDateFormat dateFormat = new SimpleDateFormat(
		// "yyyy-MM-dd HH:mm:ss");// 可以方便地修改日期格式
		// String hehe = dateFormat.format(now);

		for (int i = 0; i < 3; i++) {
			File file = new File(path, i + "logo.jpg");
			if (file.exists())
				file.delete();
		}
		File file1 = new File(path, "page.jpg");

		if (file1.exists())
			file1.delete();

	}

	/**
	 * 获取控件，进行初始化
	 */

	public void Init() {

		btn_publish = (Button) findViewById(R.id.btn_commit);
		gv_image = (HorizontalListView) findViewById(R.id.h_scroll);
		tv_more_info = (TextView) findViewById(R.id.tv_more_info);

		imageAdapter = new ImageAdapter(this, bitmaps);
		gv_image.setAdapter(imageAdapter);

		et_content = (EditText) findViewById(R.id.et_content);
		et_price = (EditText) findViewById(R.id.et_price);
		et_square = (TextView) findViewById(R.id.et_square);
		et_classify = (TextView) findViewById(R.id.et_classify);
		et_title = (EditText) findViewById(R.id.et_title);
		if (data != null) {
			et_title.setText(data.getTitle());
			et_content.setText(data.getDesc());
			et_price.setText(data.getPrice() + "");

			if (data.getCampus().equals("0")) {
				et_square.setText("中关村校区");
			} else {
				et_square.setText("良乡校区");
				campus_flag = 1;
			}

			String current_type = data.getCategory();
			for (int i = 0; i < classify.length; i++) {
				if (current_type.equals(classify[i])) {
					classify_position = i;
					String aString = classify_type[i];
					et_classify.setText(aString);
					break;

				}
			}
			Toast.makeText(SecondHandPublishActivity.this, "图片正在下载中，请稍后...", 0)
					.show();
			tv_more_info.setText(data.getLocation());
			new getImageData().execute();

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

	/**
	 * 绑定所有的点击事件
	 */

	public void listener() {

		et_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent().setClass(
						SecondHandPublishActivity.this,
						SecondHandEditTextActivity.class);
				intent.putExtra("type", "publish");
				intent.putExtra("pre_data", et_content.getText().toString());
				startActivityForResult(intent, 110);

			}
		});
		/**
		 * 拍照
		 */
		gv_image.setOnListItemClickListener(new OnListItemClickListener() {

			@Override
			public void onClick(View v, int position) {
				// TODO Auto-generated method stub
				if (position == 0) {
					int account = bitmaps.size();

					if (bitmaps.size() < 3)
						takePhoto();
					else {
						Toast.makeText(SecondHandPublishActivity.this,
								"最多能上传三张图片", 0).show();
					}
				}

			}
		});
		/**
		 * 发布信息
		 */
		btn_publish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String url = "";
				String cloudurl = "";
				if (data == null) {
					cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "GetNewGoodInfo";
					url = Constants.BITKNOWTEST_SERVER_STRING
							+ "GetNewGoodInfo";
				} else {
					cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "GetUpdateGoodInfo";
					url = Constants.BITKNOWTEST_SERVER_STRING
							+ "GetUpdateGoodInfo";
				}
				Log.e("net_data", "url = " + url);
				if (post == null) {
					post = new HttpPost(url);
				}
				if (cloudpost == null) {
					cloudpost = new HttpPost(cloudurl);
				}
				if (et_title.getText().toString().trim().length() < 6) {
					Toast.makeText(SecondHandPublishActivity.this, "标题至少6字", 0)
							.show();
					return;
				}
				if (et_content.getText().toString().trim().length() < 10) {
					Toast.makeText(SecondHandPublishActivity.this, "标题至少6字", 0)
							.show();
					return;
				}
				if (et_price.getText().toString().trim().length() == 0) {
					Toast.makeText(SecondHandPublishActivity.this, "请填写价格", 0)
							.show();
					return;
				}
				if (et_square.getText().toString().trim().length() == 0) {
					Toast.makeText(SecondHandPublishActivity.this, "请填选择区域", 0)
							.show();
					return;
				}
				// if (et_title.getText().toString().length() == 0
				// | et_content.getText().toString().length() == 0
				// | et_price.getText().toString().length() == 0
				// | et_square.getText().toString().length() == 0) {
				// Toast.makeText(SecondHandPublishActivity.this,
				// "这四条信息都为必填项", 0).show();
				// return;
				// }
				Toast.makeText(SecondHandPublishActivity.this, "正在发布，请稍候...", 0)
						.show();
				new Thread(new postLostFound()).start();

				// Intent intent = new Intent();
				// intent.setClass(SecondHandPublishActivity.this,
				// LoginActivity.class);
				// startActivity(intent);
			}
		});

		/**
		 * 填写详细的地址信息
		 */
		tv_more_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent().setClass(
						SecondHandPublishActivity.this,
						SecondHandEditTextActivity.class);
				intent.putExtra("type", "publish");
				intent.putExtra("pre_data", tv_more_info.getText().toString());
				startActivityForResult(intent, 120);
			}
		});

		et_classify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent().setClass(
						SecondHandPublishActivity.this,
						ChooseClassifyActivity.class);
				startActivityForResult(intent, 130);
			}
		});

		et_square.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						SecondHandPublishActivity.this);
				builder.setTitle("请选择校区");
				builder.setPositiveButton("中关村校区",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 通过相机获取
								et_square.setText("中关村校区");
							}
						});
				builder.setNegativeButton("良乡校区",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								et_square.setText("良乡校区");
							}
						});

				builder.create().show();

			}
		});
	}

	/**
	 * 拍照或是从相册中获得图片之后的处理 ，获得Bitmap值存储到数列中
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		String sdPath = FileUtil.getImageCachePath();
		String corpImgName = ImageTools.getCorpImgName();
		if (data != null && resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CAMERA) {

				// 删除最近的一张照片，不留在系统相册中。
				// // ImageTools.deleteLatestPhoto(getActivity());
				// if (!ImageTools.findPhotoFromSDCard(sdPath, corpImgName)) {
				// // 拍照的图片没有保存到本地目录下。
				// // TODO：这里要提示一个文言。
				// Toast.makeText(SecondHandPublishActivity.this, "拍照失败!",
				// Toast.LENGTH_SHORT).show();
				//
				// // ImageTools.ClearNewTempImages();
				//
				// return;
				//
				// }
				//
				// // ImageTools.deleteLatestPhoto(this);
				// // 这是更加安全的策略，压缩保存在裁剪。保证内存不溢出。
				// bitmap = ImageTools.decodePhotoFromSDCard(sdPath,
				// corpImgName,
				// 600, 600, true);
				//
				// if (bitmap == null) {
				// Toast.makeText(SecondHandPublishActivity.this, "拍照失败!",
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				//
				// // ImageTools
				// // .savePhotoToSDCard(tmp_image, sdPath, corpImgName, 70);
				// //
				// // // 提示进行裁剪。图像太大，可能无法进行裁剪？
				// // startPhotoZoom(
				// // Uri.fromFile(new File(sdPath, corpImgName + ".jpg")),
				// // null);
				// // // 调用相机
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Toast.makeText(this, "sd不可用", 0).show();
					return;
				}
				Bundle bundle = data.getExtras();
				bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

			} else if (requestCode == REQUEST_PHOTO_ALBUM) {
				// 从相册来
				// if (!ImageTools.findPhotoFromSDCard(sdPath, "f_c_image")) {
				// // 拍照的图片没有保存到本地目录下。
				// // TODO：这里要提示一个文言。
				// Toast.makeText(SecondHandPublishActivity.this, "取图失败!",
				// Toast.LENGTH_SHORT).show();
				// return;
				//
				// }
				//
				// bitmap = ImageTools.decodePhotoFromSDCard(sdPath,
				// "f_c_image",
				// 500, 500, false);
				ContentResolver resolver = getContentResolver();
				Uri uri = data.getData();
				try {
					byte[] pic = readStream(resolver.openInputStream(Uri
							.parse(uri.toString())));
					bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);

					// 获取相册的路径
					String[] projection = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, projection, null, null,
							null);
					cursor.moveToFirst();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 110) {
				et_content.setText(data.getStringExtra("content"));

			} else if (requestCode == 120) {
				tv_more_info.setText(data.getStringExtra("content"));
			} else if (requestCode == 130) {
				classify_position = data.getIntExtra("position", 0);
				et_classify.setText(data.getStringExtra("content"));
			}

			if (bitmap != null) {
				// ImageView iv_one = new ImageView(this);
				// LinearLayout.LayoutParams params = new LayoutParams(
				// Utils.dp2pixel(this, 90), Utils.dp2pixel(this, 90));
				// params.setMargins(10, 10, 10, 10);
				// iv_one.setLayoutParams(params);
				// iv_one.setScaleType(ScaleType.FIT_XY);
				// iv_one.setImageBitmap(bitmap);
				// ll_image_group.addView(iv_one);

				bitmaps.add(bitmap);
				imageAdapter = new ImageAdapter(SecondHandPublishActivity.this,
						bitmaps);
				gv_image.setAdapter(imageAdapter);
			}

		}

	}

	/**
	 * 获取照片
	 * */
	public void takePhoto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		builder.setPositiveButton("拍照", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 通过相机获取
				Toast.makeText(SecondHandPublishActivity.this,
						"为了照片的正常显示，请横屏拍照", 0).show();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CAMERA);
				// ImageTools.CatchOlderImageList();
				// Intent intent = new
				// Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
				// // f_c_image.jpg 表示照相图片。
				// Uri imageUri = Uri.fromFile(new File(FileUtil
				// .getImageCachePath(), ImageTools.getCorpImgName()
				// + ".jpg"));
				// //
				// intent.putExtra("crop", "true"); // 出现裁剪窗口。
				// intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				// intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				//
				// startActivityForResult(intent, REQUEST_CAMERA);
			}
		});
		builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Uri imageUri = Uri.fromFile(new File(FileUtil
				// .getImageCachePath(), "f_c_image.jpg"));
				//
				// Intent openAlbumIntent = new
				// Intent(Intent.ACTION_GET_CONTENT);
				// openAlbumIntent.setType("image/*");
				// openAlbumIntent.putExtra("crop", "true");
				// openAlbumIntent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
				// openAlbumIntent.putExtra("aspectY", 1);// x:y=1:2
				// openAlbumIntent.putExtra("outputX", 500);
				// openAlbumIntent.putExtra("outputY", 500);
				//
				// // lixi 直接从图库选择图片。
				// openAlbumIntent.putExtra("return-data", false);
				// openAlbumIntent.putExtra("output", imageUri);
				// openAlbumIntent.putExtra("outputFormat", "JPEG");
				// openAlbumIntent.putExtra("noFaceDetection", true);
				//
				// startActivityForResult(openAlbumIntent, REQUEST_PHOTO_ALBUM);
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, REQUEST_PHOTO_ALBUM);
			}
		});

		builder.create().show();
	}

	/**
	 * 从相册中读取照片的信息
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	/**
	 * 数据异步上传
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			try {
				JSONObject object = new JSONObject(msg.obj.toString());
				if (object.getBoolean("success")) {
					Toast.makeText(SecondHandPublishActivity.this, "发布成功", 0)
							.show();
					finish();
				} else {
					Toast.makeText(SecondHandPublishActivity.this,
							"发布失败，请重新发布", 0).show();

				}

			} catch (JSONException e) {
				Toast.makeText(SecondHandPublishActivity.this, "发布失败，请重新发布", 0)
						.show();
			}

		}
	};

	/**
	 * 上传数据
	 * 
	 * @author wufangxue
	 * 
	 */
	private class postLostFound implements Runnable {

		String title;
		int campus;
		String price;
		String type;
		String description;
		String location;
		String phone;

		// String title , String desc, String price, String loc, String square ,
		// String campus

		private postLostFound() {
			phone = settings.getString(Constants.USER_PHONE, "");
			this.title = et_title.getText().toString().trim();
			this.description = et_content.getText().toString().trim();
			this.price = et_price.getText().toString().trim();
			this.location = tv_more_info.getText().toString().trim();
			this.type = classify[classify_position];
			this.campus = campus_flag;

		}

		@Override
		public void run() {

			Message message = new Message();
			MultipartEntity entity = new MultipartEntity();
			try {
				// 构建参数
				for (int i = 0; i < bitmaps.size(); i++) {

					File file = FileUtils.bitmap2file(bitmaps.get(i), 80, i);
					entity.addPart("file" + i + ".jpg", new FileBody(file));
				}

				// if (imgPath != null) {
				// File file = new File(imgPath);
				// entity.addPart("file", new FileBody(file));
				// }

				if (data != null)
					entity.addPart("id", new StringBody(data.getStringId(),
							Charset.forName(HTTP.UTF_8)));
				entity.addPart("title",
						new StringBody(title, Charset.forName(HTTP.UTF_8)));
				entity.addPart("description", new StringBody(description,
						Charset.forName(HTTP.UTF_8)));
				entity.addPart("price",
						new StringBody(price, Charset.forName(HTTP.UTF_8)));
				entity.addPart("location",
						new StringBody(location, Charset.forName(HTTP.UTF_8)));
				// 中关村校区为0，良乡校区为1
				entity.addPart("campus", new StringBody(campus_flag + "",
						Charset.forName(HTTP.UTF_8)));

				entity.addPart("phone",
						new StringBody(phone, Charset.forName(HTTP.UTF_8)));
				entity.addPart("category",
						new StringBody(type, Charset.forName(HTTP.UTF_8)));

				post.setEntity(entity);
				cloudpost.setEntity(entity);

				// 请求服务器
				BasicHttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
				HttpConnectionParams.setSoTimeout(httpParams, 30000);

				HttpResponse cloudresponse = new DefaultHttpClient(httpParams)
						.execute(cloudpost);
				if (cloudresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					message.what = POST_SUCCESS;
					String strResult = EntityUtils.toString(cloudresponse
							.getEntity());
					message.obj = strResult;

				} else {
					HttpResponse response = new DefaultHttpClient(httpParams)
							.execute(post);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						message.what = POST_SUCCESS;
						String strResult = EntityUtils.toString(response
								.getEntity());
						message.obj = strResult;
					} else {
						message.what = POST_FAILURE;
					}
				}

			} catch (Exception e) {
				message.what = POST_FAILURE;
			} finally {
				handler.sendMessage(message);
			}
		}
	}

	// 拍照获得的照片的gridview的配置
	class ImageAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<Bitmap> mData;

		public ImageAdapter(Context context, ArrayList<Bitmap> mData) {
			this.mData = mData;
			this.context = context;
		}

		@Override
		public int getCount() {
			if (mData == null)
				return 1;
			return mData.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {

			LayoutInflater flater = LayoutInflater.from(context);
			ImageView iv_image;
			if (position == 0) {
				convertView = flater.inflate(R.layout.item_add_phone, null);
			} else {
				convertView = flater.inflate(R.layout.item_publish_image, null);
				iv_image = (ImageView) convertView.findViewById(R.id.iv_phone);
				iv_image.setImageBitmap(mData.get(position - 1));
				ImageView iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				iv_delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								SecondHandPublishActivity.this);
						builder.setTitle("您确定要删除该张图片吗？");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 通过相机获取
										bitmaps.remove(position - 1);
										imageAdapter = new ImageAdapter(
												SecondHandPublishActivity.this,
												bitmaps);
										gv_image.setAdapter(imageAdapter);

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

					}
				});
			}

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					Utils.dp2pixel(SecondHandPublishActivity.this, 80),
					Utils.dp2pixel(SecondHandPublishActivity.this, 80));
			params.setMargins(
					Utils.dp2pixel(SecondHandPublishActivity.this, 5), 0,
					Utils.dp2pixel(SecondHandPublishActivity.this, 5), 0);

			convertView.setLayoutParams(params);

			return convertView;
		}

	}

	class getImageData extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			if (!data.getUrl1().equals("")) {
				bitmaps.add(new HttpPictureAsker()
						.getSecondHandUrl(Constants.TEST_SERVER_STRING
								+ data.getUrl1()));
			}
			if (!data.getUrl2().equals("")) {
				bitmaps.add(new HttpPictureAsker()
						.getSecondHandUrl(Constants.TEST_SERVER_STRING
								+ data.getUrl2()));
			}
			if (!data.getUrl3().equals("")) {
				bitmaps.add(new HttpPictureAsker()
						.getSecondHandUrl(Constants.TEST_SERVER_STRING
								+ data.getUrl3()));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			imageAdapter = new ImageAdapter(SecondHandPublishActivity.this,
					bitmaps);
			gv_image.setAdapter(imageAdapter);
		}

	}
}
