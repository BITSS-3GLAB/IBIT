package com.bitss.Digital_BIT.LostFound;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.ParamUtil;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;

public class LostFoundEditActivity extends Activity implements OnClickListener {

	private Context context;
	private static final String TITLE = "编辑";
	private static final String SD_PATH = "/sdcard/IBIT/LF_PHOTO/";
	private static final int REQUEST_CAMERA = 1024;
	private static final int REQUEST_PHOTO_ALBUM = 1025;

	private ImageView backImg;
	private ImageView writeImg;

	private View includeDes;
	private View includePlace;
	private View includePhone;

	private EditText edtDes;
	private EditText edtTime;
	private EditText edtPlace;
	private EditText edtPhone;

	private ImageView photoImg;
	private ImageView deleteImg;
	private Button takePhotoButton;

	private HttpPost post;
	private HttpPost cloudpost;
	private String imgPath = null; // 图片的路径
	private boolean isAlbumPath = false; // 来自相册，上传成功后不用删除
	private ProgressDialog dialog;

	private static final int REQUEST_EDIT = 10022; // 跳转到编辑页面
	private static final int POST_SUCCESS = 10024;
	private static final int POST_FAILURE = 10025;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (!isAlbumPath && imgPath != null) {
				// 删除发布成功的照片
				File file = new File(imgPath);
				if (file.exists()) {
					file.delete();
				}
				imgPath = null;
			}

			switch (msg.what) {
			case POST_SUCCESS:
				// 上传成功后清空数据
				edtDes.setText("");
				edtPlace.setText("");
				edtPhone.setText("");

				photoImg.setImageResource(R.drawable.edt_show_photo);
				deleteImg.setVisibility(View.INVISIBLE);

				dialog.dismiss();
				msgToast("发布成功！");
				setResult(REQUEST_EDIT);
				finish();
				break;
			case POST_FAILURE:
				// 上传成功后清空数据
				edtDes.setText("");
				edtPlace.setText("");
				edtPhone.setText("");

				photoImg.setImageResource(R.drawable.edt_show_photo);
				deleteImg.setVisibility(View.INVISIBLE);

				dialog.dismiss();
				msgToast("抱歉，发布失败，请稍候再试...");
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edt_lost_found);

		context = this;

		// 导航栏初始化
		((TextView) findViewById(R.id.tv_navi_title)).setText(TITLE);
		backImg = (ImageView) findViewById(R.id.iv_navi_back);
		writeImg = (ImageView) findViewById(R.id.iv_navi_save);
		backImg.setOnClickListener(this);
		writeImg.setOnClickListener(this);

		// 描述、时间、地点、电话相关初始化
		includeDes = findViewById(R.id.include_des);
		includePlace = findViewById(R.id.include_place);
		includePhone = findViewById(R.id.include_phone);
		((TextView) includeDes.findViewById(R.id.tv_title)).setText("描述");
		((TextView) includePlace.findViewById(R.id.tv_title)).setText("地点");
		((TextView) includePhone.findViewById(R.id.tv_title)).setText("电话");
		edtDes = (EditText) includeDes.findViewById(R.id.edt_content);
		edtPlace = (EditText) includePlace.findViewById(R.id.edt_content);
		edtPhone = (EditText) includePhone.findViewById(R.id.edt_content);

		// 拍照控件初始化
		photoImg = (ImageView) findViewById(R.id.photo_show);
		deleteImg = (ImageView) findViewById(R.id.photo_delete);
		takePhotoButton = (Button) findViewById(R.id.btn_photo);
		deleteImg.setOnClickListener(this);
		takePhotoButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.iv_navi_back:
			finish();
			break;
		case R.id.iv_navi_save:
			sendToServer();
			break;
		case R.id.photo_delete:
			photoImg.setImageResource(R.drawable.edt_show_photo);
			deleteImg.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_photo:
			// 获取照片
			takePhoto();
			break;

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

	// 发送给服务器
	public void sendToServer() {
		String desc = edtDes.getText().toString();
		String loc = edtPlace.getText().toString();
		String cont = edtPhone.getText().toString();

		if (desc.length() == 0 || loc.length() == 0 || cont.length() == 0
				|| imgPath == null) {
			Toast.makeText(this, "信息尚未填写完整", 0).show();
			return;
		}

		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(this)) {
			Utils.showToast(this, Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

		// 本地服务器
		String url = ParamUtil.SERVER_URL + "GetLostFoundInfo";
		// 云端服务器url
		String cloudurl = Constants.BITKNOWTEST_CLOUDSERVER_STRING
				+ "servlet/GetLostFoundInfo";
		Log.e("net_data", "url = " + url);
		if (post == null) {
			post = new HttpPost(url);
		}
		if (cloudpost == null) {
			cloudpost = new HttpPost(cloudurl);
		}

		dialog = ProgressDialog.show(context, "请稍等...", "数据发布中...", true);
		new Thread(new postLostFound(desc, loc, cont)).start();
	}

	private class postLostFound implements Runnable {

		String desc;
		String loc;
		String cont;

		private postLostFound(String desc, String loc, String cont) {
			this.desc = desc;
			this.loc = loc;
			this.cont = cont;
		}

		@Override
		public void run() {

			Message message = new Message();
			MultipartEntity entity = new MultipartEntity();
			try {
				// 构建参数
				if (imgPath != null) {
					File file = new File(imgPath);
					entity.addPart("file", new FileBody(file));
				}

				entity.addPart("desc",
						new StringBody(desc, Charset.forName(HTTP.UTF_8)));
				entity.addPart("loc",
						new StringBody(loc, Charset.forName(HTTP.UTF_8)));
				entity.addPart("cont",
						new StringBody(cont, Charset.forName(HTTP.UTF_8)));
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
				} else {
					HttpResponse response = new DefaultHttpClient(httpParams)
							.execute(cloudpost);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						message.what = POST_SUCCESS;
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

	public void msgToast(String msg) {
		Toast.makeText(context, msg, 0).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data != null && resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CAMERA) {
				// 调用相机
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Toast.makeText(this, "sd不可用", 0).show();
					return;
				}

				File file = new File(SD_PATH);
				if (!file.exists()) {
					file.mkdirs();// 创建文件夹
				}
				String fileName = SD_PATH + System.currentTimeMillis()
						+ "_1.jpg";

				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				FileOutputStream b = null;
				try {
					b = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

					isAlbumPath = false;
					imgPath = fileName;

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						b.flush();
						b.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				photoImg.setImageBitmap(bitmap);// 将图片显示在ImageView里
				deleteImg.setVisibility(View.VISIBLE);

			} else if (requestCode == REQUEST_PHOTO_ALBUM) {
				// 从相册来
				ContentResolver resolver = getContentResolver();
				Uri uri = data.getData();
				try {
					byte[] pic = readStream(resolver.openInputStream(Uri
							.parse(uri.toString())));
					Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0,
							pic.length);

					// 获取相册的路径
					String[] projection = { MediaStore.Images.Media.DATA };
					Cursor cursor = managedQuery(uri, projection, null, null,
							null);
					cursor.moveToFirst();
					imgPath = cursor.getString(cursor
							.getColumnIndexOrThrow(projection[0]));
					isAlbumPath = true;

					photoImg.setImageBitmap(bitmap);
					deleteImg.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

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

}
