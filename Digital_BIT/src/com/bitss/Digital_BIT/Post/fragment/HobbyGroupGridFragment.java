package com.bitss.Digital_BIT.Post.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.bitss.Digital_BIT.BaseActivity;
import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Post.activity.HobbyGroupPostListActivity;
import com.bitss.Digital_BIT.Post.fragment.PostListFragment.PostListAdapter;
import com.bitss.Digital_BIT.Post.model.HobbyGroupInformation;
import com.bitss.Digital_BIT.Post.model.PostInformation;
import com.bitss.Digital_BIT.Post.provider.DataProvider;
import com.bitss.Digital_BIT.Tools.HttpErrorToast;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Logger;
import com.bitss.Digital_BIT.Util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.origamilabs.library.views.StaggeredGridView;

public class HobbyGroupGridFragment extends SherlockFragment {

	private PullToRefreshGridView mPullToRefreshGridView;
	private GridView mGridView;
	private HobbyGroupGridAdapter mAdapter;
	private List<HobbyGroupInformation> mData = new ArrayList<HobbyGroupInformation>();
	private int type;

	private long mMinPostId = 0;
	private long mMaxPostId = 0;

	private long mOldMaxPostId = 0;

	private RefreshTask mRefreshTask = null;

	private final HobbyGroupInformation tempHobbyGroupInformation = null;

	private BaseApplication mApp = null;
	// Save ListView state
	private Parcelable staggeredGridViewState = null;

	private DataProvider mProvider = null;

	// TODO change to get Instance
	public static HobbyGroupGridFragment newInstance() {
		HobbyGroupGridFragment f = new HobbyGroupGridFragment();
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

		// mData = new LinkedList<PostInformation>();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(Constants.KEY_POST_TYPE, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ptr_grid_view,
				container, false);
		mPullToRefreshGridView = (PullToRefreshGridView) view
				.findViewById(R.id.pull_refresh_gridview);

		mGridView = mPullToRefreshGridView.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.

		mPullToRefreshGridView
				.setOnRefreshListener(new OnRefreshListener<GridView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<GridView> refreshView) {
						doRefresh();
					}

				});

		mGridView = mPullToRefreshGridView.getRefreshableView();

		TextView tv = new TextView(mApp);
		tv.setGravity(Gravity.CENTER);
		tv.setText("Empty View, Pull Down/Up to Add Items");
		mPullToRefreshGridView.setEmptyView(tv);

		mAdapter = new HobbyGroupGridAdapter(getActivity(), mData);
		mGridView.setAdapter(mAdapter);

		if (!Utils.isNetworkAvailable(mApp)) {
			HttpErrorToast.Show(mApp);
		}

		// 生成界面的时候显示历史数据
		if (mData.size() == 0) {
			// 进入页面，先显示历史数据
			// -1或者0代表获取所有社团的信息(为避免以后社团变多容易升级)
			List<HobbyGroupInformation> list = getInitData();
			if (list.size() > 0) {
				mData.clear();
				mData.addAll(list);
				mAdapter.notifyDataSetChanged();
			}

			if (mData.size() == 0) {
				// 进入页面，默认不刷新，如果数据为空，则刷新
				doRefresh();
			}
		}

		return view;
	}

	public List<HobbyGroupInformation> getInitData() {
		List<HobbyGroupInformation> list = mProvider.getHobbyGroupInitData();
		if (list.size() > 0) {
			mMinPostId = list.get(list.size() - 1).getHobbyGroupId();
			mOldMaxPostId = list.get(0).getHobbyGroupId();

		}
		return list;
	}

	public List<HobbyGroupInformation> getRefreshData(int count)
			throws IOException, JSONException {
		List<HobbyGroupInformation> list = mProvider
				.getHobbyGroupInformationList(0, count);
		if (list.size() > 0) {
			mMinPostId = list.get(list.size() - 1).getHobbyGroupId();
			mMaxPostId = list.get(0).getHobbyGroupId();
		}

		return list;
	}

	public void doRefresh() {
		if (mRefreshTask != null) {
			mRefreshTask.cancel(true);
		}
		mRefreshTask = new RefreshTask();
		mRefreshTask.execute();
	}

	public class RefreshTask extends
			AsyncTask<Void, Void, List<HobbyGroupInformation>> {
		@Override
		protected List<HobbyGroupInformation> doInBackground(Void... params) {
			try {
				return getRefreshData(Constants.COUNT_PER_PAGE);
			} catch (Exception e) {
				((BaseActivity) getActivity()).handleException(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<HobbyGroupInformation> result) {
			if (result != null) {
				if (result.size() < Constants.COUNT_PER_PAGE) {
					// 没有下一页了
					mPullToRefreshGridView.setMode(Mode.PULL_FROM_START);
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
						if (result.get(i).getHobbyGroupId() > mOldMaxPostId) {
							count++;
						} else {
							break;
						}
					}
					Utils.showToast(getActivity(),
							String.format("社团目录更新了", count));
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
		mPullToRefreshGridView.onRefreshComplete();
	}

	public class HobbyGroupGridAdapter extends BaseAdapter {

		private Activity activity = null;
		private List<HobbyGroupInformation> data = null;
		private BaseApplication mApp = null;

		public HobbyGroupGridAdapter(Activity activity,
				List<HobbyGroupInformation> data) {
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
			return data.get(position).getHobbyGroupId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// convertView缓存
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(activity).inflate(
						R.layout.gridview_hobby_group, null);
				viewHolder = new ViewHolder();
				viewHolder.mlLinearLayout = (LinearLayout) convertView
						.findViewById(R.id.card_list);
				viewHolder.ivHobbyGroup = (ImageView) convertView
						.findViewById(R.id.iv_hobby_group);
				viewHolder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				viewHolder.tvPostNum = (TextView) convertView
						.findViewById(R.id.tv_post_num);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			// TODO
			final HobbyGroupInformation item = data.get(position);

			// 根据position,将对应的内容复制设置属性
			String url = item.getHobbyGroupImageUri();
			// ImageSize minImageSize = new ImageSize();
			ImageLoader.getInstance().displayImage(url,
					viewHolder.ivHobbyGroup, mApp.getdisplayImageOptions());
			viewHolder.tvTitle.setText(item.getHobbyGroupName());
			viewHolder.tvPostNum.setText(Integer.toString(item
					.getHobbyGroupPostCount()));

			// 间隔的颜色背景
			int type = position % 4;
			if (0 == type || 3 == type) {
				viewHolder.mlLinearLayout.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.card_style));
			} else {
				viewHolder.mlLinearLayout.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.card_style_green));
			}

			viewHolder.mlLinearLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(activity,
							HobbyGroupPostListActivity.class);

					Bundle bundle = new Bundle();
					bundle.putString("CorName", item.getHobbyGroupName());
					bundle.putInt("CorID", item.getHobbyGroupId());
					intent.putExtras(bundle);
					activity.startActivity(intent);

				}
			});

			return convertView;
		}

		private class ViewHolder {
			LinearLayout mlLinearLayout;
			ImageView ivHobbyGroup;
			TextView tvTitle;
			TextView tvPostNum;
		}

	}

}
