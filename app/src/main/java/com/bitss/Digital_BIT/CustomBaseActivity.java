package com.bitss.Digital_BIT;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;


public class CustomBaseActivity extends BaseActivity {

	public TextView mTvNaviTitle;
	public TextView mTvRight ;
	public ImageView mIvNaviBack;
	public ImageView mIvNaviShare;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pageName = "CustomBaseActivity";
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		mActionBar.setCustomView(R.layout.navi_second_hand);

		mTvNaviTitle = (TextView) findViewById(R.id.tv_navi_title);

		mIvNaviBack = (ImageView) findViewById(R.id.iv_navi_back);

		mIvNaviShare = (ImageView) findViewById(R.id.iv_navi_share);
		
		mTvRight = (TextView)findViewById(R.id.tv_choose);


		mIvNaviBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backAction();
				
			}
		});
	}

	public void backAction() {
		finish();
//		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			backAction();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
