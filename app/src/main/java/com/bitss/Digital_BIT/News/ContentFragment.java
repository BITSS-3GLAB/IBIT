package com.bitss.Digital_BIT.News;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

@SuppressLint("ValidFragment")
public class ContentFragment extends Fragment {

	private Context context;
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
	private int page = 1;

	private HttpNewsAsker httpasker;
	private boolean isAlumni; // 标识是校友会还是普通新闻
	private String newsName; // 新闻的名字
	protected int newsType = 0;

	private View view;

	public ContentFragment() {
		this.newsType = 0;
	}

	public ContentFragment(boolean isAlumni, int newsType) {
		this.isAlumni = isAlumni;
		this.newsType = newsType;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_listview, container, false);
		init();
		return view;
	}

	@Override
	public void onDestroy() {
		int end = 10;
		if (mListItems.size() < 10)
			end = mListItems.size();
		NewsFileAsker f = new NewsFileAsker(context);
		f.writeFile(new LinkedList<NewsData>(mListItems.subList(0, end)),
				newsType);
		if (mListItems.size() > 0)
			f.cleanContent(newsType, mListItems.get(0).id);
		super.onDestroy();
	}

	/**
	 * 初始化所有控件
	 * */
	protected void init() {
		context = this.getActivity();
		newsName = ((NewsSlidingActivity) getActivity()).getTitleName();
		httpasker = new HttpNewsAsker(context);

		mPullRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_to_refresh_listview);
		mPullRefreshListView.setShowIndicator(false);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						doRefresh();

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						onMore();
					}
				});
		mPullRefreshListView
				.setRefreshingLabel(getString(R.string.news_is_loading));

		mActualListView = mPullRefreshListView.getRefreshableView();

		// empty view
		mListEmptyView = LayoutInflater.from(getActivity()).inflate(
				R.layout.list_empty, null);
		emptyTextView = (TextView) (mListEmptyView
				.findViewById(R.id.tv_emptyTitle));
		emptyTextView.setText(getString(R.string.news_is_loading));
		mActualListView.setEmptyView(mListEmptyView);

		mAdapter = new NewsListAdapter(context, isAlumni, newsName, newsType,
				mListItems);
		mActualListView.setAdapter(mAdapter);

		if (mListItems.size() == 0) {
			// 进入页面，先显示历史数据
			LinkedList<NewsData> list = new NewsFileAsker(context)
					.readFile(newsType);
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
		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(getActivity())) {
			Utils.showToast(getActivity(), Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

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
		/**
		 * @author 周俊皓 检查网络连接是否可用
		 */
		if (!Utils.isNetworkAvailable(getActivity())) {
			Utils.showToast(getActivity(), Constants.ERROR_NETWORK_UNAVAILABLE);
			return;
		}

		if (!mIsRefresh) {
			// 此时标记为下拉开始
			mIsRefresh = true;

			mLoadMoreTask = new LoadMoreTask();
			mLoadMoreTask.execute();
		} else {
			return;
		}

	}

	public void onRefreshComplete() {

	}

	public class RefreshTask extends AsyncTask<Void, Void, Integer> {
		@Override
		protected void onPreExecute() {
			mPullRefreshListView.setRefreshing();
			emptyTextView.setText(getString(R.string.news_is_loading));
		}

		@Override
		protected Integer doInBackground(Void... params) {
			int ans = 0;
			ans = httpasker.askForNewsList(isAlumni, mListItems, newsType);

			return ans;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result < 0) {
				HttpErrorToast.Show(context);
			} else {
				page = 1;
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
			int ans = httpasker.askForOldList(mListItems, newsType, page + 1);
			return ans;
		}

		@Override
		protected void onPostExecute(Integer result) {

			// 网络连接失败
			if (result < 0) {
				HttpErrorToast.Show(context);
			} else {
				page++;
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
