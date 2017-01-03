package com.bitss.Digital_BIT.Post.fragment;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bitss.Digital_BIT.BaseActivity;
import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.News.NewsReaderActivity;
import com.bitss.Digital_BIT.Post.activity.PostDetailActivity;
import com.bitss.Digital_BIT.Post.model.PostInformation;
import com.bitss.Digital_BIT.Post.provider.DataProvider;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.origamilabs.library.views.StaggeredGridView;

public class PostListFragment extends SherlockFragment {

	private PullToRefreshStaggeredGridView mPullRefreshStaggeredGridView;
	private StaggeredGridView mStaggeredGridView;
	private PostListAdapter mAdapter;
	private final List<PostInformation> mData = new ArrayList<PostInformation>();
	private int type;
	private View mListEmptyView;

	protected long corId = -1;

	protected long mMinPostId = 0;
	private long mMaxPostId = 0;

	protected long mOldMaxPostId = 0;

	private RefreshTask mRefreshTask = null;
	private LoadMoreTask mLoadMoreTask = null;

	private final PostInformation tempPostInformation = null;

	private BaseApplication mApp = null;
	// Save ListView state
	private Parcelable staggeredGridViewState = null;

	protected DataProvider mProvider = null;

	// TODO change to get Instance
	public static PostListFragment newInstance() {
		PostListFragment f = new PostListFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(Constants.KEY_POST_TYPE)) {
			type = savedInstanceState.getInt(Constants.KEY_POST_TYPE);
		} else {
			type = (getArguments() != null) ? getArguments().getInt(
					Constants.KEY_POST_TYPE) : 0;
		}

		mApp = (BaseApplication) getActivity().getApplicationContext();
		mProvider = mApp.getProvider();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(Constants.KEY_POST_TYPE, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ptr_staggered_grid_view,
				container, false);
		mPullRefreshStaggeredGridView = (PullToRefreshStaggeredGridView) view
				.findViewById(R.id.pull_refresh_staggered_grid);
		mPullRefreshStaggeredGridView.setMode(Mode.BOTH);

		mStaggeredGridView = mPullRefreshStaggeredGridView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshStaggeredGridView
				.setOnRefreshListener(new OnRefreshListener2<StaggeredGridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<StaggeredGridView> refreshView) {
						// Toast.makeText(mApp, "Pull Down!",
						// Toast.LENGTH_SHORT).show();
						onRefresh();

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<StaggeredGridView> refreshView) {

						Log.i("PostListFragment", "onCreateView mMinPostId:"
								+ mMinPostId);

						// Toast.makeText(mApp, "Pull Up!",
						// Toast.LENGTH_SHORT).show();
						staggeredGridViewState = mPullRefreshStaggeredGridView
								.getRefreshableView().onSaveInstanceState();
						onMore();

					}

				});

		mStaggeredGridView = mPullRefreshStaggeredGridView.getRefreshableView();

		mAdapter = new PostListAdapter(getActivity(), mData);
		mStaggeredGridView.setAdapter(mAdapter);

		if (!Utils.isNetworkAvailable(mApp)) {
			HttpErrorToast.Show(mApp);
		}

		// 生成界面的时候显示历史数据
		if (mData.size() == 0) {
			// 进入页面，先显示历史数据
			List<PostInformation> list = getInitData(Constants.COUNT_PER_PAGE);
			if (list.size() > 0) {
				mData.clear();
				mData.addAll(list);
				mAdapter.notifyDataSetChanged();
			}

			if (mData.size() == 0) {
				// 进入页面，默认不刷新，如果数据为空，则刷新
				onRefresh();
			}
		}

		return view;
	}

	public List<PostInformation> getInitData(int count) {
		List<PostInformation> list = mProvider.getPostInitData(count, corId);
		if (list.size() > 0) {
			mMinPostId = list.get(list.size() - 1).getPostId();
			mOldMaxPostId = list.get(0).getPostId();

		}
		Log.i("PostListFragment", "getInitData list.size():" + list.size());
		Log.i("PostListFragment", "getInitData mMinPostId:" + mMinPostId);
		Log.i("PostListFragment", "getInitData mOldMaxPostId:" + mOldMaxPostId);
		return list;
	}

	public List<PostInformation> getRefreshData(int count, long corId)
			throws IOException, JSONException {
		List<PostInformation> list = mProvider.getPostInformationList(0, 0,
				count, corId);
		if (list.size() > 0) {
			mMinPostId = list.get(list.size() - 1).getPostId();
			mMaxPostId = list.get(0).getPostId();
		}
		Logger.i("PostListFragment",
				"getRefreshData list.size():" + list.size());
		Logger.i("PostListFragment", "getRefreshData mMinPostId:" + mMinPostId);
		Logger.i("PostListFragment", "getRefreshData mOldMaxPostId:"
				+ mOldMaxPostId);
		return list;
	}

	public List<PostInformation> getMoreData(int count, long corId)
			throws IOException, JSONException {
		List<PostInformation> list = new ArrayList<PostInformation>(count);

		// 当unitlId减到0的时候记在更多默认返回空的

		if (0 == mMinPostId - 1) {
			return list;
		}

		try {

			list = mProvider.getPostInformationList(0, mMinPostId - 1, count,
					corId);
		} catch (IOException e) {
			Log.i("PostListFragment", "getMoreData from net failed");
			Log.i("PostListFragment", "getMoreData from net failed mMinPostId:"
					+ mMinPostId);
			// 如果从网络获取失败，那么从本地数据库获取
			list = mProvider.getSQLiteDataProvider().getPostInformationList(0,
					mMinPostId - 1, count, corId);
			// if (list.size() == 0) {
			// throw e;
			// }

		}

		if (list.size() > 0) {
			mMinPostId = list.get(list.size() - 1).getPostId();
			Log.i("PostListFragment", "getMoreData after mMinPostId:"
					+ mMinPostId);
		}

		return list;
	}

	public void onRefresh() {
		if (mRefreshTask != null) {
			mRefreshTask.cancel(true);
		}
		mRefreshTask = new RefreshTask();
		mRefreshTask.execute();
	}

	public void onMore() {
		if (mLoadMoreTask != null) {
			mLoadMoreTask.cancel(true);
		}
		mLoadMoreTask = new LoadMoreTask();
		mLoadMoreTask.execute();
	}

	public class RefreshTask extends
			AsyncTask<Void, Void, List<PostInformation>> {
		@Override
		protected List<PostInformation> doInBackground(Void... params) {
			try {

				return getRefreshData(Constants.COUNT_PER_PAGE, corId);
			} catch (Exception e) {
				((BaseActivity) getActivity()).handleException(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<PostInformation> result) {
			if (result != null) {
				if (result.size() < Constants.COUNT_PER_PAGE) {
					// 没有下一页了
					mPullRefreshStaggeredGridView.setMode(Mode.PULL_FROM_START);
				}

				if (result.size() > 0) {
					mData.clear();
					mData.addAll(result);
					mAdapter.notifyDataSetChanged();
				} else {
					mData.clear();
					mAdapter.notifyDataSetChanged();
				}

				if (mOldMaxPostId < mMaxPostId) {
					// 更新了几条数据
					int count = 0;
					for (int i = 0; i < result.size(); i++) {
						if (result.get(i).getPostId() > mOldMaxPostId) {
							count++;
						} else {
							break;
						}
					}
					Utils.showToast(getActivity(),
							String.format("海报更新了", count));
					mOldMaxPostId = mMaxPostId;
				} else {
					// 没有更新的数据
					Utils.showToast(getActivity(), "海报没有更新");
				}
			}

			onRefreshComplete();
		}

	}

	public void onRefreshComplete() {
		mPullRefreshStaggeredGridView.onRefreshComplete();
	}

	public class LoadMoreTask extends
			AsyncTask<Void, Void, List<PostInformation>> {
		@Override
		protected void onPreExecute() {

		}

		@Override
		protected List<PostInformation> doInBackground(Void... params) {
			try {

				return getMoreData(Constants.COUNT_PER_PAGE, corId);
			} catch (Exception e) {
				((BaseActivity) getActivity()).handleException(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<PostInformation> result) {
			if (result != null) {
				if (result.size() < Constants.COUNT_PER_PAGE) {
					// 没有更多了
					Utils.showToast(getActivity(), "没有更多的海报了");
				}

				if (result.size() > 0) {
					mData.addAll(result);
					mAdapter.notifyDataSetChanged();
				}
				onRefreshComplete();
				mPullRefreshStaggeredGridView.getRefreshableView()
						.onRestoreInstanceState(staggeredGridViewState);
			}
			super.onPostExecute(result);
		}
	}

	public class PostListAdapter extends BaseAdapter {

		private Activity activity = null;
		private List<PostInformation> data = null;
		private BaseApplication mApp = null;

		public PostListAdapter(Activity activity, List<PostInformation> data) {
			super();
			this.activity = activity;
			this.data = data;
			this.mApp = (BaseApplication) (activity.getApplicationContext());
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return data.get(position).getPostId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// convertView缓存
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.row_staggered, null);
				viewHolder = new ViewHolder();
				viewHolder.mlLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.card_list);
				viewHolder.ivPost = (ImageView) convertView
						.findViewById(R.id.iv_post);
				viewHolder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				viewHolder.tvTime = (TextView) convertView
						.findViewById(R.id.tv_time);
				viewHolder.tvLocation = (TextView) convertView
						.findViewById(R.id.tv_location);
				viewHolder.tvHost = (TextView) convertView
						.findViewById(R.id.tv_host);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// TODO
			final PostInformation item = data.get(position);

			int actural_height = Utils.getImgActuralHeight(mApp,
					item.getPostHeight(), item.getPostWidth());

			Log.i("PostListFragment", "actural_height:" + actural_height);
			// 设置view的大小，因为AsynctaskImage和view的自动销毁会产生位置计算的问题，所以一定要固定view的高度再加载图片
			viewHolder.ivPost.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, actural_height));

			// 根据position,将对应的内容复制设置属性
			String url = item.getPostImageUri();
			// ImageSize minImageSize = new ImageSize();
			ImageLoader.getInstance().displayImage(url, viewHolder.ivPost,
					mApp.getdisplayImageOptions());

			viewHolder.tvTime.setText(item.getPostTimestamp());
			viewHolder.tvLocation.setText(item.getPostLocation());
			viewHolder.tvHost.setText(item.getPostHost());
			viewHolder.tvTitle.setText(item.getPostTitle());

			viewHolder.mlLinearLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Intent intent = new Intent(activity,
							PostDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong("PostId", item.getPostId());
					bundle.putString("PostTimestamp", item.getPostTimestamp());
					bundle.putString("PostLocation", item.getPostLocation());
					bundle.putString("PostHost", item.getPostHost());
					bundle.putString("PostTitle", item.getPostTitle());
					bundle.putString("PostImageUri", item.getPostImageUri());

					intent.putExtras(bundle);

					//
					// news.newTag = false;
					// news.readTag = true;
					notifyDataSetChanged();
					activity.startActivity(intent);

				}
			});

			return convertView;
		}

		private class ViewHolder {
			LinearLayout mlLinearLayout;
			ImageView ivPost;
			TextView tvTitle;
			TextView tvTime;
			TextView tvLocation;
			TextView tvHost;
		}

	}

}
