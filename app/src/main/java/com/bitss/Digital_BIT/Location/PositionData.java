package com.bitss.Digital_BIT.Location;

import java.io.Serializable;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class PositionData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String address;
	private String latitude;
	private String longitude;

	public PositionData(String name, String address, String latitude,
			String longitude) {
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public GeoPoint getPoint() {
		return new GeoPoint((int) ((Double.valueOf(latitude)) * 1e6),
				(int) ((Double.valueOf(longitude)) * 1e6));
	}
}
