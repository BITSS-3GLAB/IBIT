package com.bitss.Digital_BIT.LostFound;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class DetailPhotoActivity extends CustomBaseActivity {

	private ImageView detailPhoto;
	private DisplayImageOptions displayImageOptions;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_photo);

		mTvNaviTitle.setText("图片详情");
		initParam();

		String photoUrl = getIntent().getStringExtra("photo_url");
		detailPhoto = (ImageView) findViewById(R.id.detail_photo);
		ImageLoader.getInstance().displayImage(photoUrl, detailPhoto,
				displayImageOptions, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1lu,
							FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						new PhotoViewAttacher(detailPhoto);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});

	}

	public void initParam() {
		displayImageOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.uil_ic_stub)
				.showImageForEmptyUri(R.drawable.uil_ic_empty)
				.showImageOnFail(R.drawable.uil_ic_error).cacheInMemory(true) // default
				.cacheOnDisc(true) // default
				.build();
	}
}
