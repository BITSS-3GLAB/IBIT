package com.bitss.Digital_BIT.BitKnow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class BitKnowNetworkHandler extends Handler{
	public static final int SUCCESS = 200;
	public static final int FAILURE = 301;
	
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		
		switch (msg.what) {
		case SUCCESS:
			Bundle bundle = msg.getData();
			onSuccess(bundle.getString("data"));
			break;
		case FAILURE:
			onFailure();
			break;
		default:
			break;
		}
	}

	public abstract void onSuccess(String data);
	public abstract void onFailure();

}
