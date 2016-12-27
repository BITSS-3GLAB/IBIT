package com.bitss.Digital_BIT.Admission.database;

public class AdmissionColumns {

	public class Plan {
		public static final String TABLE_NAME = "plan";

		/**
		 * 年份
		 * */
		public static final String KEY_YEAR = "year";

		/**
		 * 省份
		 * */
		public static final String KEY_PROVINCE = "province";

		/**
		 * 专业
		 * */
		public static final String KEY_MAJOR = "major";

		/**
		 * 理工、文史
		 * */
		public static final String KEY_TYPE = "type";

		/**
		 * 学制（4年）
		 * */
		public static final String KEY_LASTS = "lasts";

		/**
		 * 本科
		 * */
		public static final String KEY_LEVEL = "level";

		/**
		 * 学费
		 * */
		public static final String KEY_TUITION = "tuition";

		/**
		 * 招聘人数
		 * */
		public static final String KEY_NUMBER = "number";

	}

	public class Score {
		public static final String TABLE_NAME = "score";

		/**
		 * 年份
		 * */
		public static final String KEY_YEAR = "year";

		/**
		 * 省份
		 * */
		public static final String KEY_PROVINCE = "province";

		/**
		 * 专业
		 * */
		public static final String KEY_MAJOR = "major";

		/**
		 * 理工、文史
		 * */
		public static final String KEY_TYPE = "type";

		/**
		 * 学制（4年）
		 * */
		public static final String KEY_LASTS = "lasts";

		/**
		 * 最高分
		 * */
		public static final String KEY_HIGH = "high";

		/**
		 * 最低分
		 * */
		public static final String KEY_LOW = "low";

	}

}
