package com.bitss.Digital_BIT.SecondHandMarket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.News.CommentData;
import com.bitss.Digital_BIT.News.CommentListAdapter;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.View.MyListView;
import com.viewpagerindicator.UnderlinePageIndicator;

public class SecondHandDetailActivity extends CustomBaseActivity {

	private ViewPager viewPager;
	private UnderlinePageIndicator secondHandLinePageIndicator;
	private TextView tv_title, tv_time, tv_price, tv_campus, tv_detail,
			writeComment, tv_comment_info, tv_without_image;
	private MyListView lv_comment;
	private ImageView imgOK, imgNO, iv_share, iv_store;
	private EditText etxtName, etxtComment;

	private ArrayList<String> imageUrl = new ArrayList<String>();
	private SecondHandMarketHttpConnection mConnection;
	private SecondHandMarketData data;
	private Dialog dialog;
	SharedPreferences settings;

	private LinkedList<CommentData> commentDatas = new LinkedList<CommentData>();

	private String sellerPhone;
	private String userPhone;
	private String describe;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_hand_detail);
		mTvNaviTitle.setText(getResources().getString(R.string.detail));
		Init();
		listener();

	}

	public void Init() {
		data = (SecondHandMarketData) getIntent().getSerializableExtra("data");
		settings = getSharedPreferences("User", 0);
		mIvNaviShare.setVisibility(View.VISIBLE);
		mIvNaviShare.setImageResource(R.drawable.phone);
		writeComment = (TextView) findViewById(R.id.etxtComment);
		tv_comment_info = (TextView) findViewById(R.id.tv_comment_info);
		tv_without_image = (TextView) findViewById(R.id.tv_without_image);

		mConnection = new SecondHandMarketHttpConnection(this);
		mConnection.setTimeOut(11000);

		viewPager = (ViewPager) findViewById(R.id.dialog_classifyPager);

		if (!data.getUrl1().equals("")) {
			imageUrl.add(Constants.BITKNOWTEST_CLOUDSERVER_STRING
					+ data.getUrl1());
			if (!data.getUrl2().equals("")) {
				imageUrl.add(Constants.BITKNOWTEST_CLOUDSERVER_STRING
						+ data.getUrl2());
				if (!data.getUrl3().equals("")) {
					imageUrl.add(Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ data.getUrl3());

				}

			}
		} else {
			// imageUrl.add(Constants.TEST_SERVER_STRING
			// + "goods_images/uil_ic_empty.png");
			viewPager.setVisibility(View.GONE);
			tv_without_image.setVisibility(View.VISIBLE);

		}
		secondHandLinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.secondhandindictor);
		viewPager.setAdapter(new ImagePagerAdapter(imageUrl,
				SecondHandDetailActivity.this));
		secondHandLinePageIndicator.setViewPager(viewPager);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_campus = (TextView) findViewById(R.id.tv_campue);
		tv_detail = (TextView) findViewById(R.id.tv_moredescript_info);

		tv_title.setText(data.getTitle());
		tv_time.setText("发布时间  " + data.getTime().substring(0, 10));
		tv_price.setText(data.getPrice().toString() + "元");
		if (data.getCampus().equals("0"))
			tv_campus.setText("中关村校区");
		else
			tv_campus.setText("良乡校区");
		tv_detail.setText(data.getDesc());

		lv_comment = (MyListView) findViewById(R.id.lv_comment);

		try {
			getCommentData();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		iv_share = (ImageView) findViewById(R.id.ibtnShare);
		iv_store = (ImageView) findViewById(R.id.ibtnComment);

	}

	public void takePhone() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择");
		builder.setPositiveButton("发短信", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Uri uri = Uri.parse("smsto:" + sellerPhone);

				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

				intent.putExtra("sms_body", "");

				startActivity(intent);

			}
		});
		builder.setNegativeButton("打电话", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent phoneIntent = new Intent("android.intent.action.CALL",
						Uri.parse("tel:" + sellerPhone));
				startActivity(phoneIntent);
			}
		});
		//

		builder.create().show();
	}

	public void listener() {
		writeComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				userPhone = settings.getString(Constants.USER_PHONE, "");
				if (userPhone.equals("")) {
					Utils.haveNotLogin(SecondHandDetailActivity.this);
				} else {

					myselfDialog();
				}

			}
		});

		mIvNaviShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				takePhone();
			}
		});

		iv_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "二手市场");
				intent.putExtra(Intent.EXTRA_TEXT, data.getTitle() + " "
						+ describe + " 仅需" + data.getPrice() + "元-北理校园通二手市场");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));

			}
		});

		iv_store.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (settings.getString(Constants.USER_PHONE, "").equals("")) {
					Utils.haveNotLogin(SecondHandDetailActivity.this);
				} else {
					try {
						storeProduct();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

			}
		});
	}

	public void myselfDialog() {
		dialog = new Dialog(SecondHandDetailActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.outputcomment);

		Window diaWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = diaWindow.getAttributes();
		diaWindow.setGravity(Gravity.BOTTOM);
		lp.width = LayoutParams.FILL_PARENT;
		diaWindow.setAttributes(lp);
		imgOK = (ImageView) dialog.findViewById(R.id.imgOK);
		imgNO = (ImageView) dialog.findViewById(R.id.imgNo);
		etxtName = (EditText) dialog.findViewById(R.id.etxtName);
		etxtName.setVisibility(View.GONE);
		etxtComment = (EditText) dialog.findViewById(R.id.etxtCommentDetail);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(etxtComment, InputMethodManager.SHOW_FORCED);
		imgNO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		imgOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String mTitle = etxtName.getText().toString();
				String mBody = etxtComment.getText().toString();

				if (mBody.trim().length() == 0) {
					Toast.makeText(
							SecondHandDetailActivity.this,
							SecondHandDetailActivity.this
									.getString(R.string.warning_comment_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(etxtComment.getWindowToken(), 0); // 强制隐藏键盘
				try {
					addCommentData(mBody);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// 将评论的信息发送到数据库进行存储

				// 每次将评论发表之后都要刷新一次得到最新的评论信息
				dialog.dismiss();

			}
		});
		dialog.setTitle("评论");

		dialog.show();

	}

	private void storeProduct() throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("phone", settings.getString(Constants.USER_PHONE, ""));
			json.put("goodID", data.getStringId());
		} catch (JSONException e) {

		}
		mConnection.doPost(Constants.TEST_SERVER_STRING + "AddGoodsCollect",
				Constants.BITKNOWTEST_CLOUDSERVER_STRING + "AddGoodsCollect",
				json, new SecondHandMarketNetworkHandler() {

					@Override
					public void onSuccess(String str) {
						try {
							JSONObject json = new JSONObject(str);
							if (json.getBoolean("success")) {
								Toast.makeText(SecondHandDetailActivity.this,
										"收藏成功", 0).show();
							} else {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										SecondHandDetailActivity.this);
								builder.setTitle("您已收藏过该物品，是否要取消收藏？");
								builder.setPositiveButton("是",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												try {
													NotstoreProduct();
												} catch (UnsupportedEncodingException e) {
													// block
													e.printStackTrace();
												}
											}
										});
								builder.setNegativeButton("否",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
											}
										});

								builder.create().show();
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

	private void NotstoreProduct() throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("phone", settings.getString(Constants.USER_PHONE, ""));
			json.put("goodID", data.getStringId());
		} catch (JSONException e) {

		}
		mConnection
				.doPost(Constants.TEST_SERVER_STRING + "CancelGoodsCollect",
						Constants.BITKNOWTEST_CLOUDSERVER_STRING
								+ "CancelGoodsCollect", json,
						new SecondHandMarketNetworkHandler() {

							@Override
							public void onSuccess(String str) {
								try {
									JSONObject json = new JSONObject(str);
									if (json.getBoolean("success")) {
										Toast.makeText(
												SecondHandDetailActivity.this,
												"已取消收藏", 0).show();
									} else {
										Toast.makeText(
												SecondHandDetailActivity.this,
												"取消收藏失败", 0).show();
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

	private void getCommentData() throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("offset", "0");
			json.put("number", "100");
			json.put("id", data.getStringId());
		} catch (JSONException e) {

		}
		mConnection
				.doPost(Constants.TEST_SERVER_STRING + "SendSingleGoodInfo",
						Constants.BITKNOWTEST_CLOUDSERVER_STRING
								+ "SendSingleGoodInfo", json,
						new SecondHandMarketNetworkHandler() {

							@Override
							public void onSuccess(String str) {
								try {
									JSONObject json = new JSONObject(str);
									System.out.println(str);
									boolean isSuccess = json
											.getBoolean("success");
									if (isSuccess) {
										JSONObject result = json
												.getJSONObject("result");
										JSONObject records = result
												.getJSONObject("goodInfo");

										sellerPhone = records.getString("phone");
										describe = records
												.getString("description");
										tv_detail.setText(describe);

										JSONArray comment = result
												.getJSONArray("goodComments");
										commentDatas.clear();

										for (int i = 0; i < comment.length(); i++) {
											JSONObject item = comment
													.getJSONObject(i);
											String time = item
													.getString("time");
											time = time.substring(0,
													time.length() - 2);
											commentDatas.add(new CommentData(
													item.getString("username"),
													item.getString("text"),
													time,
													item.getString("photoUrl"),
													0, 0, 0));
										}

										lv_comment
												.setAdapter(new CommentListAdapter(
														SecondHandDetailActivity.this,
														commentDatas));
										if (commentDatas.size() == 0) {
											tv_comment_info
													.setVisibility(View.VISIBLE);
											lv_comment.setVisibility(View.GONE);
										} else {
											tv_comment_info
													.setVisibility(View.GONE);
											lv_comment
													.setVisibility(View.VISIBLE);
										}
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

	private void addCommentData(String body)
			throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("phone", userPhone);
			json.put("text", body);
			json.put("goodId", data.getStringId());
		} catch (JSONException e) {

		}
		mConnection.doPost(Constants.TEST_SERVER_STRING + "GetNewGoodComment",
				Constants.BITKNOWTEST_CLOUDSERVER_STRING + "GetNewGoodComment",
				json, new SecondHandMarketNetworkHandler() {

					@Override
					public void onSuccess(String str) {
						try {
							JSONObject json = new JSONObject(str);
							System.out.println(str);
							boolean isSuccess = json.getBoolean("success");
							if (isSuccess) {
								Toast.makeText(SecondHandDetailActivity.this,
										"评论成功", 0).show();
								try {
									getCommentData();
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
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
