package com.bitss.Digital_BIT.Guide;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.BitKnow.BitKnowMainActivity;
//import com.bitss.Digital_BIT.BitKnow.QuestionDescribeActivity;
import com.bitss.Digital_BIT.Bus.BusActivity;
import com.bitss.Digital_BIT.Location.IBitMapActivity;
import com.bitss.Digital_BIT.LostFound.LostFoundActivity;
import com.bitss.Digital_BIT.Meeting.BITMeetingActivity;
import com.bitss.Digital_BIT.News.NewsSlidingActivity;
import com.bitss.Digital_BIT.Phone.BITphonenumActivity;
import com.bitss.Digital_BIT.SecondHandMarket.SecondHandActivity;
import com.bitss.Digital_BIT.Util.GuideAnimationUtil;

public class GuideItemView extends LinearLayout {

	private Context context;
	private int position;

	public GuideItemView(Context context, int position) {
		super(context);
		this.context = context;
		this.position = position;
		setupViews();
	}

	public GuideItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setupViews();
	}

	private void setupViews() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = null;

		if (position == 0) {
			// 启动页面
			view = inflater.inflate(R.layout.layout_guide_image, this);
			ImageView guideImg = (ImageView) view.findViewById(R.id.guide_img);
			TextView hintSlidTv = (TextView) view
					.findViewById(R.id.tv_hint_slid);

			// 左滑提示
			Animation ani = new AlphaAnimation(0f, 1f);
			ani.setDuration(2000);
			ani.setRepeatMode(Animation.REVERSE);
			ani.setRepeatCount(Animation.INFINITE);
			hintSlidTv.startAnimation(ani);

			// 给图片设置动画
			new GuideAnimationUtil(context, guideImg);

		} else if (position == 1) {
			// 显示6个tab
			view = inflater.inflate(R.layout.layout_guide_tab, this);
			setView(view);
		} else if (position == 2) {
			// 二手市场、失物招领
			view = inflater.inflate(R.layout.layout_guide_tab, this);

			View upLayout = view.findViewById(R.id.up_tab);
			upLayout.setVisibility(View.VISIBLE);

			ImageView upLeft = (ImageView) upLayout.findViewById(R.id.tab_left);
			ImageView upRight = (ImageView) upLayout
					.findViewById(R.id.tab_right);
//			upLeft.setImageResource(R.drawable.secondhand_icon);
			upLeft.setImageResource(R.drawable.icon_phone);

			upRight.setImageResource(R.drawable.icon_lf);
			// 二手市场
			upLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Intent intent = new Intent(context,
					// SecondHandActivity.class);
					// intent.putExtra("type", "alumni");
					// context.startActivity(intent);
					context.startActivity(new Intent(context,
							BITphonenumActivity.class));
				}
			});

			upRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					context.startActivity(new Intent(context,
							LostFoundActivity.class));
				}
			});
			upRight.setVisibility(View.INVISIBLE);

			// 北理知道
			// View midLayout = view.findViewById(R.id.middle_tab);
			// midLayout.setVisibility(View.VISIBLE);
			// ImageView midLeft = (ImageView) midLayout
			// .findViewById(R.id.tab_left);
			// midLeft.setImageResource(R.drawable.icon_bitknow);
			// ImageView midRight = (ImageView) midLayout
			// .findViewById(R.id.tab_right);
			// midRight.setVisibility(View.INVISIBLE);
			//
			// midLeft.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// context.startActivity(new Intent(context,
			// BitKnowMainActivity.class));
			// }
			// });

		}
	}

	// 设置tab中间页
	public void setView(View view) {

		View upTabLayout = view.findViewById(R.id.up_tab);
		View midTabLayout = view.findViewById(R.id.middle_tab);
		View downTabLayout = view.findViewById(R.id.down_tab);

		upTabLayout.setVisibility(View.VISIBLE);
		midTabLayout.setVisibility(View.VISIBLE);
		downTabLayout.setVisibility(View.VISIBLE);

		ImageView upLeft = (ImageView) upTabLayout.findViewById(R.id.tab_left);
		ImageView upRight = (ImageView) upTabLayout
				.findViewById(R.id.tab_right);
		ImageView midLeft = (ImageView) midTabLayout
				.findViewById(R.id.tab_left);
		ImageView midRight = (ImageView) midTabLayout
				.findViewById(R.id.tab_right);
		ImageView downLeft = (ImageView) downTabLayout
				.findViewById(R.id.tab_left);
		ImageView downRight = (ImageView) downTabLayout
				.findViewById(R.id.tab_right);

		upLeft.setImageResource(R.drawable.icon_news);
		upRight.setImageResource(R.drawable.icon_bus);
		// upRight.setImageResource(R.drawable.icon_meeting);

		// midLeft.setImageResource(R.drawable.icon_admission);
		midLeft.setImageResource(R.drawable.icon_bitknow);
		midRight.setImageResource(R.drawable.secondhand_icon);
		// midRight.setImageResource(R.drawable.icon_bus);

		// downLeft.setImageResource(R.drawable.icon_phone);
		downLeft.setImageResource(R.drawable.icon_lf);
		downRight.setImageResource(R.drawable.icon_location);

		upLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewsSlidingActivity.class);
				intent.putExtra("type", "news");
				context.startActivity(intent);
			}
		});
		upRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// context.startActivity(new Intent(context,
				// BITMeetingActivity.class));
				context.startActivity(new Intent(context, BusActivity.class));
			}
		});
		midLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// context.startActivity(new Intent(context,
				// AdmissionMainActivity.class));
				context.startActivity(new Intent(context,
						BitKnowMainActivity.class));

			}
		});
		midRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// context.startActivity(new Intent(context,
				// BusActivity.class));
				Intent intent = new Intent(context, SecondHandActivity.class);
				intent.putExtra("type", "alumni");
				context.startActivity(intent);
			}
		});
		downLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// context.startActivity(new Intent(context,
				// BITphonenumActivity.class));
				context.startActivity(new Intent(context,
						LostFoundActivity.class));
			}
		});

		downRight.setOnClickListener(new OnClickListener() {

			/**
			 * 测试使用
			 */

			@Override
			public void onClick(View v) {
				context.startActivity(new Intent(context, IBitMapActivity.class));
				// context.startActivity(new
				// Intent(context,QuestionDescribeActivity.class));
			}
		});

	}

	// dp变为px
	public int dip2px(int value) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (value * scale + 0.5f);
	}

}
