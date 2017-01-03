package com.bitss.Digital_BIT.Admission;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;

public class AdmissionAQActivity extends CustomBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_admission_college_info);

		mTvNaviTitle.setText(getString(R.string.str_admission_aq));
		WebView webView = (WebView) findViewById(R.id.admission_college_info);
		ImageView imageView = (ImageView) findViewById(R.id.img_bg_college);

		String html_file = getIntent().getExtras().getString("html_file");
		if (html_file != null) {
			imageView.setImageResource(R.drawable.bg_admission_aq);
			webView.loadUrl(String.format("file:///android_asset/%s.html",
					html_file));
		}
	}

}
