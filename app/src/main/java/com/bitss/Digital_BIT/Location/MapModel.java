package com.bitss.Digital_BIT.Location;

public class MapModel {

	// 分组的结构
	static class GroupModel {
		public String name;// 分组名字
		public String num;// 分组下的个数

		public GroupModel(String name, String num) {
			this.name = name;
			this.num = num;
		}
	}

	// 子集的结构
	static class ChildModel {
		public String name;// 子集名字
		public String address;// 子集地址
		public String latitude;
		public String longitude;
		public int distance;// 子集距离

		public ChildModel(String name, String address, String lat, String lng,
				int distance) {
			this.name = name;
			this.address = address;
			this.latitude = lat;
			this.longitude = lng;
			this.distance = distance;
		}
	}

}
