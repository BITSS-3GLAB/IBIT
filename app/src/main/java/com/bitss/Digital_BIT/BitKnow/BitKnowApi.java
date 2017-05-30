package com.bitss.Digital_BIT.BitKnow;

import com.bitss.Digital_BIT.BitKnow.model.AnswerModel;
import com.bitss.Digital_BIT.BitKnow.model.QuestionModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * @author Junhao Zhou 2017/5/24
 */

public interface BitKnowApi {

  @GET("/IBIT/question/front/resolvedQuestions")
  Call<List<QuestionModel>> getHottestList(@Query("page") int page);

  @GET("/IBIT/question/front/unresolvedQuestions")
  Call<List<QuestionModel>> getLatestList(@Query("page") int page);

  @GET("/IBIT/question/front/personalQuestions/resolved")
  Call<List<QuestionModel>> getResolved(@Query("page") int page);

  @GET("/IBIT/question/front/personalQuestions/unresolved")
  Call<List<QuestionModel>> getUnresolved(@Query("page") int page);

  @GET("/IBIT/question/front/questionById/{id}")
  Call<QuestionModel> getDetail(@Path("id") int id);

  @GET("/IBIT/question/front/answerListByQuestion/{id}")
  Call<List<AnswerModel>> getAnswerList(@Path("id") int id, @Query("page") int page);

  @Multipart
  @PUT("/IBIT/question/front/question")
  Call<ResponseBody> postQuestion(@Part MultipartBody.Part title,
                                  @Part MultipartBody.Part content,
                                  @Part MultipartBody.Part tags,
                                  @Part MultipartBody.Part icon);

  @PUT("/IBIT/question/front/answerContent")
  Call<ResponseBody> postAnswer(RequestBody body);

  @FormUrlEncoded
  @PUT("/IBIT/question/front/answerAgreement")
  Call<ResponseBody> like(@Field("answerId") int id);

  @FormUrlEncoded
  @HTTP(method = "DELETE", path = "/IBIT/question/front/answerAgreement", hasBody = true)
  Call<ResponseBody> unlike(@Field("answerId") int id);

  @FormUrlEncoded
  @POST("/IBIT/question/front/bestAnswer")
  Call<ResponseBody> adopt(@Field("answerId") int id);


}
