package com.bitss.Digital_BIT.Location;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.bitss.Digital_BIT.BaseActivity;
import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.R;
import com.umeng.analytics.MobclickAgent;

public class IBitMapActivity extends BaseActivity implements OnClickListener {

	private static final String TITLE = "地图服务";
	private static final int LOC_FINISH = 1002; // 定位失败
	private static final int SEARCH_FINISH = 1003; // 搜索结束

	private BaseApplication mApp = null;

	private ImageView locationButton;
	private ImageView searchButton;
	private ImageView routeButton;
	private ImageView clearButton;

	private MapView mMapView = null;
	private BMapManager mBMapMan = null;
	private MapController mMapController = null;

	// ---------------------------------定位-------------------
	private LocationClient mLocClient = null;
	private LocationData locData = null;
	private MyLocationOverlay mLocationOverlay = null; // 定位图层
	public MyLocationListenner mListener = new MyLocationListenner(); // 定位监听
	private GeoPoint locationPoint = null; // 当前位置
	private Dialog dialog;

	// -------------------------------搜索----------------------
	private SearchOverlay mSearchOverlay;
	private PositionData searchData = null; // 搜索结果的位置信息
	private Drawable marker;

	// 中关村和良乡地图的切换
	private boolean isFirstComing = true; // 第一次切换到地图，点击良乡会弹出对话框
//	private TextView selectZGC;
//	private TextView selectLX;
	private RelativeLayout zgcLayout;
//	private RelativeLayout lxLayout;
//	private ImageView lxImg;
//	private PhotoViewAttacher mAttacher;

	public BMapManager getBMapManager(Context mContext) {
		if (mBMapMan == null) {
			mBMapMan = new BMapManager(mContext);
			mBMapMan.init("hshbWTD3dqn3BZrLU6UEWsL7", null);
		}
		return mBMapMan;
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOC_FINISH:
				dialog.dismiss();
				mLocClient.unRegisterLocationListener(mListener);
				mLocClient = null;
				break;
			case SEARCH_FINISH:

				// 清空之前的搜索结果
				if (mSearchOverlay != null) {
					mMapView.getOverlays().remove(mSearchOverlay);
					mSearchOverlay = null;
				}
				GeoPoint pt = searchData.getPoint();
				mSearchOverlay = new SearchOverlay(IBitMapActivity.this,
						marker, pt, searchData.getName(),
						searchData.getAddress(), mMapView);
				OverlayItem item = new OverlayItem(pt, "", "");
				item.setMarker(getResources().getDrawable(R.drawable.marker));
				mSearchOverlay.addItem(item);
				mMapView.getOverlays().add(mSearchOverlay);
				mMapView.refresh();
				mMapController.setZoom(18);
				mMapController.animateTo(pt);
				mMapController.setCenter(pt);

				break;
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			mMapView.onRestoreInstanceState(savedInstanceState);
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			mMapView.onSaveInstanceState(outState);
		}

	}

	@Override
	public void onDestroy() {
		if (mLocClient != null) {
			mLocClient.stop();
			mLocClient.unRegisterLocationListener(mListener);
		}
		if (mMapView != null) {
			mMapView.onPause();
			mMapView = null;
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}

		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		if (mMapView != null) {
			mMapView.onResume();
		}

		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void initNavi() {
		((TextView) findViewById(R.id.tv_navi_title)).setText(TITLE);
		((ImageView) findViewById(R.id.iv_navi_back))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (BaseApplication) getApplication();
		if (mBMapMan == null) {
			mBMapMan = mApp.getBMapManager(getApplicationContext());
		}
		setContentView(R.layout.activity_main);
		
		getSupportActionBar().hide();
		initNavi();
		init();
		clickToLocation();
	}

	public void init() {

		mMapView = (MapView) findViewById(R.id.map_view);
		mMapController = mMapView.getController();
		mMapController.setZoom(16);
		mMapController.enableClick(true);

		locationButton = (ImageView) findViewById(R.id.btn_location);
		locationButton.setOnClickListener(this);
		searchButton = (ImageView) findViewById(R.id.btn_search);
		searchButton.setOnClickListener(this);
		routeButton = (ImageView) findViewById(R.id.btn_route);
		routeButton.setOnClickListener(this);
		clearButton = (ImageView) findViewById(R.id.btn_clear);
		clearButton.setOnClickListener(this);

		// 中关村和良乡地图切换
//		selectZGC = (TextView) findViewById(R.id.select_zgc);
//		selectLX = (TextView) findViewById(R.id.select_lx);
		zgcLayout = (RelativeLayout) findViewById(R.id.zgc_layout);
//		lxImg = (ImageView) findViewById(R.id.lx_img);
//		mAttacher = new PhotoViewAttacher(lxImg);
//		selectZGC.setOnClickListener(this);
//		selectLX.setOnClickListener(this);
		
		// 显示中关村的地图
//		selectZGC.setBackgroundResource(R.color.location_select);
//		selectLX.setBackgroundResource(R.color.location_normal);
//		lxLayout.setVisibility(View.INVISIBLE);
		zgcLayout.setVisibility(View.VISIBLE);
	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				// 保存定位信息以及更新图层
				locationPoint = new GeoPoint(
						(int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6));

				if (mLocationOverlay == null) {
					mLocationOverlay = new MyLocationOverlay(mMapView);
					locData = new LocationData();
				}
				locData.latitude = location.getLatitude();
				locData.longitude = location.getLongitude();
				locData.accuracy = location.getRadius();
				locData.direction = location.getDerect();
				mLocationOverlay.setData(locData);

				if (!mMapView.getOverlays().contains(mLocationOverlay)) {
					mMapView.getOverlays().add(mLocationOverlay);
				}
				mLocationOverlay.enableCompass();
				mMapView.refresh();
				mMapController.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
			}
			handler.sendEmptyMessage(LOC_FINISH);
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 点击开始定位
	 * */
	public void clickToLocation() {

		dialog = ProgressDialog.show(this, "", "正在为您定位，请稍候......");

		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(mListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_location) {
			// 定位
			clickToLocation();

		} else if (v.getId() == R.id.btn_search) {
			// 把当前位置的经纬度传过去，算生活区的距离
			Intent intent = new Intent(this, SearchActivity.class);
			if (locationPoint != null) {
				intent.putExtra("latitude",
						locationPoint.getLatitudeE6() / (1E6));
				intent.putExtra("longitude",
						locationPoint.getLongitudeE6() / (1E6));
			}
			startActivityForResult(intent, 0);

		} else if (v.getId() == R.id.btn_route) {
			// 把当前位置的经纬度传过去，算生活区的距离
			Intent intent = new Intent(this, MyRouteActivity.class);
			if (locationPoint != null) {
				intent.putExtra("latitude",
						locationPoint.getLatitudeE6() / (1E6));
				intent.putExtra("longitude",
						locationPoint.getLongitudeE6() / (1E6));
			}
			startActivityForResult(intent, 1);

		} else if (v.getId() == R.id.btn_clear) {
			if (mSearchOverlay != null) {
				mSearchOverlay.removePopView();
			}
			mMapView.getOverlays().clear();
			mMapView.refresh();
		}
//			else if (v.getId() == R.id.select_zgc) {
//			selectZGC.setBackgroundResource(R.color.location_select);
//			selectLX.setBackgroundResource(R.color.location_normal);
//			lxLayout.setVisibility(View.INVISIBLE);
//			zgcLayout.setVisibility(View.VISIBLE);
//		} 
//		else if (v.getId() == R.id.select_lx) {
//			if (isFirstComing) {
//				isFirstComing = false;
//				Toast.makeText(this, "抱歉，该部分地图数据尚未完整", 0).show();
//			}
//			selectZGC.setBackgroundResource(R.color.location_normal);
//		    selectLX.setBackgroundResource(R.color.location_select);
//			lxLayout.setVisibility(View.VISIBLE);
//			zgcLayout.setVisibility(View.INVISIBLE);
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0: // 搜索返回

			Bundle b = data.getExtras();
			PositionData d = (PositionData) b.get("search_point");

			if (d != null) {
				if (marker == null) {
					marker = getResources().getDrawable(R.drawable.marker);
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight());
				}

				searchData = d;
				handler.sendEmptyMessage(SEARCH_FINISH);
			}
			break;

		case 1: // 路线导航
			Bundle routeBundle = data.getExtras();
			int route_mode = routeBundle.getInt("route_mode");

			// route_mode = -1 点击返回按钮
			if (route_mode != -1) {
				PositionData startData = (PositionData) routeBundle
						.getSerializable("start_point");
				PositionData endData = (PositionData) routeBundle
						.getSerializable("end_point");

				GeoPoint start_point, end_point;

				if (startData == null) { // 空表示当前位置
					if (locationPoint == null) {
						Toast.makeText(IBitMapActivity.this, "抱歉，请您先定位",
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						start_point = locationPoint;
					}
				} else {
					start_point = startData.getPoint();
				}

				if (endData == null) { // 空表示当前位置
					if (locationPoint == null) {
						Toast.makeText(IBitMapActivity.this, "抱歉，请您先定位",
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						end_point = locationPoint;
					}
				} else {
					end_point = endData.getPoint();
				}

				MyRouteOverlay routeOverlay = new MyRouteOverlay(mMapView,
						mBMapMan, start_point, end_point, route_mode, this);
				routeOverlay.routeMode();
				mMapController.animateTo(start_point);
				mMapController.setCenter(start_point);
				mMapController.setZoom(18);
			}

			break;
		default:
			break;
		}
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

}