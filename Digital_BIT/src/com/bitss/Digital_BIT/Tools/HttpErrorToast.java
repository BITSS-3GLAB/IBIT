package com.bitss.Digital_BIT.Tools;

import com.bitss.Digital_BIT.R;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class HttpErrorToast {
	public static void Show(Context context) {
		Toast toast = Toast.makeText(context,
				context.getString(R.string.http_ask_error), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 50);
		toast.show();
	}
}
