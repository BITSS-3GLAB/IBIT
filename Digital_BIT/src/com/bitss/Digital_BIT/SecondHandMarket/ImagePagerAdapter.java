package com.bitss.Digital_BIT.SecondHandMarket;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bitss.Digital_BIT.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ImagePagerAdapter extends PagerAdapter {

	private ArrayList<String> images;
	private LayoutInflater inflater;
	private Context context;
	private DisplayImageOptions displayImageOptions;

	ImagePagerAdapter(ArrayList<String> images, Context context) {
		this.images = images;
		this.context = context;
		inflater = LayoutInflater.from(context);

		displayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true).build();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, final int position) {
		View imageLayout = inflater.inflate(R.layout.item_pager_image, view,
				false);
		assert imageLayout != null;
		ImageView imageView = (ImageView) imageLayout
				.findViewById(R.id.secondhandimage);
		// imageView.setScaleType(ScaleType.FIT_XY);
		final ProgressBar spinner = (ProgressBar) imageLayout
				.findViewById(R.id.secondhandloading);
		// String temp =
		// "http://pic1.nipic.com/2008-09-08/200898163242920_2.jpg";
		ImageLoader.getInstance().displayImage(images.get(position), imageView,
				displayImageOptions, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						spinner.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// String message = null;
						// switch (failReason.getType()) {
						// case IO_ERROR:
						// message = "Input/Output error";
						// break;
						// case DECODING_ERROR:
						// message = "Image can't be decoded";
						// break;
						// case NETWORK_DENIED:
						// message = "Downloads are denied";
						// break;
						// case OUT_OF_MEMORY:
						// message = "Out Of Memory error";
						// break;
						// case UNKNOWN:
						// message = "Unknown error";
						// break;
						// }
						// Toast.makeText(context, message, Toast.LENGTH_SHORT)
						// .show();

						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						spinner.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				});
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						SecondHandMarketDetailPhotoActivity.class);
				intent.putExtra("url", images.get(position));
				context.startActivity(intent);
			}
		});

		view.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}
}
