package com.bitss.Digital_BIT.BitKnow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bitss.Digital_BIT.BitKnow.model.QuestionModel;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Util.Constants;
import com.bitss.Digital_BIT.okhttp.RetrofitFactory;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Junhao Zhou 2017/5/28
 */

public class BaseQuestionListFragment extends Fragment {

  public static final int TYPE_LATEST = 0x00;

  public static final int TYPE_HOTTEST = 0x01;

  public static final int TYPE_PERSONAL_RESOLVED = 0x10;

  public static final int TYPE_PERSONAL_UNRESOLVED = 0x11;

  private List<QuestionModel> questionList;

  private PullToRefreshListView pullToRefreshListView;

  private BitKnowMainAdapter bitKnowMainAdapter;

  private int type = TYPE_LATEST;

  private int currentPage = 1;// 记录已经加载多少页

  public static BaseQuestionListFragment newInstance(int type) {

    Bundle args = new Bundle();
    args.putInt("type", type);
    BaseQuestionListFragment fragment = new BaseQuestionListFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    type = getArguments().getInt("type");
    questionList = new ArrayList<>();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_question_list, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.question_list);
    pullToRefreshListView.setShowIndicator(false);
    pullToRefreshListView
            .setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.BOTH);
    pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
      @Override
      public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        doRefresh();
      }

      @Override
      public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        onMore();
      }
    });

    pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
                              int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("data", questionList.get(position - 1));
        intent.putExtra(Constants.KEY_ITEM_POSITION, position);
        intent.setClass(getActivity(),
                BitKnowDetailActivity.class);
        startActivityForResult(intent, Constants.ANSWER_COUNT_REFRESH);
      }
    });

    bitKnowMainAdapter = new BitKnowMainAdapter(getActivity(), questionList);
    pullToRefreshListView.setAdapter(bitKnowMainAdapter);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (questionList.size() == 0) {
      currentPage = 1;
      loadData(currentPage);
    }
  }

  private void doRefresh() {
    currentPage = 1;
    loadData(currentPage);
  }

  private void onMore() {
    loadData(currentPage);
  }

  private void loadData(final int page) {
    Retrofit retrofit = RetrofitFactory.newInstance()
            .baseUrl(Constants.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create()).build();

    BitKnowApi api = retrofit.create(BitKnowApi.class);
    Call<List<QuestionModel>> call = null;
    if (type == TYPE_HOTTEST) {
      call = api.getHottestList(page);
    } else if (type == TYPE_LATEST) {
      call = api.getLatestList(page);
    } else if (type == TYPE_PERSONAL_RESOLVED) {
      call = api.getResolved(page);
    } else if (type == TYPE_PERSONAL_UNRESOLVED) {
      call = api.getUnresolved(page);
    }
    if (call == null) {
      return;
    }
    call.enqueue(new Callback<List<QuestionModel>>() {
      @Override
      public void onResponse(Call<List<QuestionModel>> call, Response<List<QuestionModel>> response) {
        if (page == 1) {
          questionList.clear();
        }
        if (response.body() != null) {
          questionList.addAll(response.body());
        }
        bitKnowMainAdapter.notifyDataSetChanged();
        pullToRefreshListView.onRefreshComplete();
      }

      @Override
      public void onFailure(Call<List<QuestionModel>> call, Throwable t) {
        pullToRefreshListView.onRefreshComplete();
        t.printStackTrace();
      }
    });
  }

}
