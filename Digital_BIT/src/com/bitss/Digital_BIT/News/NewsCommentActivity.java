package com.bitss.Digital_BIT.News;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class NewsCommentActivity extends ListActivity {

	private BaseApplication mApp;
	
	private ImageView btnBack;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mActualListView;
	private TextView mEmptyView;
	private CommentListAdapter adapter;
	private CommentFileAsker fileAsker;
	private LinkedList<CommentData> cListItems = new LinkedList<CommentData>();
	private LinkedList<CommentData> testItems = new LinkedList<CommentData>();

	private Bundle bundle;
	private Map<String, Bitmap> pictureMap = new HashMap<String, Bitmap>();
	private RefreshTask mGetDataTask;
	private LoadMoreTask mLoadMoreTask;

	private HttpCommentAsker httpCommentAsker;
	// 判断是否在刷新
	private boolean isRefresh = false;
	private int typeId = 1;
	private long newsId = 1;
	private int commNum = 3;
	private String newsTitle;
	private String newsTime;
	private int page = 1;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApp = (BaseApplication) getApplicationContext();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment_detail);

		// 获取从新闻内容页得到的信息
		Intent intent = this.getIntent();
		bundle = intent.getExtras();
		typeId = bundle.getInt("NewsType");
		newsId = bundle.getLong("NewsID");
		pictureMap = (Map<String, Bitmap>) bundle.get("NewsPictureList");
		newsTitle = bundle.getString("NewsTitle");
		newsTime = bundle.getString("NewsTime");

		CommentFileAsker fileAsker = new CommentFileAsker(
				NewsCommentActivity.this);
		fileAsker.writeFile(cListItems, typeId, newsId);

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
				NewsCommentActivity.this);
		alertDialog.setMessage(message);
		alertDialog.show();
	}

	@Override
	protected void onDestroy() {
		// 当这个程序退出的时候，将会保存最新的10条或者10条以下的信息
		int end = commNum;
		if (cListItems.size() < commNum)
			end = cListItems.size();
		fileAsker.writeFile(
				new LinkedList<CommentData>(cListItems.subList(0, end)),
				typeId, newsId);
		super.onDestroy();
	}

	protected void init() {

		fileAsker = new CommentFileAsker(NewsCommentActivity.this);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.second_hand_pull_to_refresh_listview);
		mEmptyView = (TextView) findViewById(R.id.tv_comment_empty);
		mPullRefreshListView.setEmptyView(mEmptyView);
		httpCommentAsker = new HttpCommentAsker();
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
			LinkedList<CommentData> list = new CommentFileAsker(
					NewsCommentActivity.this).readFile(typeId, newsId);
			cListItems.clear();
			cListItems.addAll(list);
			adapter.notifyDataSetChanged();
			// 将新的数据匹配上去

			if (cListItems.size() == 0) {
				// 默认不会刷新，只有在数据为空的时候才会刷新
				doFresh();
			} else {
				// mEmptyView.setVisibility(View.GONE);
			}
		}
	}

	public void doFresh() {
		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(this)) {
			Utils.showToast(this, Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

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
		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(this)) {
			Utils.showToast(this, Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

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
//			Integer resule = httpCommentAsker.askForMobileList(cListItems,
//					false, commNum, typeId, newsId, true);
			Integer resule = httpCommentAsker.askForMobileList(mApp, cListItems, newsId, 1);
			return resule;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 表示网络连接不成功
			if (result < 0) {
				HttpErrorToast.Show(NewsCommentActivity.this);
			} else {
				page = 1;
			}
			// mListItems.addFirst("Added after refresh...");
			/**
			 * 疑问2：怎么判断当前评论的ID是不是最新的ID，是通过获得的评论的ID来比较的吗？ 疑问3：服务器中返回的评论是最新的在前面的吗？
			 * 疑问4：这里的评论的拼接是怎么处理的？
			 */
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
			// Call onRefreshComplete when the list has been refreshed.
			// 而cListItems中是文件中最新的文件
			/**
			 * 疑问4：连接服务器的HttpAsker文件中返回0表示成功连接上了服务器，返回-1和-2都表示出错
			 * ibit中result是返回取得的最新评论的条数,怎么能够通过result来对评论进行处理呢
			 */
			isRefresh = false;
			mPullRefreshListView.onRefreshComplete();
			// if (cListItems.size() == 0) {
			// mEmptyView.setVisibility(View.VISIBLE);
			// } else {
			// mEmptyView.setVisibility(View.GONE);
			// }
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
//			Integer resule = httpCommentAsker.askForMobileList(cListItems,
//					true, commNum, typeId, newsId, false);
			Integer resule = httpCommentAsker.askForMobileList(mApp, cListItems, newsId, page + 1);
			return resule;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 表示网络连接不成功
			if (result < 0) {
				HttpErrorToast.Show(NewsCommentActivity.this);
			} else {
				page++;
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
