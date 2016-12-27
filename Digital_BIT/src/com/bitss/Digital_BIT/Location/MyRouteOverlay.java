package com.bitss.Digital_BIT.Location;

import android.app.Activity;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPlanNode;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyRouteOverlay {

	private Activity mActivity;

	private MapView mMapView = null;
	private BMapManager mBMapMan = null;
	private MapController mMapController = null;
	private MKSearch mSearch = null;

	private GeoPoint start_point, end_point;
	private int mode;

	MyRouteOverlay(final MapView mMapView, BMapManager mBMapMan,
			GeoPoint startPoint, GeoPoint endPoint, int mode,
			final Activity mActivity) {
		this.mActivity = mActivity;
		this.mMapView = mMapView;
		this.mBMapMan = mBMapMan;
		mMapController = mMapView.getController();

		start_point = startPoint;
		end_point = endPoint;
		this.mode = mode;

		init();
	}

	public void init() {
		mSearch = new MKSearch();
		mSearch.init(mBMapMan, new MKSearchListener() {

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// 没找到结果
				if (error != 0 || res == null) {
					Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(mActivity,
						mMapView);

				routeOverlay.setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapController.animateTo(res.getStart().pt);
				mMapView.refresh();
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				TransitOverlay routeOverlay = new TransitOverlay(mActivity,
						mMapView);

				routeOverlay.setData(res.getPlan(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapController.animateTo(res.getStart().pt);
				mMapView.refresh();
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(mActivity, "抱歉，未找到结果", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				RouteOverlay routeOverlay = new RouteOverlay(mActivity,
						mMapView);
				routeOverlay.setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapController.animateTo(res.getStart().pt);
				mMapView.refresh();

			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {

			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {

			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 
	 * @param mode
	 *            :0表示驾车
	 * */
	public void routeMode() {
		MKPlanNode stNode = new MKPlanNode();
		MKPlanNode enNode = new MKPlanNode();
		stNode.pt = start_point;
		enNode.pt = end_point;

		if (mode == 0) {
			mSearch.drivingSearch("北京", stNode, "北京", enNode);
		} else if (mode == 1) {
			mSearch.walkingSearch("北京", stNode, "北京", enNode);
		}

	}
}
