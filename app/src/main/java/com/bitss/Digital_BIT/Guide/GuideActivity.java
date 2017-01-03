package com.bitss.Digital_BIT.Guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bitss.Digital_BIT.BaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.More.AboutUsActivity;
import com.bitss.Digital_BIT.More.ResponseActivity;
import com.bitss.Digital_BIT.Personal.LoginActivity;
import com.bitss.Digital_BIT.Personal.PersonalInfoActivity;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.SetupDBUtil;
import com.umeng.update.UmengUpdateAgent;

public class GuideActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "GuideActivity";

	private LinearLayout moreLayout; // 意见反馈、关于
	private ImageView bgImg;
	private ImageView feedBackImg;
	private ImageView aboutUsImg;
	private ImageView personalImg ;

	private ViewPager mViewPager;
	private GuideAdapter mGuideAdapter;

	private boolean canScrollPager = false; // 数据库如果插入完毕，可以向左滑动
	SharedPreferences settings;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UmengUpdateAgent.update(this);
		setContentView(R.layout.activity_guide);

		getSupportActionBar().hide();

		// 初始化数据库
		new SetupDBUtil(this);
		settings = mApp.getPreferences();
		moreLayout = (LinearLayout) findViewById(R.id.more);
		bgImg = (ImageView) findViewById(R.id.img_bg);
		feedBackImg = (ImageView) findViewById(R.id.feed_back);
		aboutUsImg = (ImageView) findViewById(R.id.about_us);
		personalImg = (ImageView)findViewById(R.id.iv_personal);
		personalImg.setOnClickListener(this);
		bgImg.setVisibility(View.INVISIBLE);
		moreLayout.setVisibility(View.INVISIBLE);
		feedBackImg.setOnClickListener(this);
		aboutUsImg.setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		mGuideAdapter = new GuideAdapter(this);
		mViewPager.setAdapter(mGuideAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					moreLayout.setVisibility(View.INVISIBLE);
				} else {
					bgImg.setVisibility(View.VISIBLE);
					moreLayout.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 在数据库没完成之前，不允许滑动
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (canScrollPager) {
			return super.dispatchTouchEvent(ev);
		} else {
			return true;
		}

	}

	public void setScrollStatus(boolean value) {
		this.canScrollPager = value;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.feed_back:
			startActivity(new Intent(this, ResponseActivity.class));
			break;

		case R.id.about_us:
			startActivity(new Intent(this, AboutUsActivity.class));
			break;
			
		case R.id.iv_personal:
			if(settings.getString(Constants.KEY_EMAIL, "").equals(""))
				startActivity(new Intent(this, LoginActivity.class));
			else {
				startActivity(new Intent(this, PersonalInfoActivity.class));
			}
			break;
		}

	}

}
