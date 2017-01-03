package com.bitss.Digital_BIT.Bus;

import java.util.Comparator;

/**
 * Collections.sort(List list,Comparator comparator),
 * Comparator是个接口，需要定义比较的规则
 * */
public class ComparatorBus implements Comparator<Object>{

	@Override
	public int compare(Object a, Object b) {
		BusInfo busInfo_1 = (BusInfo) a;
		BusInfo busInfo_2 = (BusInfo) b;
		return busInfo_1.startTime.compareTo(busInfo_2.startTime);
	}

}
