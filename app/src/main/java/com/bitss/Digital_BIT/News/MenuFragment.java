package com.bitss.Digital_BIT.News;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;

public class MenuFragment extends Fragment implements OnClickListener,
		OnItemClickListener {

	private static final int ORDER_CODE = 1001;

	private boolean isAlumni; // 是否是校友会
	private Context context;
	private ImageView mOrderButton;

	private ListView mListView;
	private MenuAdapter adapter;
	private List<NewsTypeModel> newsTypeData = new ArrayList<NewsTypeModel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.context = getActivity();
		isAlumni = ((NewsSlidingActivity) context).isAlumni();

		View view = inflater.inflate(R.layout.layout_menu_list, null);
		mListView = (ListView) view.findViewById(R.id.news_type_listview);
		mListView.setOnItemClickListener(this);

		mOrderButton = (ImageView) view.findViewById(R.id.top_news_order);
		if (isAlumni) {
			mOrderButton.setVisibility(View.INVISIBLE);
		} else {
			mOrderButton.setVisibility(View.VISIBLE);
			mOrderButton.setOnClickListener(this);
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		loadMenuData();
	}

	public void loadMenuData() {
		newsTypeData.clear();
		newsTypeData.addAll(new MenuDataBuilder(context).getMenuData(isAlumni));

		if (adapter == null) {
			adapter = new MenuAdapter();
			mListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}

	}

	private void switchFragment(int newsType, String newsName) {
		if (context == null)
			return;

		if (context instanceof NewsSlidingActivity) {
			((NewsSlidingActivity) context).switchContent(newsType, newsName);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.top_news_order) {
			startActivity(new Intent(context, DragListActivity.class));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int position = (int) arg3;
		int newsType = Integer.valueOf(newsTypeData.get(position).newsType);
		String newsName = newsTypeData.get(position).newsName;
		switchFragment(newsType, newsName);

	}

	public class MenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return newsTypeData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_menu, parent, false);
			TextView name = (TextView) convertView.findViewById(R.id.tv_menu);
			name.setText(newsTypeData.get(position).newsName);
			return convertView;
		}

	}

}
