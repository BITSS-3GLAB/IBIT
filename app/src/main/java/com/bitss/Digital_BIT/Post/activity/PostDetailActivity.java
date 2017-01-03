package com.bitss.Digital_BIT.Post.activity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

import org.json.JSONException;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.News.CommentData;
import com.bitss.Digital_BIT.News.CommentFileAsker;
import com.bitss.Digital_BIT.News.HttpCommentAsker;
import com.bitss.Digital_BIT.News.NewsCommentActivity;
import com.bitss.Digital_BIT.News.NewsReaderActivity;
import com.bitss.Digital_BIT.Post.provider.OAuthDataProvider;
import com.bitss.Digital_BIT.Post.provider.SQLiteDataProvider;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PostDetailActivity extends CustomBaseActivity {

	private TextView etxt;
	private ImageView ibtnComment, ibtnShare, imgOK, imgNO;
	private Dialog dialog;
	private EditText etxtName, etxtComment;
	private SQLiteDataProvider sqLite;
	private Bundle bundle;
	private long postID = 1;

	private ImageView ivPost;
	private TextView tvTitle, tvTime, tvLocation, tvPhone, tvHost, tvContent;
	private String postUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_post_detail);

		mTvNaviTitle.setText(getResources().getString(
				R.string.postdetailactivity_action_bar_title));
		init();

	}

	public void init() {
		etxt = (TextView) findViewById(R.id.ptxtComment);
		ibtnComment = (ImageView) findViewById(R.id.pbtnComment);
		ibtnShare = (ImageView) findViewById(R.id.pbtnShare);
		Intent intent = this.getIntent();
		bundle = intent.getExtras();
		postID = bundle.getLong("PostId");
		sqLite = new SQLiteDataProvider(PostDetailActivity.this);

		ivPost = (ImageView) findViewById(R.id.iv_post);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvLocation = (TextView) findViewById(R.id.tv_location);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
		tvHost = (TextView) findViewById(R.id.tv_host);
		tvContent = (TextView) findViewById(R.id.tv_content);

		postUrl = bundle.getString("PostImageUri");

		ImageLoader.getInstance().displayImage(postUrl, ivPost,
				mApp.getdisplayImageOptions());
		tvTitle.setText(bundle.getString("PostTitle"));
		tvTime.setText(bundle.getString("PostTimestamp"));
		tvLocation.setText(bundle.getString("PostLocation"));
		tvPhone.setText(bundle.getString("PostPhone"));
		tvHost.setText(bundle.getString("PostHost"));

		etxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// nActivity = new NewsCommentActivity();
				// showDialog();
				myselfDialog();

			}
		});

		// 显示别人评论的内容
		ibtnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putLong("PostId", postID);

				Intent intent = new Intent();
				intent = new Intent(PostDetailActivity.this,
						PostCommentActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});

		ibtnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				// intent.putExtra(Intent.EXTRA_SUBJECT, newsTitle);
				// intent.putExtra(Intent.EXTRA_TEXT, newsTitle + " " + newsTime
				// + shareString.substring(0, 30));
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "分享"));

			}
		});
	}

	public void myselfDialog() {
		dialog = new Dialog(PostDetailActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.outputcomment);

		Window diaWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = diaWindow.getAttributes();
		diaWindow.setGravity(Gravity.BOTTOM);
		lp.width = LayoutParams.FILL_PARENT;
		diaWindow.setAttributes(lp);
		imgOK = (ImageView) dialog.findViewById(R.id.imgOK);
		imgNO = (ImageView) dialog.findViewById(R.id.imgNo);

		imgNO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		imgOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				CommentData toPostData;
				etxtName = (EditText) dialog.findViewById(R.id.etxtName);
				etxtComment = (EditText) dialog
						.findViewById(R.id.etxtCommentDetail);

				String mTitle = etxtName.getText().toString();
				String mBody = etxtComment.getText().toString();

				// 将数据存进服务器中
				OAuthDataProvider oauProvider = new OAuthDataProvider(
						PostDetailActivity.this);
				try {
					int result = oauProvider
							.putCommentTo(postID, mTitle, mBody);
					if (result < 0) {
						HttpErrorToast.Show(PostDetailActivity.this);

					}
					if (result == 0) {
						Toast toast = Toast
								.makeText(
										PostDetailActivity.this,
										PostDetailActivity.this
												.getString(R.string.insert_comment_success),
										Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM, 0, 50);
						toast.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// 每次将评论发表之后都要刷新一次得到最新的评论信息

				dialog.dismiss();

			}
		});
		dialog.setTitle("评论");

		dialog.show();

	}

}
