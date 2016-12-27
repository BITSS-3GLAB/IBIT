package com.bitss.Digital_BIT.BitKnow;

import uk.co.senab.photoview.PhotoViewAttacher;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class BitKnowDetailPhotoActivity extends CustomBaseActivity {
	private ImageView imageView;
	private DisplayImageOptions displayImageOptions;

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitknow_detail_photo);
		mTvNaviTitle.setText("图片详情");
		initParam();
		
		String url = getIntent().getStringExtra("url");
		imageView = (ImageView)findViewById(R.id.bitknow_detail_photo);
		ImageLoader.getInstance().displayImage(url, imageView, displayImageOptions, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				// TODO Auto-generated method stub
				new PhotoViewAttacher(imageView);
				
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void initParam()
	{
		displayImageOptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.uil_ic_stub)
		.showImageForEmptyUri(R.drawable.uil_ic_empty)
		.showImageOnFail(R.drawable.uil_ic_error).cacheInMemory(true) // default
		.cacheOnDisc(true) // default
		.build();
	}
}
