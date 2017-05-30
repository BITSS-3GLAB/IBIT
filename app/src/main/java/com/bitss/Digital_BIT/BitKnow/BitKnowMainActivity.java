package com.bitss.Digital_BIT.BitKnow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bitss.Digital_BIT.BaseApplication;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.Util.Utils;

public class BitKnowMainActivity extends FragmentActivity implements OnClickListener {

  private ImageView backToGuide;

  private View messageIcon;
  private View addIcon;

  private RelativeLayout left, right;
  private TextView suggest;
  private ImageView suggest_bar;
  private TextView latest;
  private ImageView latest_bar;

  private SharedPreferences settings;
  private String phone;

  private BaseQuestionListFragment latestFragment;

  private BaseQuestionListFragment hotestFragment;

  private FragmentManager fragmentManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bitknow_main);

    fragmentManager = getSupportFragmentManager();
    init();
    listeners();
    switchFragment(BaseQuestionListFragment.TYPE_HOTTEST);
  }

  private void init() {
    settings = ((BaseApplication) getApplication()).getPreferences();
    phone = settings.getString(Constants.USER_PHONE, "");
    backToGuide = (ImageView) findViewById(R.id.bitknow_main_back);
    messageIcon = findViewById(R.id.bitknow_message);
    addIcon = findViewById(R.id.bitknow_add);

    left = (RelativeLayout) findViewById(R.id.left);
    right = (RelativeLayout) findViewById(R.id.right);
    suggest = (TextView) findViewById(R.id.suggest);
    suggest_bar = (ImageView) findViewById(R.id.suggest_bar);
    latest = (TextView) findViewById(R.id.lastest);
    latest_bar = (ImageView) findViewById(R.id.lastest_bar);
  }

//  private void getData(int refresh, String type)
//          throws UnsupportedEncodingException {
//    if (refresh == DoRefresh) {
//      // 下拉刷新
//      JSONObject json = new JSONObject();
//      try {
//        json.put("offset", "0");
//        json.put("number", "10");
//        json.put("key", searchData);
//        json.put("orderBy", order);
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//
//      mConnection.doPost(Constants.BITKNOWTEST_SERVER_STRING
//                      + "SendQuestionsInfo",
//              Constants.BITKNOWTEST_CLOUDSERVER_STRING
//                      + "SendQuestionsInfo", json,
//              new BitKnowNetworkHandler() {
//                @Override
//                public void onSuccess(String str) {
//                  try {
//                    JSONObject json = new JSONObject(str);
//                    System.out.println(str);
//                    // Log.v("json", str);
//                    boolean isSucess = json.getBoolean("success");
//                    if (isSucess) {
//                      mDatas.clear();
//                      JSONObject result = json
//                              .getJSONObject("result");
//                      JSONArray records = result
//                              .getJSONArray("records");
//                      // Log.v(TAG, str);
//                      for (int i = 0; i < records.length(); i++) {
//                        JSONObject item = records
//                                .getJSONObject(i);
//                        BitKnowMainData data = new BitKnowMainData(
//                                item);
//                        mDatas.add(data);
//                      }
//                    }
//
//                    if (bitKnowMainAdapter == null) {
//                      bitKnowMainAdapter = new BitKnowMainAdapter(
//                              myActivity, mDatas);
//                      pullToRefreshListView
//                              .setAdapter(bitKnowMainAdapter);
//                    } else {
//                      bitKnowMainAdapter.notifyDataSetChanged();
//                    }
//                  } catch (JSONException e) {
//                    e.printStackTrace();
//                  } finally {
//                    number = 0;
//                    pullToRefreshListView.onRefreshComplete();
//                    mIsRefresh = false;
//                  }
//                }
//
//                @Override
//                public void onFailure() {
//                  pullToRefreshListView.onRefreshComplete();
//                  Toast.makeText(myActivity, "网络异常",
//                          Toast.LENGTH_SHORT).show();
//                  mIsRefresh = false;
//                }
//              });
//    } else {
//      // 上滑加载更多
//      JSONObject json = new JSONObject();
//      try {
//        json.put("offset", mDatas.size());
//        json.put("number", "10");
//        json.put("key", searchData);
//        json.put("orderBy", order);
//      } catch (JSONException e) {
//        e.printStackTrace();
//      }
//
//      mConnection.doPost(Constants.BITKNOWTEST_SERVER_STRING
//                      + "SendQuestionsInfo",
//              Constants.BITKNOWTEST_CLOUDSERVER_STRING
//                      + "SendQuestionsInfo", json,
//              new BitKnowNetworkHandler() {
//
//                @Override
//                public void onSuccess(String str) {
//                  try {
//                    JSONObject json = new JSONObject(str);
//                    System.out.println(str);
//                    boolean isSucess = json.getBoolean("sucess");
//                    if (isSucess) {
//                      JSONObject result = json
//                              .getJSONObject("result");
//                      JSONArray records = result
//                              .getJSONArray("records");
//                      for (int i = 0; i < records.length(); i++) {
//                        JSONObject item = records
//                                .getJSONObject(i);
//                        BitKnowMainData data = new BitKnowMainData(
//                                item);
//                        mDatas.add(data);
//                      }
//                    }
//
//                    bitKnowMainAdapter.notifyDataSetChanged();
//                    Toast.makeText(myActivity, "成功加载更多数据",
//                            Toast.LENGTH_SHORT).show();
//                  } catch (JSONException e) {
//                    e.printStackTrace();
//                  } finally {
//                    pullToRefreshListView.onRefreshComplete();
//                    mIsRefresh = false;
//                  }
//                }
//
//                @Override
//                public void onFailure() {
//                  pullToRefreshListView.onRefreshComplete();
//                  Toast.makeText(myActivity, "网络异常",
//                          Toast.LENGTH_SHORT).show();
//                  mIsRefresh = false;
//                }
//              });
//    }
//  }

  private void switchFragment(int type) {
    BaseQuestionListFragment fragment;
    fragment = (BaseQuestionListFragment) fragmentManager.findFragmentByTag("question_list_" + type);
    if (fragment == null) {
      fragment = BaseQuestionListFragment.newInstance(type);
    }
    fragmentManager.beginTransaction()
            .replace(R.id.container, fragment, "question_list_" + type)
            .commitAllowingStateLoss();
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.bitknow_main_back:
        finish();
      case R.id.left:
      case R.id.suggest:
        suggest.setTextColor(getResources().getColor(R.color.font_4));
        suggest_bar.setVisibility(View.VISIBLE);
        latest.setTextColor(getResources().getColor(R.color.font_3));
        latest_bar.setVisibility(View.GONE);
        switchFragment(BaseQuestionListFragment.TYPE_HOTTEST);
        break;
      case R.id.right:
      case R.id.lastest:
        suggest.setTextColor(getResources().getColor(R.color.font_3));
        suggest_bar.setVisibility(View.GONE);
        latest.setTextColor(getResources().getColor(R.color.font_4));
        latest_bar.setVisibility(View.VISIBLE);
        switchFragment(BaseQuestionListFragment.TYPE_LATEST);
        break;
      case R.id.bitknow_message:
        phone = settings.getString(Constants.KEY_EMAIL, "");
        if (phone.equals("")) {
          Utils.haveNotLogin(BitKnowMainActivity.this);
        } else {
          Intent messageIntent = new Intent();
          messageIntent.setClass(this, PersonalQuestionActivity.class);
          startActivity(messageIntent);
          break;
        }
      case R.id.bitknow_add:
        phone = settings.getString(Constants.KEY_EMAIL, "");
        if (phone.equals("")) {
          Utils.haveNotLogin(BitKnowMainActivity.this);
        } else {
          Intent addIntent = new Intent();
          addIntent.setClass(this, QuestionDescribeActivity.class);
          startActivityForResult(addIntent, 5);
        }
        break;
      default:
        break;
    }
  }

  private void listeners() {
    backToGuide.setOnClickListener(this);
    messageIcon.setOnClickListener(this);
    addIcon.setOnClickListener(this);
    left.setOnClickListener(this);
    suggest.setOnClickListener(this);
    right.setOnClickListener(this);
    latest.setOnClickListener(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 5) {
//      loadData(1);
    } else if (requestCode == Constants.ANSWER_COUNT_REFRESH) {
      if (data != null) {
        int pos = data.getIntExtra(Constants.KEY_ITEM_POSITION, -1) - 1;
        int answer = data.getIntExtra(Constants.KEY_ANSWER_COUNT, -1);

        if (pos >= 0 && answer != -1) {
//          mDatas.get(pos).answerNum = answer;
//          bitKnowMainAdapter.notifyDataSetChanged();
        }
      }
    }
  }

}
