package com.bitss.Digital_BIT.receiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Guide.GuideActivity;
import com.bitss.Digital_BIT.Meeting.RemindManager;
import com.bitss.Digital_BIT.Meeting.RemindModel;
import com.bitss.Digital_BIT.Util.StringUtil;
import com.bitss.Digital_BIT.Util.TimeFormatUtil;

public class MyReceiver extends BroadcastReceiver {

	private static final String TAG = "MyReceiver";
	private Context context;
	private RemindManager manager;
	private Vibrator vibrator;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		String action = intent.getAction();
		manager = new RemindManager(context);

		if (action.equals(StringUtil.ACTION_MEETING_REMIND_STRING)) {
			Log.e(TAG, "receive the remind action");

			String type = intent.getExtras().getString("type");
			String id = intent.getExtras().getString("remind_id");

			setNotification(id, type);

		} else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.e(TAG, "receive the boot completed action");

			resetRemind();
		}
	}

	void setNotification(String id, String type) {

		Intent intent = new Intent(context, GuideActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(context,
				Integer.valueOf(id), intent, 0);

		Notification notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.when = System.currentTimeMillis();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;

		// 删除数据库为id的记录
		if (manager == null) {
			manager = new RemindManager(context);
		}

		RemindModel remindMeeting = manager.getRemindById(id);
		if (remindMeeting != null) {
			TimeFormatUtil timeFormatUtil = new TimeFormatUtil();
			String name = remindMeeting.name;
			String content = timeFormatUtil.format2String(remindMeeting.time)
					+ " " + remindMeeting.place;

			if (type.equals("normal")) {
				notification.tickerText = "开会提醒";
			} else {
				notification.tickerText = "您可能有错过的会议";
			}

			notification.setLatestEventInfo(context, name, content, pi);
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(context.NOTIFICATION_SERVICE);
			notificationManager.notify(Integer.valueOf(id), notification);

			// 震动
			setupVibrator();

			// 删除纪录
			manager.deleteData(id);
		}
	}

	/**
	 * 开机检查所有未完成的提醒，如果提醒时间未到，重新设置提醒；如果提醒时间已过，则立即提醒
	 * */
	public void resetRemind() {

		List<RemindModel> unRemindList = new ArrayList<RemindModel>();
		unRemindList = manager.getUnRemindList();

		for (RemindModel remindModel : unRemindList) {

			// 把当前时间和remind_time进行比较，然后设置相应的闹铃
			long remindTime = remindModel.remind_time;
			long nowTime = new Date().getTime();

			PendingIntent pendingIntent;
			Intent mIntent = new Intent(StringUtil.ACTION_MEETING_REMIND_STRING);
			mIntent.putExtra("remind_id", remindModel.id);

			AlarmManager am = (AlarmManager) context
					.getSystemService(context.ALARM_SERVICE);

			if (nowTime >= remindTime) {
				// 立即提醒，可能已经错过几点的会议
				mIntent.putExtra("type", "remind_now");
				pendingIntent = PendingIntent.getBroadcast(context,
						Integer.valueOf(remindModel.id), mIntent, 0);
				am.set(AlarmManager.RTC_WAKEUP, nowTime, pendingIntent);
			} else {
				// 重新设置提醒
				mIntent.putExtra("type", "normal");
				pendingIntent = PendingIntent.getBroadcast(context,
						Integer.valueOf(remindModel.id), mIntent, 0);
				am.set(AlarmManager.RTC_WAKEUP, remindTime, pendingIntent);
			}
		}
	}

	// 启动一个振动器
	public void setupVibrator() {
		if (vibrator == null) {
			vibrator = (Vibrator) context
					.getSystemService(Service.VIBRATOR_SERVICE);
		}
		vibrator.vibrate(new long[] { 100, 10, 100, 1000 }, -1);
	}

}
