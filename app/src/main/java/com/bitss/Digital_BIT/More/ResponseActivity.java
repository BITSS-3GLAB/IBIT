package com.bitss.Digital_BIT.More;

import java.io.IOException;

import okhttp3.Response;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.ReLogin;
import com.bitss.Digital_BIT.okhttp.HttpClient;
import com.bitss.Digital_BIT.okhttp.request.DefaultJsonBodyBuilder;
import com.bitss.Digital_BIT.okhttp.request.HttpRequest;
import com.bitss.Digital_BIT.okhttp.request.Parameter;
import com.umeng.analytics.MobclickAgent;

public class ResponseActivity extends Activity {

	private Context context;
	private static final String TITLE = "反馈";

	private ImageView backButton;
	private TextView titleTv;
	private ImageView sendButton;

	private EditText sendEdit;
	private ImageView cleanButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responselayout);
		init();
		setListener();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void init() {
		context = this;
		backButton = (ImageView) findViewById(R.id.iv_navi_back);
		titleTv = (TextView) findViewById(R.id.tv_navi_title);
		titleTv.setText(TITLE);
		sendButton = (ImageView) findViewById(R.id.iv_navi_share);
		sendButton.setImageResource(R.drawable.nvg_accept_style);
		sendButton.setVisibility(View.VISIBLE);

		sendEdit = (EditText) findViewById(R.id.responseedit);
		cleanButton = (ImageView) findViewById(R.id.clear_response);
		cleanButton.setVisibility(View.INVISIBLE);
	}

	private void setListener() {
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = sendEdit.getText().toString();
				if (str.length() > 0) {
					str = str.replaceAll("\n", ",");
					str = str.replaceAll("\r", ",");
					str = str.replaceAll("\r\n", ",");
					str = str.replaceAll(" ", ",");
					str = str.replaceAll("\"", ",");
					str = str.replaceAll("\'", ",");
					String tmp = str.replaceAll(",", "");
					if (tmp.length() == 0)
						return;
					new GetDataTask().execute(str);
				} else {
					Toast.makeText(context, "您尚未填写反馈哟", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		cleanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendEdit.setText("");
			}
		});
		sendEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().length() != 0) {
					cleanButton.setVisibility(View.VISIBLE);
				} else {
					cleanButton.setVisibility(View.INVISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	private class GetDataTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			int ans = -1;
			// ans = new MoreHttpSender().sendMessage(params[0]);
			HttpRequest request = new HttpRequest.Builder()
					.url("/feedback/front/feedback")
					.bodyBuilder(new DefaultJsonBodyBuilder())
					.add(new Parameter("content", params[0]))
					.add(new Parameter("platForm", 0)).build();
			try {
				Response response = HttpClient.getInstance(context).put(
						request.getUrl(), request.getBody());
				ans = response.code();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return ans;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// Call onRefreshComplete when the list has been refreshed.
			String rStr;
			if (result == 201) {
				// 清空发送的内容
				sendEdit.setText("");
				rStr = context.getString(R.string.http_send_sucess);
			} else if (result == 401) {
				rStr = context.getString(R.string.http_send_fail);
				new ReLogin(context).showDialog();
			} else {
				rStr = context.getString(R.string.http_send_fail);
			}
			Toast toast = Toast.makeText(context, rStr, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 50);
			toast.show();

			super.onPostExecute(result);
		}
	}
}