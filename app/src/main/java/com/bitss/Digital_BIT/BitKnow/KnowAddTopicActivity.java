package com.bitss.Digital_BIT.BitKnow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.R.drawable;
import com.bitss.Digital_BIT.Util.FileUtils;
import com.bitss.Digital_BIT.Util.Utils;
import com.bitss.Digital_BIT.okhttp.RetrofitFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("deprecation")
public class KnowAddTopicActivity extends CustomBaseActivity {

  private EditText topicDescribe;
  private TopicAdapter topicGrid;
  private ArrayList<String> picUri = new ArrayList<>();
  private ArrayList<Bitmap> bitmaps = new ArrayList<>();
  private String describe;
  private String sentTopic = "";

  public static final int RESULT_OVER = 100;

  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_know_add_topic);

    intiActionBar();
    addGridView();
    getMyIntent();// 得到传来的问题描述和图片uri
    listens();
  }

  /**
   * 设置actionBar
   */
  private void intiActionBar() {
    mTvNaviTitle.setText(R.string.bitknow_add_topic);

    mTvRight.setText(R.string.submit);

    mTvRight.setVisibility(View.VISIBLE);
  }

  /**
   * 得到intent传过来的东西，包括问题描述和图片的uri
   */
  private void getMyIntent() {
    Bundle bundle = getIntent().getExtras();
    describe = bundle.getString(QuestionDescribeActivity.INTENT_DESCRIBE);
    picUri = bundle.getStringArrayList(QuestionDescribeActivity.INTENT_BITMAP);
    getSentBitmap();
  }

  /**
   * 设置所有监听
   */
  @SuppressLint("NewApi")
  private void listens() {

    TextView addBtn = (TextView) findViewById(R.id.add);
    topicDescribe = (EditText) findViewById(R.id.add_topic);

    addBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        if (topicDescribe.getText().toString().trim().equals(""))
          return;

        String topic = topicDescribe.getText().toString().trim();
        topicGrid.addTopic(topic);
        topicDescribe.setText("");
      }
    });

    // 发送问题的监听
    mTvRight.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {

        ArrayList<String> allTopic = topicGrid.getAllTopic();
        if (allTopic.size() < 1) {
          Toast.makeText(KnowAddTopicActivity.this, "请选择至少一个标签",
                  Toast.LENGTH_SHORT).show();
          ;
          return;
        }

        getSentTopic(allTopic);// 得到标签
        postQuestion();
      }
    });

  }

  private void postQuestion() {
    Retrofit retrofit = RetrofitFactory.newInstance()
            .build();
    BitKnowApi api = retrofit.create(BitKnowApi.class);

    MultipartBody.Part filePart = null;
    if (bitmaps.size() == 1) {
      File file = FileUtils.bitmap2file(bitmaps.get(0), 80, 0);
      RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), file);
      filePart = MultipartBody.Part.createFormData("icon", file.getName(), body);
    }
    MultipartBody.Part titlePart = MultipartBody.Part.createFormData("title", "");
    MultipartBody.Part contentPart = MultipartBody.Part.createFormData("content", describe);
    MultipartBody.Part tagsPart = MultipartBody.Part.createFormData("tags", sentTopic);

    Call<ResponseBody> call = api.postQuestion(titlePart,
            contentPart, tagsPart, filePart);
    call.enqueue(new Callback<ResponseBody>() {
      @Override
      public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        int code = response.code();
        if (code == 201) {
          Intent intent = getIntent();
          setResult(RESULT_OVER, intent);
          KnowAddTopicActivity.this.finish();
        } else if (code == 401) {
          Utils.haveNotLogin(KnowAddTopicActivity.this);
        } else {
          Toast.makeText(KnowAddTopicActivity.this, "网络环境不好╯﹏╰",
                  Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public void onFailure(Call<ResponseBody> call, Throwable t) {
        Toast.makeText(KnowAddTopicActivity.this, "网络环境不好╯﹏╰",
                Toast.LENGTH_SHORT).show();
      }
    });
  }

  /**
   * 添加gridview
   */
  private void addGridView() {
    GridView gridView = (GridView) findViewById(R.id.gridview);

    topicGrid = new TopicAdapter(this);
    gridView.setAdapter(topicGrid);
  }

  /**
   * 把uri转化成bitmap
   */
  private void getSentBitmap() {
    for (String p : picUri) {
      Uri uri = Uri.parse(p);
      Bitmap bitmap = null;
      try {
        bitmap = MediaStore.Images.Media.getBitmap(
                this.getContentResolver(), uri);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      bitmaps.add(bitmap);
    }
  }

  /**
   * 得到标签的格式
   */
  void getSentTopic(ArrayList<String> allTopic) {

    sentTopic = TextUtils.join(",", allTopic);
    sentTopic = sentTopic.substring(0, sentTopic.length() - 1);
  }
}

/**
 * @author 林蔚澜 gridView的适配器
 */
class TopicAdapter extends BaseAdapter {

  private ArrayList<Map<String, String>> value = new ArrayList<Map<String, String>>();
  private final String BLUE = "blue", ORANGE = "orange", PINK = "pink",
          GREEN = "green", YELLOW = "yellow", GREY = "grey";

  private final String[] otherColor = {"blue", "orange", "pink", "green",
          "yellow"};
  private final String CHOSEN = "chosen", UNCHOSEN = "unchosen";
  private LayoutInflater inflater;

  TopicAdapter(Context context) {
    super();
    inflater = LayoutInflater.from(context);
    iniFirstSix();

  }

  /**
   * 最初的几个默认标签
   */
  private void iniFirstSix() {
    Map<String, String> tmp = new HashMap<String, String>();
    tmp.put("topic", "学习");
    tmp.put("color", BLUE);
    tmp.put("chose", CHOSEN);
    value.add(tmp);

    tmp = new HashMap<String, String>();
    tmp.put("topic", "生活");
    tmp.put("color", ORANGE);
    tmp.put("chose", UNCHOSEN);
    value.add(tmp);

    tmp = new HashMap<String, String>();
    tmp.put("topic", "情感");
    tmp.put("color", PINK);
    tmp.put("chose", UNCHOSEN);
    value.add(tmp);

    tmp = new HashMap<String, String>();
    tmp.put("topic", "就业");
    tmp.put("color", GREEN);
    tmp.put("chose", UNCHOSEN);
    value.add(tmp);

    tmp = new HashMap<String, String>();
    tmp.put("topic", "娱乐");
    tmp.put("color", YELLOW);
    tmp.put("chose", UNCHOSEN);
    value.add(tmp);

    tmp = new HashMap<String, String>();
    tmp.put("topic", "其他");
    tmp.put("color", GREY);
    tmp.put("chose", UNCHOSEN);
    value.add(tmp);
  }

  @Override
  public int getCount() {
    return value.size();
  }

  @Override
  public Object getItem(int arg0) {
    return arg0;
  }

  @Override
  public long getItemId(int arg0) {
    return arg0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final Map<String, String> item = value.get(position);
    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = inflater
              .inflate(R.layout.bitknow_top_grid_item, null);
      viewHolder = new ViewHolder();
      viewHolder.title = (TextView) convertView.findViewById(R.id.topic);
      viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    viewHolder.title.setText((String) item.get("topic"));

    // 得到背景图id
    Class<drawable> cls = R.drawable.class;
    Integer pic = null;
    try {
      pic = cls
              .getDeclaredField(
                      "know_topic_" + item.get("color") + "_"
                              + item.get("chose")).getInt(null);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    // 显示图片
    viewHolder.image.setImageResource(pic);

    convertView.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        if (item.get("chose").equals("unchosen"))
          item.put("chose", CHOSEN);
        else
          item.put("chose", UNCHOSEN);
        TopicAdapter.this.notifyDataSetChanged();
      }
    });

    return convertView;

  }

  /**
   * 添加一个标签
   *
   * @param String 标签内容（颜色和是否可选自动分配）
   */
  void addTopic(String topic) {

    int id = (value.size() - 6) % 5;

    Map<String, String> tmp = new HashMap<String, String>();
    tmp.put("topic", topic);
    tmp.put("color", otherColor[id]);
    tmp.put("chose", CHOSEN);
    value.add(tmp);
    TopicAdapter.this.notifyDataSetChanged();
  }

  /**
   * 得到话题组成的ArrayList<String>
   *
   * @return
   */
  ArrayList<String> getAllTopic() {
    ArrayList<String> allTopic = new ArrayList<String>();
    for (Map<String, String> p : value) {
      if (p.get("chose").equals(CHOSEN))
        allTopic.add(p.get("topic"));
    }
    return allTopic;
  }

  class ViewHolder {
    public TextView title;
    public ImageView image;
  }

}
