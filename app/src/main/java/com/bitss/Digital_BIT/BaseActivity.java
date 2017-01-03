package com.bitss.Digital_BIT;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends SherlockFragmentActivity {

	protected BaseApplication mApp = null;
	protected ActionBar mActionBar = null;
	protected String pageName = "BaseActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionBar = getSupportActionBar();

		mApp = (BaseApplication) getApplicationContext();

		mApp.addActivity(this);
	}

	@Override
	public void onDestroy() {
		mApp.removeActivity(this);
		super.onDestroy();
	}

	private final Handler exceptionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Exception e = (Exception) msg.obj;
			handleException0(e);
		}

	};

	@SuppressLint("HandlerLeak")
	public void handleException(Exception exception) {
		exception.printStackTrace();
		Message msg = exceptionHandler.obtainMessage();
		msg.obj = exception;
		msg.sendToTarget();
	}

	@SuppressLint("ShowToast")
	protected void handleException0(Exception exception) {
		int err = 0;

		try {
			throw exception;
		} catch (UnknownHostException e) {
			err = R.string.error_host;
		} catch (HttpHostConnectException e) {
			err = R.string.error_host;
		} catch (ConnectTimeoutException e) {
			err = R.string.error_timeout;
		} catch (SocketTimeoutException e) {
			err = R.string.error_timeout;
		} catch (JSONException e) {
			err = R.string.error_json;
		} catch (Exception e) {
			err = R.string.error_unknow;
		}

		if (err != 0) {
			Toast.makeText(this, err, Toast.LENGTH_SHORT);
		}
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
