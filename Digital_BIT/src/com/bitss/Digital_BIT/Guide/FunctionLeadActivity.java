package com.bitss.Digital_BIT.Guide;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bitss.Digital_BIT.R;

/**
 * 功能引导页
 * 
 * 第一次启动时加载
 * 
 * @author hq 2014-2-18
 * 
 * */
public class FunctionLeadActivity extends Activity {

	private ViewPager mLeadPager;
	private LeadAdapter mLeadAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_function_lead);

		mLeadPager = (ViewPager) findViewById(R.id.function_lead_viewpager);
		mLeadAdapter = new LeadAdapter(this);
		mLeadPager.setAdapter(mLeadAdapter);
	}

	public class LeadAdapter extends PagerAdapter {

		private Context context;
		private HashMap<Integer, LeadPage> mHashMap;

		public LeadAdapter(Context context) {
			this.context = context;
			mHashMap = new HashMap<Integer, LeadPage>();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			return;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			LeadPage page;
			if (mHashMap.containsKey(position)) {
				page = mHashMap.get(position);
			} else {
				page = new LeadPage(context, position);
				mHashMap.put(position, page);
				((ViewPager) container).addView(page);
			}

			return page;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}

	/**
	 * 构造每个页面
	 * */
	public class LeadPage extends LinearLayout {

		private Context context;

		public LeadPage(Context context, int position) {
			super(context);
			this.context = context;
			setupView(position);
		}

		public void setupView(int position) {
			View view = LayoutInflater.from(context).inflate(
					R.layout.item_function_lead, this);
			ImageView leadBack = (ImageView) view
					.findViewById(R.id.img_lead_bg);
			ImageView leadStart = (ImageView) view
					.findViewById(R.id.img_lead_start);
			leadStart.setVisibility(View.INVISIBLE);
			leadStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 修改配置文件
					PackageInfo packInfo = null;
					try {
						packInfo = getPackageManager().getPackageInfo(
								getPackageName(), 0);
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					SharedPreferences preferences = context.getSharedPreferences(
							"hasLeadPage",
							context.getApplicationContext().MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("lead_page_version", packInfo.versionName);
					editor.commit();

					Intent intent = new Intent(FunctionLeadActivity.this,
							GuideActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					FunctionLeadActivity.this.finish();
				}
			});

			switch (position) {
			case 0:
				leadBack.setBackgroundResource(R.drawable.bg_lead01);
				break;
			case 1:
				leadBack.setBackgroundResource(R.drawable.bg_lead02);
				break;
			case 2:
				leadStart.setVisibility(View.VISIBLE);
				leadBack.setBackgroundResource(R.drawable.bg_lead03);
				break;

			}
		}

	}

}
