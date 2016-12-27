package com.bitss.Digital_BIT.SecondHandMarket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Personal.PersonalMoreInfoActivity;

public class SecondHandEditTextActivity extends CustomBaseActivity {

	private EditText et_content;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_text);
		type = getIntent().getStringExtra("type");
		mTvNaviTitle.setText("编辑信息");
		et_content = (EditText) findViewById(R.id.et_text);
		et_content.setText(getIntent().getStringExtra("pre_data"));
		mTvRight.setVisibility(View.VISIBLE);
		mTvRight.setText("完成");
		listener();
	}

	private void listener() {
		mTvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (et_content.getText().toString().length() == 0) {
					Toast.makeText(SecondHandEditTextActivity.this, "请填写内容", 0)
							.show();
				} else {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0); // 强制隐藏键盘
					if (type.equals("publish")) {
						Intent intent = new Intent(
								SecondHandEditTextActivity.this,
								SecondHandPublishActivity.class);
						intent.putExtra("content", et_content.getText()
								.toString());
						setResult(RESULT_OK, intent);
						finish();
					} else {
						Intent intent = new Intent(
								SecondHandEditTextActivity.this,
								PersonalMoreInfoActivity.class);
						intent.putExtra("content", et_content.getText()
								.toString());
						setResult(RESULT_OK, intent);
						finish();
					}
				}

			}
		});
	}
}
