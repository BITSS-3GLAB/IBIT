package com.bitss.Digital_BIT.BitKnow;

import com.bitss.Digital_BIT.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.mime.content.ContentBody;

import com.bitss.Digital_BIT.LostFound.DetailPhotoActivity;
import com.bitss.Digital_BIT.Util.Constants;
import com.makeramen.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class BitKnowMainAdapter extends BaseAdapter {

	private Context context;
	private List<BitKnowMainData> mData;
	private DisplayImageOptions displayImageOptions;
	
	public BitKnowMainAdapter(Context context,List<BitKnowMainData> mData)
	{
		this.context = context;
		this.mData = mData;
		
		displayImageOptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.uil_ic_stub)
		.showImageForEmptyUri(R.drawable.uil_ic_empty)
		.showImageOnFail(R.drawable.uil_ic_error)
		.resetViewBeforeLoading(false)
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
		.displayer(new FadeInBitmapDisplayer(200))
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
	}
	
	@Override
	public int getCount() {
		if(mData == null)
			return 0;
		else return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		List<String> mImages = new ArrayList<String>();
		//if(convertView == null)
		//{
			holder = new ViewHolder();
			LayoutInflater flater = LayoutInflater.from(context);
			convertView = flater.inflate(R.layout.bitknow_main_adapter_item, null);
			
			holder.photo = (RoundedImageView)convertView.
					findViewById(R.id.photo);
			holder.name = (TextView)convertView.
					findViewById(R.id.name);
			holder.time = (TextView)convertView.
					findViewById(R.id.time);
			
			holder.image1 = (ImageView)convertView.
					findViewById(R.id.image1);
			holder.image2 = (ImageView)convertView.
					findViewById(R.id.image2);
			holder.image3 = (ImageView)convertView.
					findViewById(R.id.image3);
			
			holder.content = (TextView)convertView.
					findViewById(R.id.content);
			
			holder.label1 = (TextView)convertView.
					findViewById(R.id.label1);
			holder.label2 = (TextView)convertView.
					findViewById(R.id.label2);
			holder.label3 = (TextView)convertView.
					findViewById(R.id.label3);
			holder.answerNumber = (TextView)convertView.
					findViewById(R.id.answerNumber);
			
			
			convertView.setTag(holder);
		//}else {
		//	holder = (ViewHolder)convertView.getTag();
		//}
				
			String photoUrl = Constants.PHTOT_CLOUDSERVER_STRING + (String)mData.get(position).getPhotoUrl();
			//ImageLoader.getInstance().displayImage(photoUrl, holder.photo);
			ImageLoader.getInstance().displayImage(photoUrl, holder.photo, displayImageOptions);
			holder.name.setText((String)mData.get(position).getName());
			holder.time.setText((String)mData.get(position).getTime());
			
			final String url1 = Constants.PHTOT_CLOUDSERVER_STRING + (String)mData.get(position).getUrl1();
			//ImageLoader.getInstance().displayImage(url1, holder.image1);
			final String url2 = Constants.PHTOT_CLOUDSERVER_STRING  + (String)mData.get(position).getUrl2();
			//ImageLoader.getInstance().displayImage(url2, holder.image2);
			final String url3 = Constants.PHTOT_CLOUDSERVER_STRING + (String)mData.get(position).getUrl3();
			//ImageLoader.getInstance().displayImage(url3, holder.image3);

			
			final BitKnowMainData data = mData.get(position);
			holder.image1.setVisibility(View.GONE);
			holder.image2.setVisibility(View.GONE);
			holder.image3.setVisibility(View.GONE);
			int image_num = 0;
			if(!data.getUrl1().equals("无")) image_num ++;
			if(!data.getUrl2().equals("无")) image_num ++;
			if(!data.getUrl3().equals("无")) image_num ++;
			
			switch (image_num) {
			case 3:
				holder.image3.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(url3, holder.image3, displayImageOptions);
				holder.image3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context,BitKnowDetailPhotoActivity.class);
						intent.putExtra("url", url3);
						context.startActivity(intent);
					}
				});
			case 2:
				holder.image2.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(url2, holder.image2, displayImageOptions);
				holder.image2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context,BitKnowDetailPhotoActivity.class);
						intent.putExtra("url", url2);
						context.startActivity(intent);
					}
				});
			case 1:
				holder.image1.setVisibility(View.VISIBLE);
				ImageLoader.getInstance().displayImage(url1, holder.image1,displayImageOptions);
				holder.image1.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,BitKnowDetailPhotoActivity.class);
						intent.putExtra("url", url1);
						context.startActivity(intent);
					}
				});
				break;
			default:
				break;
			}
			
			
			holder.content.setText((String)mData.get(position).getContent());
			
			String[] labels = ((String)mData.get(position).getLabels()).split("\\|", 4);
			
			int length = labels.length;
			
			switch (length) {
			case 0:
				holder.label1.setVisibility(View.GONE);
				holder.label2.setVisibility(View.GONE);
				holder.label3.setVisibility(View.GONE);
				break;
			case 1:
				holder.label1.setText(labels[0]);
				holder.label2.setVisibility(View.GONE);
				holder.label3.setVisibility(View.GONE);
				break;
			case 2:
				holder.label1.setText(labels[0]);
				holder.label2.setText(labels[1]);
				holder.label3.setVisibility(View.GONE);
				break;
			default:
				holder.label1.setText(labels[0]);
				holder.label2.setText(labels[1]);
				holder.label3.setText(labels[2]);
				break;
			}
				
			holder.answerNumber.setText(mData.get(position).getNum() + "人回答");
		return convertView;
	}

	private class ViewHolder
	{
		RoundedImageView photo;
		TextView name;
		TextView time;
		
		ImageView image1;
		ImageView image2;
		ImageView image3;
		
		TextView content;
		
		TextView label1;
		TextView label2;
		TextView label3;
		TextView answerNumber;
		TextView goodNumber;
	}
	
}
