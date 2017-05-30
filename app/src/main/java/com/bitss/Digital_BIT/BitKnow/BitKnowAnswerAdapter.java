package com.bitss.Digital_BIT.BitKnow;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.BitKnow.model.AnswerModel;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.RetrofitFactory;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BitKnowAnswerAdapter extends BaseAdapter {
  private Context context;
  private List<AnswerModel> answerList;
  private String phone;
  private String myphone;
  private SharedPreferences settings;

  public BitKnowAnswerAdapter(Context context, List<AnswerModel> data,
                              int questionId, String phone, String myphone) {
    this.answerList = data;
    this.context = context;
    this.phone = phone;
    this.myphone = myphone;
  }

  @Override
  public int getCount() {
    if (answerList == null)
      return 0;
    return answerList.size();
  }

  @Override
  public Object getItem(int arg0) {
    return answerList.get(arg0);
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;

    if (convertView == null) {

      holder = new ViewHolder();
      LayoutInflater flater = LayoutInflater.from(context);
      convertView = flater.inflate(R.layout.listview_bitkonw_detail, parent, false);
      holder.avatar = (RoundedImageView) convertView
              .findViewById(R.id.bit_know_user_imageView);

      holder.username = (TextView) convertView
              .findViewById(R.id.user_name);
      holder.answerTime = (TextView) convertView
              .findViewById(R.id.answer_time);
      holder.answerLikeNum = (TextView) convertView
              .findViewById(R.id.answer_like_num);
      holder.answerContent = (TextView) convertView
              .findViewById(R.id.answer_content);
      holder.answerLikeIcon = (ImageView) convertView
              .findViewById(R.id.answer_like_icon);
      holder.adoptAnswer = (TextView) convertView
              .findViewById(R.id.adopt_answer);
      holder.deleteAnswer = (TextView) convertView
              .findViewById(R.id.delete_answer);
      holder.answerAdoptionIcon = (ImageView) convertView
              .findViewById(R.id.answer_adoption_icon);
      holder.operationContainer = (LinearLayout) convertView
              .findViewById(R.id.answer_operation_container);

      convertView.setTag(holder);

    } else {

      holder = (ViewHolder) convertView.getTag();
    }

    AnswerModel answer = answerList.get(position);

    ImageLoader.getInstance().displayImage(
            Constants.PHTOT_CLOUDSERVER_STRING
                    + answer.owner.iconUrl,
            holder.avatar);

    if (position == 0) {
      holder.answerAdoptionIcon.setVisibility(answer.adoption ? View.VISIBLE : View.GONE);
    }

//    if (myphone.contains(phone)) {
//      holder.operationContainer.setVisibility(View.VISIBLE);
//    } else {
    holder.operationContainer.setVisibility(View.GONE);
//    }

    if (answer.hasAgreement) {
      holder.answerLikeIcon.setImageResource(R.drawable.bit_know_zan_red);
    } else {
      holder.answerLikeIcon.setImageResource(R.drawable.icon_bitknow_good);
    }
    holder.answerLikeIcon.setTag(answer.hasAgreement);

    holder.username.setText(answer.owner.name);
    holder.answerTime.setText(answer.time);
    holder.answerLikeNum.setText(answer.agreementNum + "个赞");
    holder.answerContent.setText(answer.content);
    holder.answerLikeIcon.setOnClickListener(new AnswerListener(
            holder.answerLikeIcon, holder.answerLikeNum, position));
    holder.adoptAnswer.setOnClickListener(new AnswerAdoptListener(position,
            holder.answerAdoptionIcon));

    return convertView;

  }

  public String getMyphone() {
    return myphone;
  }

  public void setMyPhone(String myphone) {
    this.myphone = myphone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  private class ViewHolder {
    RoundedImageView avatar;// 用户头像
    TextView username;
    TextView answerTime;
    TextView answerLikeNum;
    TextView answerContent;
    ImageView answerLikeIcon;
    TextView adoptAnswer;
    TextView deleteAnswer;
    ImageView answerAdoptionIcon;
    LinearLayout operationContainer;
  }

  private class AnswerListener implements OnClickListener {
    private ImageView likeIcon;
    private TextView likeNum;
    private int position;

    public AnswerListener(ImageView likeIcon, TextView likeNum,
                          int position) {
      this.likeIcon = likeIcon;
      this.likeNum = likeNum;
      this.position = position;
    }

    @Override
    public void onClick(View v) {
      final AnswerModel answer = answerList.get(position);
      int id = answer.id;

      Retrofit retrofit = RetrofitFactory.newInstance()
              .build();
      BitKnowApi api = retrofit.create(BitKnowApi.class);
      boolean hasAgreement = (Boolean) likeIcon.getTag();
      if (!hasAgreement) {
        Call<ResponseBody> call = api.like(id);
        call.enqueue(new Callback<ResponseBody>() {
          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.code() == 201) {
              Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
              likeIcon
                      .setImageResource(R.drawable.bit_know_zan_red);
              answer.agreementNum++;
              likeNum.setText(answer.agreementNum + "个赞");
              likeIcon.setTag(true);
            } else if (response.code() == 401) {
              Utils.haveNotLogin(context);
            } else if (response.code() == 409) {
              Toast.makeText(context, "已点过赞", Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
          }
        });
      } else {
        Call<ResponseBody> call = api.unlike(id);
        call.enqueue(new Callback<ResponseBody>() {
          @Override
          public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.code() == 201) {
              Toast.makeText(context, "取消赞成功", Toast.LENGTH_SHORT).show();
              likeIcon
                      .setImageResource(R.drawable.icon_bitknow_good);
              answer.agreementNum--;
              likeNum.setText(answer.agreementNum + "个赞");
              likeIcon.setTag(false);
            } else if (response.code() == 401) {
              Utils.haveNotLogin(context);
            } else {
              Toast.makeText(context, "取消赞失败", Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(context, "取消赞失败", Toast.LENGTH_SHORT).show();
          }
        });
      }
    }

  }

  private class AnswerAdoptListener implements OnClickListener {
    private ImageView answeruse;
    private int position;

    public AnswerAdoptListener(int position, ImageView answeruse) {
      this.position = position;
      this.answeruse = answeruse;
    }

    @Override
    public void onClick(View v) {
      AnswerModel answer = answerList.get(position);
      int id = answer.id;
      Retrofit retrofit = RetrofitFactory.newInstance()
              .build();
      BitKnowApi api = retrofit.create(BitKnowApi.class);
      Call<ResponseBody> call = api.adopt(id);
      call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
          int code = response.code();
          if (code == 201) {
            Toast.makeText(context, "采纳成功", Toast.LENGTH_SHORT)
                    .show();
            answerList.get(0).adoption = false;
            answerList.get(position).adoption = true;
            AnswerModel data = answerList
                    .get(position);
            answerList.remove(position);
            answerList.add(0, data);
          } else if (code == 401) {
            Utils.haveNotLogin(context);
          } else if (code == 409) {
            Toast.makeText(context, "已采纳", Toast.LENGTH_SHORT).show();
          } else {
            Toast.makeText(context, "采纳失败", Toast.LENGTH_SHORT).show();
          }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
          Toast.makeText(context, "采纳失败", Toast.LENGTH_SHORT).show();
        }
      });
    }

  }
}
