package com.bitss.Digital_BIT.News;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * @author huangqian 2013-8-8 新闻、校友会共用页面，通过type来区别
 * */
public class NewsSlidingActivity extends SlidingFragmentActivity {

	private ImageView titleBack;
	private ImageView titleMore;
	private TextView titleTv;

	private SlidingMenu sm;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private boolean isAlumni; // 标识是校友会还是普通新闻
	private int defaultNewsType;
	private String defaultNewsName;

	private Fragment mLastFragment;
	private MenuFragment mMenuFragment;

	private HashMap<Integer, Fragment> newsFragmentMap = new HashMap<Integer, Fragment>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		setBehindContentView(R.layout.menu_frame);

		isAlumni = getIntent().getStringExtra("type").equals("alumni") ? true
				: false;
		defaultNewsName = "新闻快讯";
		defaultNewsType = 0;

		getSupportActionBar().hide();
		initUI();
	}

	public void initUI() {
		// navi
		titleBack = (ImageView) findViewById(R.id.iv_navi_back);
		titleBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleTv = (TextView) findViewById(R.id.tv_navi_title);
		titleTv.setText(defaultNewsName);
		titleMore = (ImageView) findViewById(R.id.iv_navi_more);
		titleMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggle();
			}
		});

		// set the Behind View
		sm = getSlidingMenu();
		sm.setMode(SlidingMenu.RIGHT);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		// init content and menu
		Fragment defaultFragment = new ContentFragment(isAlumni,
				defaultNewsType);
		newsFragmentMap.put(defaultNewsType, defaultFragment);
		fragmentTransaction.replace(R.id.content_frame, defaultFragment);
		mMenuFragment = new MenuFragment();
		fragmentTransaction.replace(R.id.menu_frame, mMenuFragment).commit();

		mLastFragment = defaultFragment;
	}

	/**
	 * 判断是不是校友会
	 * */
	public boolean isAlumni() {
		return isAlumni;
	}

	/**
	 * 获取title的名字
	 * */
	public String getTitleName() {
		return defaultNewsName;
	}

	public void switchContent(int newsType, String newsName) {
		Fragment switchFragment = newsFragmentMap.get(newsType);
		if (mLastFragment != switchFragment) {
			if (switchFragment == null) {
				switchFragment = new ContentFragment(isAlumni, newsType);
				newsFragmentMap.put(newsType, switchFragment);
			}
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.content_frame, switchFragment)
					.commit();

			defaultNewsName = newsName;
			mLastFragment = switchFragment;
			titleTv.setText(defaultNewsName);
		}
		sm.showContent();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		super.onKeyDown(keyCode, event);
		return true;
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
