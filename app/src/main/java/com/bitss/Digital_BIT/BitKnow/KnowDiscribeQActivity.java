package com.bitss.Digital_BIT.BitKnow;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.View.HorizontalListView;
import com.bitss.Digital_BIT.View.HorizontalListView.OnListItemClickListener;

public class KnowDiscribeQActivity extends CustomBaseActivity {

	private HorizontalListView gv_image;
	private EditText discrib_content;
	private ImageAdapter imageAdapter;
	private ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private ArrayList<String> picUri = new ArrayList<String>();
	private static final int REQUEST_CAMERA = 1024;
	private static final int REQUEST_PHOTO_ALBUM = 1025;
	private static final int REQUEST_ADDTOPIC = 1;
	public static final String INTENT_DISCRIBEQ = "discribe";
	public static final String INTENT_BITMAP = "pic";

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_know_describe_question);

		intiActionBar();
		intiOther();
		listens();

	}

	// 设置actionBar
	private void intiActionBar() {
		mTvNaviTitle.setText(R.string.bitknow_discribe_question);

		mTvRight.setText(R.string.next_step);

		mTvRight.setVisibility(View.VISIBLE);
	}

	// 设置图片、文本框
	private void intiOther() {
		gv_image = (HorizontalListView) findViewById(R.id.h_scroll);
		discrib_content = (EditText) findViewById(R.id.discrib_content);

		imageAdapter = new ImageAdapter(this, bitmaps);
		gv_image.setAdapter(imageAdapter);

		// 使描述问题文本框获得焦点
		discrib_content.setFocusable(true);
		discrib_content.setFocusableInTouchMode(true);
		discrib_content.requestFocus();
		// discrib_content.setOnFocusChangeListener(new OnFocusChangeListener()
		// {
		// public void onFocusChange(View v, boolean hasFocus) {
		// EditText _v=(EditText)v;
		// if (!hasFocus) {// 失去焦点
		// _v.setHint(_v.getTag().toString());
		// } else {
		// String hint=_v.getHint().toString();
		// _v.setTag(hint);
		// _v.setHint("");
		// }
		// }
		// });
	}

	// 设置所有监听
	private void listens() {
		mTvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String discribe = discrib_content.getText().toString().trim();
				if (discribe.length() == 0 && picUri.size() == 0) {
					Toast.makeText(KnowDiscribeQActivity.this, "请完善内容", 0)
							.show();
					return;
				}

				Intent intent = new Intent(KnowDiscribeQActivity.this,
						KnowAddTopicActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(INTENT_DISCRIBEQ, discribe);
				bundle.putStringArrayList(INTENT_BITMAP, picUri);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_ADDTOPIC);

			}
		});

		/**
		 * 拍照
		 */
		gv_image.setOnListItemClickListener(new OnListItemClickListener() {

			@Override
			public void onClick(View v, int position) {

				if (position == 0) {
					// int account = bitmaps.size();
					// Toast.makeText(KnowDiscribeQActivity.this, account + "",
					// Toast.LENGTH_SHORT)
					// .show();
					if (bitmaps.size() < 3)
						takePhoto();
					else {
						Toast.makeText(KnowDiscribeQActivity.this, "最多能上传三张图片",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
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
				intent.addCategory(Intent.CATEGORY_DEFAULT);
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

	// 拍照获得的照片的gridview的配置
	private class ImageAdapter extends BaseAdapter {
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

		@SuppressLint("InflateParams")
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
								KnowDiscribeQActivity.this);
						builder.setTitle("您确定要删除该张图片吗？");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 通过相机获取
										bitmaps.remove(position - 1);
										picUri.remove(position - 1);
										imageAdapter = new ImageAdapter(
												KnowDiscribeQActivity.this,
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
					Utils.dp2pixel(KnowDiscribeQActivity.this, 80),
					Utils.dp2pixel(KnowDiscribeQActivity.this, 80));
			params.setMargins(Utils.dp2pixel(KnowDiscribeQActivity.this, 5), 0,
					Utils.dp2pixel(KnowDiscribeQActivity.this, 5), 0);

			convertView.setLayoutParams(params);

			return convertView;
		}

	}

	/**
	 * 拍照或是从相册中获得图片之后的处理 ，获得Bitmap值存储到数列中
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap = null;
		if (data != null && resultCode == RESULT_OK) {

			if (requestCode == REQUEST_CAMERA) {
				// 调用相机
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Toast.makeText(this, "sd不可用", Toast.LENGTH_SHORT).show();
					return;
				}

				Uri uri;
				Bundle bundle = data.getExtras();

				// 有两种手机，一种返回uri，一种返回bundle
				if (bundle != null) {
					bitmap = (Bitmap) bundle.get("data");
					uri = Uri.parse(MediaStore.Images.Media.insertImage(
							getContentResolver(), bitmap, null, null));
				} else {
					uri = data.getData();
					try {
						bitmap = MediaStore.Images.Media.getBitmap(
								this.getContentResolver(), uri);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				picUri.add(uri.toString());

			} else if (requestCode == REQUEST_PHOTO_ALBUM) {
				// 从相册来
				ContentResolver resolver = getContentResolver();
				Uri uri = data.getData();

				picUri.add(data.getDataString());
				try {
					byte[] pic = readStream(resolver.openInputStream(Uri
							.parse(uri.toString())));
					bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);

					// 获取相册的路径
					String[] projection = { MediaStore.Images.Media.DATA };
					@SuppressWarnings("deprecation")
					Cursor cursor = managedQuery(uri, projection, null, null,
							null);
					cursor.moveToFirst();

				} catch (Exception e) {
					e.printStackTrace();
				}
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
				imageAdapter = new ImageAdapter(KnowDiscribeQActivity.this,
						bitmaps);
				gv_image.setAdapter(imageAdapter);
			}

		}
		if (resultCode == KnowAddTopicActivity.RESULT_OVER) {// 从topic返回来的结果是成功了
			this.finish();
		}

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
}
