package com.bitss.Digital_BIT.okhttp;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.Util.Constants;

import retrofit2.Retrofit;

/**
 * @author Junhao Zhou 2017/5/28
 */

public class RetrofitFactory {

  public static Retrofit.Builder newInstance() {
    return new Retrofit.Builder()
            .baseUrl(Constants.SERVER_URL)
            .client(HttpClient.getInstance(BaseApplication.getApplcation())
                    .getOkHttpClient());
  }
}
