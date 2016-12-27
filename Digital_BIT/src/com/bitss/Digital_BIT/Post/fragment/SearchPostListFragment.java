package com.bitss.Digital_BIT.Post.fragment;

import java.util.List;

import com.bitss.Digital_BIT.Post.model.PostInformation;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchPostListFragment extends PostListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO
		Bundle bundle = getArguments();
		corId = bundle.getInt("CorID");
		Log.i("PostListFragment", "corId" + corId);

		// mData = new LinkedList<PostInformation>();
	}

	// TODO change to get Instance
	public static SearchPostListFragment newInstance() {
		SearchPostListFragment f = new SearchPostListFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public List<PostInformation> getInitData(int count) {
		List<PostInformation> list = mProvider.getPostInitData(count,corId);
		if (list.size() > 0) {
			mMinPostId = list.get(list.size() - 1).getPostId();
			mOldMaxPostId = list.get(0).getPostId();

		}
		Log.i("PostListFragment", "getInitData list.size():" + list.size());
		Log.i("PostListFragment", "getInitData mMinPostId:" + mMinPostId);
		Log.i("PostListFragment", "getInitData mOldMaxPostId:" + mOldMaxPostId);
		return list;
	}
}
