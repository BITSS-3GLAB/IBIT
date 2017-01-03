package com.bitss.Digital_BIT.Bus;

public class BusInfo {
	public String busInfo;
	public String startPlace;
	public String endPlace;
	public String startTime;
	public String endTime;
	/**
	 * 校车的状态（0：已开走----1：即将要开-----2：需要等待）
	 * */
	public int busStatus;

	public BusInfo(String bus_info, String start_place, String end_place,
			String start_time, String end_time, int bus_status) {
		this.busInfo = bus_info;
		this.startPlace = start_place;
		this.endPlace = end_place;
		this.startTime = start_time;
		this.endTime = end_time;
		this.busStatus = bus_status;
	}
}
