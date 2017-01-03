package com.bitss.Digital_BIT.Util;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.Personal.LoginActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

public class ReLogin {

	private AlertDialog mDialog = null;
	private Context context = null;
	private Activity mAct;

	public ReLogin(Context ctx) {
		if (ctx instanceof Activity) {
			this.context = ctx.getApplicationContext();
			this.mAct = (Activity) ctx;
		} else if (ctx instanceof Application) {
			throw new IllegalArgumentException(
					"can not create a dialog use the application context");
		} else {
		}
		build();
	}

	private void build() {
		if (mDialog == null) {
			Builder builder = new Builder(mAct);
			builder.setMessage("登录信息已过期，请重新登录。")
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).setPositiveButton("去登录", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(mAct,
									LoginActivity.class);
							mAct.startActivity(intent);
							dialog.dismiss();
						}
					});
			mDialog = builder.create();
		}
	}

	private void clear() {
		((BaseApplication) context).getPreferences().edit()
				.remove(Constants.USER_PHONE).remove(Constants.NICK_NAME)
				.remove(Constants.KEY_EMAIL).commit();
	}

	public void showDialog() {
		clear();
		if (!mDialog.isShowing()) {
			mDialog.show();
		}
	}
}
