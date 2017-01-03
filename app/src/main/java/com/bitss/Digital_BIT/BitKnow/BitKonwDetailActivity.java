package com.bitss.Digital_BIT.BitKnow;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.BitKnow.BitKnowAnswerAdapter.AnswerDeleteCallBack;
import com.bitss.Digital_BIT.BitKnow.BitKnowNetworkHandler;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class BitKonwDetailActivity extends CustomBaseActivity {
	private PullToRefreshListView pullToRefreshListView;
	private BitKnowHttpConnect myConnect;
	private boolean mIsRefresh = false;// 此时下拉刷新是否在进行
	private static final int DoRefresh = 1;
	private static final int OnMore = 0;
	private static List<BitKnowAnswerData> mData = new ArrayList<BitKnowAnswerData>();
	private TextView tv_info;
	private BitKnowAnswerAdapter listViewAdapter;
	private Context myActivity;
	private int number = 0;// 记录已经加载多少页
	private TextView questionusername;
	private TextView questiontime;
	private TextView question;
	private RoundedImageView questionusericon;
	private ImageView questionImageView1;
	private ImageView questionImageView2;
	private ImageView questionImageView3;
	private TextView questionlabel1;
	private TextView questionlabel2;
	private TextView questionlabel3;
	private ImageView questionzanImageView;
	private TextView questionzanTextView;
	private TextView questionanswernumberTextView;
	private LinearLayout questionImageViewLinearLayout;
	private String url1;
	private String url2;
	private String url3;
	private String urlicon;
	private TextView answerTextView;
	private EditText BitKnowComment;
	SharedPreferences settings;
	private Dialog dialog;
	private ImageView imgOK, imgNO;
	private EditText etxtName, etxtComment;
	private int id;
	private BitKnowMainData data;
	private Intent intent;
	private String phone;
	private String myphone;// 记录登录手机
	private DisplayImageOptions displayImageOptions;
	private int itemPosition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitknow_detail);
		init();
		listener();
	}	

	@Override
	public void backAction() {
		Intent intent = new Intent();
		intent.putExtra(Constants.KEY_ANSWER_COUNT, data.getNum());
		intent.putExtra(Constants.KEY_ITEM_POSITION, itemPosition);
		setResult(Constants.ANSWER_COUNT_REFRESH, intent);
		finish();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private void init() {
		displayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.uil_ic_stub)
				.showImageForEmptyUri(R.drawable.uil_ic_empty)
				.showImageOnFail(R.drawable.uil_ic_error)
				.resetViewBeforeLoading(false)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.displayer(new FadeInBitmapDisplayer(200)).cacheInMemory(true)
				.cacheOnDisc(true).build();

		mTvNaviTitle.setText(getResources().getString(R.string.bit_konw));// 此处应该改为获得姓名
		myConnect = new BitKnowHttpConnect(this);
		myConnect.setTimeOut(11000);
		tv_info = (TextView) findViewById(R.id.tv_answer_info);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.bit_know_pull_to_refresh_listview);
		pullToRefreshListView.setShowIndicator(false);
		pullToRefreshListView
				.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
		myActivity = this;
		questionusername = (TextView) findViewById(R.id.bitkonwname);
		questiontime = (TextView) findViewById(R.id.bitkonwtime);
		question = (TextView) findViewById(R.id.bitkonwquestion);
		questionusericon = (RoundedImageView) findViewById(R.id.bitkonwimageView);
		questionImageView1 = (ImageView) findViewById(R.id.bit_know_question_imageview1);
		questionImageView2 = (ImageView) findViewById(R.id.bit_know_question_imageview2);
		questionImageView3 = (ImageView) findViewById(R.id.bit_know_question_imageview3);
		questionlabel1 = (TextView) findViewById(R.id.bitkonwlabel1);
		questionlabel2 = (TextView) findViewById(R.id.bitkonwlabel2);
		questionlabel3 = (TextView) findViewById(R.id.bitkonwlabel3);
		questionzanTextView = (TextView) findViewById(R.id.bitknowanswernumber);
		questionImageViewLinearLayout = (LinearLayout) findViewById(R.id.bitknow_detail_qusetion_photo);
		BitKnowComment = (EditText) findViewById(R.id.bit_know_answer_textview);
		questionanswernumberTextView = (TextView) findViewById(R.id.bitknowanswernumber);
		settings = getSharedPreferences("User", 0);
		myphone = settings.getString(Constants.USER_PHONE, "");
		intent = getIntent();
		data = (BitKnowMainData) intent.getSerializableExtra("data");
		id = data.getId();
		urlicon = data.getPhotoUrl();
		itemPosition = intent.getIntExtra(Constants.KEY_ITEM_POSITION, -1);
		try {
			doRefresh();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mTvNaviTitle.setText("北理知道");
		mTvRight.setText("");
		mTvRight.setVisibility(View.VISIBLE);
		questionanswernumberTextView.setText("" + data.getNum() + "人回答");
	}

	@SuppressWarnings("unchecked")
	public void listener() {
		BitKnowComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (settings.getString(Constants.USER_PHONE, "").equals("")) {
					Utils.haveNotLogin(BitKonwDetailActivity.this);
					myphone = settings.getString(Constants.USER_PHONE, "");
				} else {
					myselfDialog();
				}
			}
		});
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				try {
					doRefresh();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				try {
					onMore();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		});
		mTvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	public void myselfDialog() {
		dialog = new Dialog(BitKonwDetailActivity.this);
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
							BitKonwDetailActivity.this,
							BitKonwDetailActivity.this
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

				// 每次将回答发表之后都要刷新一次得到最新的评论信息
				dialog.dismiss();

			}
		});
		etxtComment.setHint("请回答...");
		dialog.setTitle("回答");

		dialog.show();

	}

	private void addCommentData(String body)
			throws UnsupportedEncodingException {
		JSONObject json = new JSONObject();
		try {
			json.put("phone", settings.getString(Constants.USER_PHONE, ""));
			json.put("question_id", id);
			json.put("number_of_zan", 0);
			json.put("text", body);
		} catch (JSONException e) {

		}
		myConnect.doPost(Constants.BITKNOWTEST_SERVER_STRING + "GetNewAnswer",
				Constants.BITKNOWTEST_CLOUDSERVER_STRING + "GetNewAnswer",
				json, new BitKnowNetworkHandler() {

					@Override
					public void onSuccess(String str) {
						try {
							JSONObject json = new JSONObject(str);
							System.out.println(str);
							boolean isSuccess = json.getBoolean("success");
							if (isSuccess) {
								Toast.makeText(BitKonwDetailActivity.this,
										"回答成功", 0).show();
								try {
									doRefresh();
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

	private AnswerDeleteCallBack mCallback = new AnswerDeleteCallBack() {

		@Override
		public void onDeleteAnswer() {
			if (data != null) {
				data.deleteAnswer();
				String tmp = data.getNum() + "人回答";
				questionanswernumberTextView
						.setText(tmp);
			}
		}
	};

	private void getData(int refresh) throws UnsupportedEncodingException {
		switch (refresh) {
		case DoRefresh:
			JSONObject json = new JSONObject();
			try {
				json.put("offset", 0);
				json.put("number", 10);
				json.put("id", id);
				if (myphone != "") {
					json.put("phone", myphone);
				} else {
					json.put("phone", "-1");
				}
			} catch (JSONException e) {

			}
			myConnect.doPost(Constants.BITKNOWTEST_SERVER_STRING
					+ "SendSingleQuestionInfo",
					Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "SendSingleQuestionInfo", json,
					new BitKnowNetworkHandler() {

						@Override
						public void onSuccess(String str) {
							try {
								JSONObject json = new JSONObject(str);
								boolean isSuccess = json.getBoolean("success");
								if (isSuccess) {
									mData.clear();
									JSONObject result = json
											.getJSONObject("result");
									JSONObject questionInfo = result
											.getJSONObject("questionInfo");
									setQuestion(questionInfo);
									phone = questionInfo.getString("phone");
									JSONObject adaptedAnswer = result
											.getJSONObject("adaptedAnswer");
									if (adaptedAnswer.toString().length() != 15) {// 无论是否有赞，都会发送haszan这个标签
										BitKnowAnswerData data = new BitKnowAnswerData(
												adaptedAnswer, true);
										mData.add(data);
									}
									JSONArray answerInfo = result
											.getJSONArray("answerInfo");
									for (int i1 = 0; i1 < answerInfo.length(); i1++) {
										JSONObject item = answerInfo
												.getJSONObject(i1);
										BitKnowAnswerData data1 = new BitKnowAnswerData(
												item, false);
										if (mData.size() != 0
												&& mData.get(0).getisuse() == true) {
											if (mData.get(0).getid() != data1
													.getid()) {
												mData.add(data1);
											}
										} else {
											mData.add(data1);
										}
									}
									data.setNum(mData.size());
									questionanswernumberTextView.setText(""
											+ data.getNum() + "人回答");
								}

								// list = pullToRefreshListView
								// .getRefreshableView();
								if (mData.size() > 0) {
									pullToRefreshListView
											.setVisibility(View.VISIBLE);
									tv_info.setVisibility(View.GONE);
								} else {
									tv_info.setVisibility(View.VISIBLE);
								}
								if (listViewAdapter == null) {
									listViewAdapter = new BitKnowAnswerAdapter(
											myActivity, mData, data.getId(),
											phone, myphone, mCallback);
									pullToRefreshListView
											.setAdapter(listViewAdapter);// 为ListView控件绑定适配器
								} else {
									listViewAdapter.setphone(phone);
									listViewAdapter.setMyphone(settings
											.getString(Constants.USER_PHONE, ""));
									listViewAdapter.notifyDataSetChanged();
									pullToRefreshListView
											.setAdapter(listViewAdapter);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								number = 0;
								pullToRefreshListView.onRefreshComplete();
								mIsRefresh = false;
							}

						}

						@Override
						public void onFailure() {
							pullToRefreshListView.onRefreshComplete();
							Toast.makeText(myActivity, "网络异常",
									Toast.LENGTH_SHORT).show();
							mIsRefresh = false;
						}
					});

			break;
		case OnMore:
			JSONObject json1 = new JSONObject();
			try {
				json1.put("offset", mData.size());
				json1.put("number", 10);
				json1.put("id", id);
				if (myphone != "") {
					json1.put("phone", myphone);
				} else {
					json1.put("phone", "-1");
				}

			} catch (JSONException e) {

			}
			myConnect.doPost(Constants.BITKNOWTEST_SERVER_STRING
					+ "SendSingleQuestionInfo",
					Constants.BITKNOWTEST_CLOUDSERVER_STRING
							+ "SendSingleQuestionInfo", json1,
					new BitKnowNetworkHandler() {

						@Override
						public void onSuccess(String str) {
							try {
								JSONObject json = new JSONObject(str);
								boolean isSuccess = json.getBoolean("success");
								if (isSuccess) {

									JSONObject result = json
											.getJSONObject("result");
									// JSONObject adaptedAnswer = result
									// .getJSONObject("adaptedAnswer");
									// if (adaptedAnswer.toString().length() >
									// 2) {
									// BitKnowAnswerData data = new
									// BitKnowAnswerData(
									// adaptedAnswer, true);
									// mData.add(data);
									// }
									JSONArray answerInfo = result
											.getJSONArray("answerInfo");
									for (int i1 = 0; i1 < answerInfo.length(); i1++) {
										JSONObject item = answerInfo
												.getJSONObject(i1);
										BitKnowAnswerData data1 = new BitKnowAnswerData(
												item, false);
										if (mData.size() != 0
												&& mData.get(0).getisuse() == true) {
											if (mData.get(0).getid() != data1
													.getid()) {
												mData.add(data1);
											}
										} else {
											mData.add(data1);
										}
									}

									if (answerInfo.length() > 0) {
										Toast.makeText(myActivity,
												"成功获取加载更多数据",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(myActivity, "没有更多数据了",
												Toast.LENGTH_SHORT).show();
									}
									data.setNum(mData.size());
									questionanswernumberTextView.setText(""
											+ data.getNum() + "人回答");
								}

								if (mData.size() > 0) {
									pullToRefreshListView
											.setVisibility(View.VISIBLE);
									tv_info.setVisibility(View.GONE);
								} else {
									tv_info.setVisibility(View.VISIBLE);
								}
								listViewAdapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							} finally {
								number = 0;
								pullToRefreshListView.onRefreshComplete();
								mIsRefresh = false;
							}

						}

						@Override
						public void onFailure() {

						}
					});
			break;

		default:
			break;
		}

	}

	private void doRefresh() throws UnsupportedEncodingException {
		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;
			getData(DoRefresh);

		} else {
			return;
		}
	}

	private void onMore() throws UnsupportedEncodingException {
		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;
			getData(OnMore);

		} else {
			return;
		}

	}

	private void setQuestion(JSONObject questionInfo) {
		try {
			url1 = questionInfo.getString("picUrl1");
			url2 = questionInfo.getString("picUrl2");
			url3 = questionInfo.getString("picUrl3");
			String time = questionInfo.getString("time");
			String tag = questionInfo.getString("tag");
			List<String> labeList = new ArrayList<String>();
			time = time.substring(0, time.length() - 2);
			question.setText(questionInfo.getString("text"));
			questiontime.setText(time);
			int j = 0;// 记录前一个|的位置
			int tagnumber = 0;
			for (int i = 0; i < tag.length(); i++) {
				String temp = String.valueOf(tag.charAt(i));
				String label;
				if (temp.equals("|")) {
					label = tag.substring(j, i);
					labeList.add(label);
					j = i + 1;
					tagnumber++;
				}
			}
			tagnumber++;
			labeList.add(tag.substring(j, tag.length()));
			if (labeList.size() >= 3) {
				questionlabel1.setText(labeList.get(0));
				questionlabel2.setText(labeList.get(1));
				questionlabel3.setText(labeList.get(2));
			} else if (labeList.size() == 2) {
				questionlabel1.setText(labeList.get(0));
				questionlabel2.setText(labeList.get(1));
				questionlabel3.setVisibility(View.GONE);
			} else if (labeList.size() == 1) {
				questionlabel1.setText(labeList.get(0));
				questionlabel2.setVisibility(View.GONE);
				questionlabel3.setVisibility(View.GONE);
			} else if (labeList.size() == 0) {
				questionlabel1.setVisibility(View.GONE);
				questionlabel2.setVisibility(View.GONE);
				questionlabel3.setVisibility(View.GONE);
			}
			/*
			 * if(url1.contains("无")&&url2.contains("无")&&url3.contains("无")){
			 * questionImageViewLinearLayout.setVisibility(View.GONE); } else {
			 * if(url1.contains("无")==false){ url1 =
			 * url1.substring(1,url1.length());
			 * ImageLoader.getInstance().displayImage
			 * (Constants.BITKNOWTEST_SERVER_STRING+url1,questionImageView1); }
			 * else{ questionImageView1.setVisibility(View.GONE); }
			 * if(url2.contains("无")==false){ url2 =
			 * url2.substring(1,url2.length());
			 * ImageLoader.getInstance().displayImage
			 * (Constants.BITKNOWTEST_SERVER_STRING+url2,questionImageView2); }
			 * else{ questionImageView2.setVisibility(View.GONE); }
			 * if(url3.contains("无")==false){ url3 =
			 * url3.substring(1,url3.length());
			 * ImageLoader.getInstance().displayImage
			 * (Constants.BITKNOWTEST_SERVER_STRING+url3,questionImageView3); }
			 * else{ questionImageView3.setVisibility(View.GONE); } }
			 */

			final String imageUrl1 = Constants.PHTOT_CLOUDSERVER_STRING + url1;
			final String imageUrl2 = Constants.PHTOT_CLOUDSERVER_STRING + url2;
			final String imageUrl3 = Constants.PHTOT_CLOUDSERVER_STRING + url3;

			questionImageView1.setVisibility(View.GONE);
			questionImageView2.setVisibility(View.GONE);
			questionImageView3.setVisibility(View.GONE);

			int image_num = 0;
			if (!url1.equals("无"))
				image_num++;
			if (!url2.equals("无"))
				image_num++;
			if (!url3.equals("无"))
				image_num++;

			switch (image_num) {
			case 3:
				questionImageView3.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(imageUrl3,
						questionImageView3, displayImageOptions);
				questionImageView3.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BitKonwDetailActivity.this,
								BitKnowDetailPhotoActivity.class);
						intent.putExtra("url", imageUrl3);
						startActivity(intent);
					}
				});
			case 2:
				questionImageView2.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(imageUrl2,
						questionImageView2, displayImageOptions);
				questionImageView2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BitKonwDetailActivity.this,
								BitKnowDetailPhotoActivity.class);
						intent.putExtra("url", imageUrl2);
						startActivity(intent);
					}
				});
			case 1:
				questionImageView1.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(imageUrl1,
						questionImageView1, displayImageOptions);
				questionImageView1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BitKonwDetailActivity.this,
								BitKnowDetailPhotoActivity.class);
						intent.putExtra("url", imageUrl1);
						startActivity(intent);
					}
				});
				break;
			default:
				break;
			}

			String photoUrl = Constants.PHTOT_CLOUDSERVER_STRING + urlicon;
			questionusername.setText(data.getName());
			ImageLoader.getInstance().displayImage(photoUrl, questionusericon,
					displayImageOptions);
			// ImageLoader.getInstance().displayImage(Constants.BITKNOWTEST_SERVER_STRING+urlicon,questionusericon);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
}
