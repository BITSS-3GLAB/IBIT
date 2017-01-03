package com.bitss.Digital_BIT.Post.activity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.News.CommentData;
import com.bitss.Digital_BIT.News.CommentFileAsker;
import com.bitss.Digital_BIT.News.CommentListAdapter;
import com.bitss.Digital_BIT.News.HttpCommentAsker;
import com.bitss.Digital_BIT.Post.provider.OAuthDataProvider;
import com.bitss.Digital_BIT.Post.provider.SQLiteDataProvider;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.umeng.analytics.MobclickAgent;

import android.view.View.OnClickListener;

public class PostCommentActivity extends ListActivity {

	private ImageView btnBack;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mActualListView;
	private CommentListAdapter adapter;
	private CommentFileAsker fileAsker;
	private LinkedList<CommentData> cListItems = new LinkedList<CommentData>();
	private LinkedList<CommentData> testItems = new LinkedList<CommentData>();

	private Bundle bundle;
	private RefreshTask mGetDataTask;
	private LoadMoreTask mLoadMoreTask;
	private SQLiteDataProvider sqLite;
	private OAuthDataProvider oAuth;

	// 判断是否在刷新
	private boolean isRefresh = false;
	private long announcedId = 1;
	private int commNum = 3;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment_detail);

		// 获取从新闻内容页得到的信息
		Intent intent = this.getIntent();
		bundle = intent.getExtras();
		announcedId = bundle.getLong("PostId");

		// CommentFileAsker fileAsker = new
		// CommentFileAsker(NewsCommentActivity.this);
		// fileAsker.writeFile(cListItems, typeId, newsId);

		init();

		((TextView) findViewById(R.id.tv_navi_title)).setText("评论");
		((ImageView) findViewById(R.id.iv_navi_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						finish();
					}
				});

	}

	/**
	 * 调试用的方法
	 * 
	 * @param message
	 */

	public void Debug(String message) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				PostCommentActivity.this);
		alertDialog.setMessage(message);
		alertDialog.show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 当这个程序退出的时候，将会保存最新的10条或者10条以下的信息
		int end = commNum;
		if (cListItems.size() < commNum)
			end = cListItems.size();
		sqLite.savePostComment(new LinkedList<CommentData>(cListItems.subList(
				0, end)));
		super.onDestroy();
	}

	protected void init() {

		sqLite = new SQLiteDataProvider(PostCommentActivity.this);
		oAuth = new OAuthDataProvider(PostCommentActivity.this);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_to_refresh_listview);
		mActualListView = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setShowIndicator(false);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				doFresh();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				onMore();
			}
		});
		// 将数据信息匹配到这个List当中，但是在这个时候列表中的信息还是空的
		adapter = new CommentListAdapter(this, cListItems);
		mActualListView.setAdapter(adapter);

		// 在进入界面的时候默认不会进行刷新，先显示本地文件中的数据
		if (cListItems.size() == 0) {
			// 在页面起始先显示历史数据
			LinkedList<CommentData> list;
			cListItems.clear();

			list = sqLite.getPostComment(announcedId, commNum);
			cListItems.addAll(list);
			adapter.notifyDataSetChanged();
			// 将新的数据匹配上去

			if (cListItems.size() == 0) {
				// 默认不会刷新，只有在数据为空的时候才会刷新
				doFresh();
			}

		}
	}

	public void doFresh() {
		if (!isRefresh) {
			// 表示下拉开始
			isRefresh = true;
			mGetDataTask = new RefreshTask();
			mGetDataTask.execute();

		} else {
			return;
		}

	}

	public void onMore() {
		if (!isRefresh) {
			isRefresh = true;

			mLoadMoreTask = new LoadMoreTask();
			mLoadMoreTask.execute();

		}
	}

	private class RefreshTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPullRefreshListView.setRefreshing();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// Simulates a background job.
			// 向服务器申请数据信息，返回一些数字标识，看是否连接上服务器

			// 将得到的数据存储在传进的参数中
			/**
			 * 疑问1：传进去的参数，然后在里面给他进行赋值，该变量就能够获得赋予它的值吗？ 是的
			 * 传进去的是从文件中取得的最新的数据，返回来的是新添加的条数
			 */
			Integer result = oAuth.getCommentList(cListItems, announcedId, 0,
					false, true, commNum);

			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 表示网络连接不成功
			if (result < 0) {
				HttpErrorToast.Show(PostCommentActivity.this);
			}

			adapter.notifyDataSetChanged();

			isRefresh = false;
			mPullRefreshListView.onRefreshComplete();

		}

	}

	public class LoadMoreTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mPullRefreshListView.setRefreshing();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Integer result = oAuth.getCommentList(cListItems, announcedId, 0,
					true, false, commNum);

			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 表示网络连接不成功
			if (result < 0) {
				HttpErrorToast.Show(PostCommentActivity.this);
			}

			// 返回的正好是能与当前id连接上的东西，所以不需要进行拼接
			// if(result > 0 && cListItems.size() > result){
			// //表示有新添的评论
			// if(cListItems.get(result-1).id - cListItems.get(result).id > 1){
			// while(cListItems.size() > result)
			// cListItems.remove(cListItems.get(result));
			// }
			//
			//
			// }
			adapter.notifyDataSetChanged();
			isRefresh = false;
			mPullRefreshListView.onRefreshComplete();

		}

	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
