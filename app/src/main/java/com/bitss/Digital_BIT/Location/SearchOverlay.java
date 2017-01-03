package com.bitss.Digital_BIT.Location;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.bitss.Digital_BIT.R;

public class SearchOverlay extends ItemizedOverlay<OverlayItem> {

	private MapView mMapView;
	private Context mContext;

	private GeoPoint searchPoint;
	private View mPopView;
	private TextView mPopName, mPopAddress;

	public SearchOverlay(Context mContext, Drawable marker,
			GeoPoint searchPoint, String name, String address, MapView mMapView) {

		super(marker, mMapView);

		this.mContext = mContext;
		this.mMapView = mMapView;
		this.searchPoint = searchPoint;
		init(name, address);
	}

	public void init(String name, String address) {

		mPopView = LayoutInflater.from(mContext)
				.inflate(R.layout.popview, null);
		mPopName = (TextView) mPopView.findViewById(R.id.tv_pop_name);
		mPopAddress = (TextView) mPopView.findViewById(R.id.tv_pop_address);
		mPopName.setText(name);
		mPopAddress.setText("位置:" + address);

		this.mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.TOP_LEFT));
		mPopView.setVisibility(View.GONE);
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	// 点击marke弹出的pop
	protected boolean onTap(int i) {

		mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				searchPoint, MapView.LayoutParams.BOTTOM_CENTER));
		mPopView.setVisibility(View.VISIBLE);
		return true;
	}

	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		mPopView.setVisibility(View.GONE);
		return super.onTap(arg0, arg1);
	}

	// 清空弹出的pop
	public void removePopView() {
		mPopView.setVisibility(View.GONE);
	}

}
