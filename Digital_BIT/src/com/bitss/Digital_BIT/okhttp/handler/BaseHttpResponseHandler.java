package com.bitss.Digital_BIT.okhttp.handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author 周俊皓 email:junhao.zhou@foxmail.com
 *
 */
public abstract class BaseHttpResponseHandler extends Handler {

	private static final int SUCCESS_MSG = 0x00;
	private static final int FAILURE_MSG = 0x01;

	@Override
	public void handleMessage(Message msg) {
		MessageHolder model;
		switch (msg.what) {
		case SUCCESS_MSG:
			model = (MessageHolder) msg.obj;
			onSuccess(model.call, model.response);
			break;
		case FAILURE_MSG:
			model = (MessageHolder) msg.obj;
			onFailure(model.call, model.exception);
			break;
		default:
			break;
		}
	}

	public void sendFailureMessage(Call call, IOException e) {
		sendMessage(obtainMessage(FAILURE_MSG, new MessageHolder(call, e)));
	}

	public void sendSuccessMessage(Call call, Response response) {
		sendMessage(obtainMessage(SUCCESS_MSG,
				new MessageHolder(call, response)));
	}

	public abstract void onFailure(Call call, IOException e);

	public abstract void onSuccess(Call call, Response response);

	public static class MessageHolder {
		private Call call;
		private IOException exception;
		private Response response;

		public MessageHolder(Call call, IOException e) {
			this.call = call;
			this.exception = e;
		}

		public MessageHolder(Call call, Response response) {
			this.call = call;
			this.response = response;
		}
	}
}
