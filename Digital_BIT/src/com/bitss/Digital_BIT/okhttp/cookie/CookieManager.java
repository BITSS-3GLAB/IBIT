package com.bitss.Digital_BIT.okhttp.cookie;

import java.util.List;

import android.content.Context;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieManager implements CookieJar {

	private PersistentCookieStore mCookieStore;

	public CookieManager(Context context) {
		mCookieStore = new PersistentCookieStore(
				context.getApplicationContext());
	}

	@Override
	public List<Cookie> loadForRequest(HttpUrl url) {
		return mCookieStore.get(url);
	}

	@Override
	public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
		if (cookies == null) {
			return;
		}
		for (Cookie cookie : cookies) {
			mCookieStore.add(url, cookie);
		}
	}

}
