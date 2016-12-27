package com.bitss.Digital_BIT.Admission;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bitss.Digital_BIT.R;

@SuppressLint("ValidFragment")
public class CollegeContentFragment extends Fragment {

	private WebView webView;

	private String file;

	@SuppressLint("ValidFragment")
	public CollegeContentFragment(String file) {
		this.file = file;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.act_admission_college_info, container, false);

		webView = (WebView) view.findViewById(R.id.admission_college_info);
		webView.loadUrl(String.format("file:///android_asset/%s.html", file));

		return view;
	}

}
