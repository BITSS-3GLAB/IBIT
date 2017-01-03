package com.bitss.Digital_BIT.Admission;

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
 * 学院概览
 * */
public class CollegeSlidingActivity extends SlidingFragmentActivity {

	private static final String DEFAULT_COLLEGE = "徐特立学院";
	private static final String DEFAULT_HTML_FILE = "xuteli";

	private ImageView titleBack;
	private ImageView titleMore;
	private TextView titleTv;

	private SlidingMenu sm;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private CollegeContentFragment contentFragment; // 内容部分
	private CollegeMenuFragment menuFragment; // 菜单部分

	private Fragment mLastFragment;
	private HashMap<String, Fragment> fragmentMap = new HashMap<String, Fragment>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		setBehindContentView(R.layout.menu_frame);

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
		titleTv.setText(DEFAULT_COLLEGE);

		titleMore = (ImageView) findViewById(R.id.iv_navi_more);
		titleMore.setImageDrawable(getResources().getDrawable(R.drawable.bg_navi_more_style));

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

		// content
		contentFragment = new CollegeContentFragment(DEFAULT_HTML_FILE);
		fragmentTransaction.replace(R.id.content_frame, contentFragment);
		fragmentMap.put(DEFAULT_HTML_FILE, contentFragment);
		mLastFragment = contentFragment;

		// menu
		menuFragment = new CollegeMenuFragment();
		fragmentTransaction.replace(R.id.menu_frame, menuFragment).commit();
	}

	// 切换内容
	public void switchContent(String name, String file) {
		titleTv.setText(name);

		Fragment switchFragment = fragmentMap.get(file);
		if (mLastFragment != switchFragment) {
			if (switchFragment == null) {
				switchFragment = new CollegeContentFragment(file);
				fragmentMap.put(file, switchFragment);
			}
			fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.content_frame, switchFragment).commit();
			mLastFragment = switchFragment;
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
