package com.bitss.Digital_BIT.BitKnow;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.BitKnow.model.AnswerModel;
import com.bitss.Digital_BIT.BitKnow.model.QuestionModel;
import com.bitss.Digital_BIT.BitKnow.model.TagModel;
import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.RetrofitFactory;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BitKnowDetailActivity extends CustomBaseActivity {
  private Context context;
  private DisplayImageOptions displayImageOptions;

  private boolean mIsRefresh = false;// 此时下拉刷新是否在进行
  private List<AnswerModel> answerList = new ArrayList<AnswerModel>();
  private PullToRefreshListView pullToRefreshListView;
  private TextView emptyText;
  private BitKnowAnswerAdapter answerListAdapter;

  private int currentPage = 1;// 记录已经加载多少页

  private TextView usernameText;
  private TextView timeText;
  private TextView contentText;
  private RoundedImageView userIcon;
  private ImageView questionImage;
  private ImageView questionImageView2;
  private ImageView questionImageView3;
  private TextView questionTag1;
  private TextView questionTag2;
  private TextView questionTag3;

  private TextView answerNumberText;
  private EditText BitKnowComment;
  private SharedPreferences settings;
  private Dialog dialog;

  private EditText etxtName, etxtComment;

  private int questionId;
  private QuestionModel questionModel;
  private String phone;
  private String myphone;// 记录登录手机
  private int itemPosition;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bitknow_detail);
    Intent intent = getIntent();
    questionModel = (QuestionModel) intent.getSerializableExtra("data");
    questionId = questionModel.id;
    itemPosition = intent.getIntExtra(Constants.KEY_ITEM_POSITION, -1);
    init();
    listener();
  }

  @Override
  public void backAction() {
    Intent intent = new Intent();
    intent.putExtra(Constants.KEY_ANSWER_COUNT, questionModel.answerNum);
    intent.putExtra(Constants.KEY_ITEM_POSITION, itemPosition);
    setResult(Constants.ANSWER_COUNT_REFRESH, intent);
    finish();
  }

  @SuppressWarnings({"unchecked", "deprecation"})
  private void init() {
    displayImageOptions = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.uil_ic_stub)
            .showImageForEmptyUri(R.drawable.uil_ic_empty)
            .showImageOnFail(R.drawable.uil_ic_error)
            .resetViewBeforeLoading(false)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .displayer(new FadeInBitmapDisplayer(200)).cacheInMemory(true)
            .cacheOnDisc(true).build();
    context = this;

    mTvNaviTitle.setText(getResources().getString(R.string.bit_konw));// 此处应该改为获得姓名

    emptyText = (TextView) findViewById(R.id.tv_answer_info);
    pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.bit_know_pull_to_refresh_listview);
    pullToRefreshListView.setShowIndicator(false);
    pullToRefreshListView
            .setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
    answerListAdapter = new BitKnowAnswerAdapter(
            context, answerList, questionModel.id,
            phone, myphone);
    pullToRefreshListView
            .setAdapter(answerListAdapter);// 为ListView控件绑定适配器

    usernameText = (TextView) findViewById(R.id.bitkonwname);
    timeText = (TextView) findViewById(R.id.bitkonwtime);
    contentText = (TextView) findViewById(R.id.bitkonwquestion);
    userIcon = (RoundedImageView) findViewById(R.id.bitkonwimageView);
    questionImage = (ImageView) findViewById(R.id.bit_know_question_imageview1);
    questionImageView2 = (ImageView) findViewById(R.id.bit_know_question_imageview2);
    questionImageView3 = (ImageView) findViewById(R.id.bit_know_question_imageview3);
    questionTag1 = (TextView) findViewById(R.id.bitkonwlabel1);
    questionTag2 = (TextView) findViewById(R.id.bitkonwlabel2);
    questionTag3 = (TextView) findViewById(R.id.bitkonwlabel3);
    BitKnowComment = (EditText) findViewById(R.id.bit_know_answer_textview);
    answerNumberText = (TextView) findViewById(R.id.bitknowanswernumber);

    settings = getSharedPreferences("User", 0);
    myphone = settings.getString(Constants.USER_PHONE, "");

    mTvNaviTitle.setText("北理知道");
    mTvRight.setText("");
    mTvRight.setVisibility(View.VISIBLE);

    setContentText(questionModel);
    loadDetail();
  }

  @SuppressWarnings("unchecked")
  public void listener() {
    BitKnowComment.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        if (settings.getString(Constants.USER_PHONE, "").equals("")) {
          Utils.haveNotLogin(BitKnowDetailActivity.this);
          myphone = settings.getString(Constants.USER_PHONE, "");
        } else {
          myselfDialog();
        }
      }
    });
    pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {
      @Override
      public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        doRefresh();
      }

      @Override
      public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        onMore();
      }
    });
    mTvRight.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

      }
    });
  }

  public void myselfDialog() {
    dialog = new Dialog(BitKnowDetailActivity.this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.outputcomment);

    Window diaWindow = dialog.getWindow();
    WindowManager.LayoutParams lp = diaWindow.getAttributes();
    diaWindow.setGravity(Gravity.BOTTOM);
    lp.width = LayoutParams.MATCH_PARENT;
    diaWindow.setAttributes(lp);
    ImageView imgOK = (ImageView) dialog.findViewById(R.id.imgOK);
    ImageView imgNO = (ImageView) dialog.findViewById(R.id.imgNo);
    etxtName = (EditText) dialog.findViewById(R.id.etxtName);
    etxtName.setVisibility(View.GONE);
    etxtComment = (EditText) dialog.findViewById(R.id.etxtCommentDetail);
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(etxtComment, InputMethodManager.SHOW_FORCED);
    imgNO.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        dialog.dismiss();
      }
    });

    imgOK.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        String content = etxtComment.getText().toString();

        if (content.trim().length() == 0) {
          Toast.makeText(
                  BitKnowDetailActivity.this,
                  BitKnowDetailActivity.this
                          .getString(R.string.warning_comment_empty),
                  Toast.LENGTH_SHORT).show();
          return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etxtComment.getWindowToken(), 0); // 强制隐藏键盘
        postAnswer(content);

        // 每次将回答发表之后都要刷新一次得到最新的评论信息
        dialog.dismiss();

      }
    });
    etxtComment.setHint("请回答...");
    dialog.setTitle("回答");

    dialog.show();

  }

  private void postAnswer(String content) {
    Retrofit retrofit = RetrofitFactory.newInstance().build();
    BitKnowApi api = retrofit.create(BitKnowApi.class);

    JsonObject param = new JsonObject();
    param.addProperty("content", content);
    param.addProperty("questionId", questionId);
    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
            param.toString());
    Call<ResponseBody> call = api.postAnswer(body);
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        int code = response.code();
        if (code == 201) {
          Toast.makeText(BitKnowDetailActivity.this,
                  "回答成功", Toast.LENGTH_SHORT).show();
          doRefresh();
        } else if (code == 401) {
          // TODO
        } else {

        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {

      }
    });
  }

  private void loadDetail() {
    Retrofit retrofit = RetrofitFactory.newInstance()
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    BitKnowApi api = retrofit.create(BitKnowApi.class);

    Call<QuestionModel> call = api.getDetail(questionId);
    call.enqueue(new Callback<QuestionModel>() {
      @Override
      public void onResponse(Call<QuestionModel> call, Response<QuestionModel> response) {
        setContentText(response.body());
        currentPage = 1;
        loadAnswers(currentPage);
      }

      @Override
      public void onFailure(Call<QuestionModel> call, Throwable t) {
        Toast.makeText(context, "加载详情失败", Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void loadAnswers(final int page) {
    Retrofit retrofit = RetrofitFactory.newInstance()
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    final BitKnowApi api = retrofit.create(BitKnowApi.class);

    Call<List<AnswerModel>> call = api.getAnswerList(questionId, page);
    call.enqueue(new Callback<List<AnswerModel>>() {
      @Override
      public void onResponse(Call<List<AnswerModel>> call, Response<List<AnswerModel>> response) {
        if (currentPage == 1) {
          answerList.clear();
        }
        List<AnswerModel> ret = response.body();
        if (ret == null || ret.size() == 0) {

        } else {
          answerList.addAll(ret);
          currentPage++;
        }
        if (answerList.size() > 0) {
          pullToRefreshListView
                  .setVisibility(View.VISIBLE);
          emptyText.setVisibility(View.GONE);
        } else {
          emptyText.setVisibility(View.VISIBLE);
        }
        answerListAdapter.notifyDataSetChanged();

        pullToRefreshListView.onRefreshComplete();
        mIsRefresh = false;
      }

      @Override
      public void onFailure(Call<List<AnswerModel>> call, Throwable t) {
        Toast.makeText(context, "网络异常",
                Toast.LENGTH_SHORT).show();
        pullToRefreshListView.onRefreshComplete();
        mIsRefresh = false;
      }
    });
  }

  private void doRefresh() {
    if (!mIsRefresh) {
      // 此时标记为下拉开始
      mIsRefresh = true;
      currentPage = 1;
      loadAnswers(currentPage);
    }
  }

  private void onMore() {
    if (!mIsRefresh) {
      // 此时标记为下拉开始
      mIsRefresh = false;
      loadAnswers(currentPage);
    }
  }

  private void setContentText(QuestionModel question) {
    if (question == null) {
      return;
    }

    String time = question.date;
    List<TagModel> tags = question.tags;
    if (tags.size() >= 3) {
      questionTag1.setText(tags.get(0).tagName);
      questionTag2.setText(tags.get(1).tagName);
      questionTag3.setText(tags.get(2).tagName);
    } else if (tags.size() == 2) {
      questionTag1.setText(tags.get(0).tagName);
      questionTag2.setText(tags.get(1).tagName);
      questionTag3.setVisibility(View.GONE);
    } else if (tags.size() == 1) {
      questionTag1.setText(tags.get(0).tagName);
      questionTag2.setVisibility(View.GONE);
      questionTag3.setVisibility(View.GONE);
    } else if (tags.size() == 0) {
      questionTag1.setVisibility(View.GONE);
      questionTag2.setVisibility(View.GONE);
      questionTag3.setVisibility(View.GONE);
    }

    final String questionPhoto = Constants.PHTOT_CLOUDSERVER_STRING + question.iconUrl;

    questionImage.setVisibility(View.GONE);
    questionImageView2.setVisibility(View.GONE);
    questionImageView3.setVisibility(View.GONE);

    int image_num = TextUtils.isEmpty(question.iconUrl) ? 0 : 1;

    switch (image_num) {
      case 1:
        questionImage.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(questionPhoto,
                questionImage, displayImageOptions);
        questionImage.setOnClickListener(new OnClickListener() {

          @Override
          public void onClick(View v) {
            Intent intent = new Intent(BitKnowDetailActivity.this,
                    BitKnowDetailPhotoActivity.class);
            intent.putExtra("url", questionPhoto);
            startActivity(intent);
          }
        });
        break;
      default:
        break;
    }

    String avatarUrl = Constants.PHTOT_CLOUDSERVER_STRING + question.owner.iconUrl;
    usernameText.setText(questionModel.owner.name);
    ImageLoader.getInstance().displayImage(avatarUrl, userIcon,
            displayImageOptions);
    // ImageLoader.getInstance().displayImage(Constants.BITKNOWTEST_SERVER_STRING+urlicon,userIcon);


    contentText.setText(question.content);
    timeText.setText(time);
    answerNumberText.setText("" + question.answerNum);
  }

}
