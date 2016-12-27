package com.bitss.Digital_BIT.Admission;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.News.HttpNewsAsker;
import com.bitss.Digital_BIT.News.NewsData;
import com.bitss.Digital_BIT.News.NewsFileAsker;
import com.bitss.Digital_BIT.News.NewsListAdapter;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AdmissionMainActivity extends CustomBaseActivity {

	private static final int ADMISSION_NEWS_TYPE = 31;

	private Context context;
	private HttpNewsAsker httpasker;

	private TextView mAdmisstionPlan;// 招生计划
	private TextView mAdmisstionScore;// 历年分数
	private TextView mAdmisstionCollegeInfo;// 学院纵览
	private TextView mAdmisstionAQ; // 招生问答

	private PullToRefreshListView mPullRefreshListView;
	private ListView mActualListView;
	private View mListEmptyView; // 空列表时显示的提示
	private TextView emptyTextView; // 动态修上面的文案

	private NewsListAdapter mAdapter = null;
	private LinkedList<NewsData> mListItems = new LinkedList<NewsData>();

	/** 此时下拉刷新是否在进行 */
	private boolean mIsRefresh = false;
	private RefreshTask mRefreshTask = null;
	private LoadMoreTask mLoadMoreTask = null;

	@Override
	public void onDestroy() {
		int end = 10;
		if (mListItems.size() < 10)
			end = mListItems.size();
		NewsFileAsker f = new NewsFileAsker(context);
		f.writeFile(new LinkedList<NewsData>(mListItems.subList(0, end)), ADMISSION_NEWS_TYPE);
		if (mListItems.size() > 0)
			f.cleanContent(ADMISSION_NEWS_TYPE, mListItems.get(0).id);
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admissiont_content_fragment);
		mTvNaviTitle.setText(getString(R.string.str_admission_news));

		initUI();
	}

	/**
	 * 初始化所有控件
	 * */
	protected void initUI() {
		context = this;
		httpasker = new HttpNewsAsker(context);

		mAdmisstionPlan = (TextView) findViewById(R.id.tv_admission_plan);
		mAdmisstionScore = (TextView) findViewById(R.id.tv_admission_score);
		mAdmisstionCollegeInfo = (TextView) findViewById(R.id.tv_admission_college_info);
		mAdmisstionAQ = (TextView) findViewById(R.id.tv_admission_aq);
		mAdmisstionPlan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AdmissionSearchActivity.class);
				intent.putExtra("search_type", "plan");
				startActivity(intent);
			}
		});
		mAdmisstionScore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AdmissionSearchActivity.class);
				intent.putExtra("search_type", "score");
				startActivity(intent);
			}
		});
		mAdmisstionCollegeInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, CollegeSlidingActivity.class);
				startActivity(intent);
			}
		});
		mAdmisstionAQ.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, AdmissionAQActivity.class);
				intent.putExtra("html_file", "zixun");
				startActivity(intent);
			}
		});

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.admission_refresh_listview);
		mPullRefreshListView.setShowIndicator(false);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				doRefresh();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				onMore();
			}
		});
		mPullRefreshListView.setRefreshingLabel(getString(R.string.news_is_loading));

		mActualListView = mPullRefreshListView.getRefreshableView();

		// empty view
		mListEmptyView = LayoutInflater.from(context).inflate(R.layout.list_empty, null);
		emptyTextView = (TextView) (mListEmptyView.findViewById(R.id.tv_emptyTitle));
		emptyTextView.setText(getString(R.string.news_is_loading));
		mActualListView.setEmptyView(mListEmptyView);

		// get data online
		mAdapter = new NewsListAdapter(context, false, getString(R.string.str_admission_news),
		        ADMISSION_NEWS_TYPE, mListItems);
		mActualListView.setAdapter(mAdapter);

		if (mListItems.size() == 0) {
			// 进入页面，先显示历史数据
			LinkedList<NewsData> list = new NewsFileAsker(context).readFile(ADMISSION_NEWS_TYPE);
			if (list.size() > 0) {
				// 已读标记
				for (int i = 0; i < list.size(); i++) {
					list.get(i).newTag = false;
				}
				mListItems.clear();
				mListItems.addAll(list);
				mAdapter.notifyDataSetChanged();
			}

			if (mListItems.size() == 0) {
				// 进入页面，默认不刷新，如果数据为空，则刷新
				doRefresh();
			}
		}
	}

	public void doRefresh() {
		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;

			mRefreshTask = new RefreshTask();
			mRefreshTask.execute();
		} else {
			return;
		}
	}

	public void onMore() {
		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;

			mLoadMoreTask = new LoadMoreTask();
			mLoadMoreTask.execute();
		} else {
			return;
		}
	}

	public class RefreshTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPreExecute() {
			mPullRefreshListView.setRefreshing();
			emptyTextView.setText(getString(R.string.news_is_loading));
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int ans = httpasker.askForNewsList(false, mListItems, ADMISSION_NEWS_TYPE);

			return ans;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result < 0) {
				HttpErrorToast.Show(context);
			}
			// empty view set the text "net error"
			if (mListItems.size() == 0) {
				emptyTextView.setText(getString(R.string.news_empty));
			}
			// 如果请求返回的最小id与请求前的最新id相差大于1，说明这之间可能还有数据，则把久的数据删掉
			if (result > 0 && mListItems.size() > result) {
				if (mListItems.get(result - 1).id - mListItems.get(result).id > 1) {
					while (mListItems.size() > result)
						mListItems.remove(mListItems.get(result));
				}
			}

			if (result > 0) {
				mAdapter.notifyDataSetChanged();
			}

			mPullRefreshListView.onRefreshComplete();
			// 此时下拉操作完毕
			mIsRefresh = false;
		}
	}

	public class LoadMoreTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPreExecute() {
			mPullRefreshListView.setRefreshing();
			emptyTextView.setText(getString(R.string.news_is_loading));
		}

		@Override
		protected Integer doInBackground(Void... params) {
			return httpasker.askForOldList(mListItems, 2, 1);
		}

		@Override
		protected void onPostExecute(Integer result) {

			// 网络连接失败
			if (result < 0) {
				HttpErrorToast.Show(context);
			}

			// empty view set the text "net error"
			if (mListItems.size() == 0) {
				emptyTextView.setText(getString(R.string.news_empty));
			}

			mAdapter.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
			// 此时下拉操作完毕
			mIsRefresh = false;
		}
	}

}
