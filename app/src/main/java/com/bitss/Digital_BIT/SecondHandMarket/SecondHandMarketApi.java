package com.bitss.Digital_BIT.SecondHandMarket;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * @author Junhao Zhou 2017/5/24
 */

public interface SecondHandMarketApi {

  @Multipart
  @PUT("good/front/good")
  Call<ResponseBody> publish(@Part("content") String content,
                             @Part("kind") int kind,
                             @Part("price") double price,
                             @Part("title") String title,
                             @Part("icon1") String icon1,
                             @Part("icon2") String icon2,
                             @Part("icon3") String icon3);




}
