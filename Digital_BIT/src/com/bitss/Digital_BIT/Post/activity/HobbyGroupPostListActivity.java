package com.bitss.Digital_BIT.Post.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Post.fragment.PostListFragment;
import com.bitss.Digital_BIT.Post.fragment.SearchPostListFragment;

public class HobbyGroupPostListActivity extends CustomBaseActivity {
	private String activityTitle = "";
	private int hobbyGroupId = 0;
	private Bundle bundle;
	private FragmentManager fragmentManager = null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hobby_hroup_post_list);

		Intent intent = getIntent();
		bundle = intent.getExtras();
		activityTitle = bundle.getString("CorName");
		hobbyGroupId = bundle.getInt("CorID", 0);

		// TODO 获取intent 设置title
		mTvNaviTitle.setText(activityTitle);

		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}

			fragmentManager = getSupportFragmentManager();

			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			SearchPostListFragment searchPostListFragment = (SearchPostListFragment) SearchPostListFragment
					.newInstance();
			searchPostListFragment.setArguments(bundle);

			fragmentTransaction.add(R.id.fragment_container,
					searchPostListFragment);
			fragmentTransaction.commit();
		}

	}
	

}
