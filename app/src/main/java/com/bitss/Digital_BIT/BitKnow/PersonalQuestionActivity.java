package com.bitss.Digital_BIT.BitKnow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitss.Digital_BIT.R;

public class PersonalQuestionActivity extends FragmentActivity implements OnClickListener {
  private Context context;
  private FragmentManager fragmentManager;

  private ImageView backToMain;

  private TextView solvedText;
  private ImageView solvedBar;
  private TextView unsolvedText;
  private ImageView unsolvedBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bitknow_message);

    context = this;
    fragmentManager = getSupportFragmentManager();
    init();
    listeners();
    switchFragment(BaseQuestionListFragment.TYPE_PERSONAL_RESOLVED);
  }

  private void init() {
    backToMain = (ImageView) findViewById(R.id.message_back);
    solvedText = (TextView) findViewById(R.id.solved_text);
    solvedBar = (ImageView) findViewById(R.id.solved_bar);
    unsolvedText = (TextView) findViewById(R.id.unsolved_text);
    unsolvedBar = (ImageView) findViewById(R.id.unsolved_bar);
  }

  private void listeners() {
    backToMain.setOnClickListener(this);
    findViewById(R.id.solved_tab).setOnClickListener(this);
    findViewById(R.id.unsolved_tab).setOnClickListener(this);
  }

  private BaseQuestionListFragment solvedFragment;

  private BaseQuestionListFragment unsolvedFragment;

  private void switchFragment(int type) {
    BaseQuestionListFragment fragment = null;
    switch (type) {
      case BaseQuestionListFragment.TYPE_PERSONAL_RESOLVED:
        if (solvedBar == null) {
          solvedFragment = BaseQuestionListFragment.newInstance(type);
        }
        fragment = solvedFragment;
        break;
      case BaseQuestionListFragment.TYPE_PERSONAL_UNRESOLVED:
        if (unsolvedFragment == null) {
          unsolvedFragment = BaseQuestionListFragment.newInstance(type);
        }
        fragment = unsolvedFragment;
        break;
    }
    if (fragment != null) {
      fragmentManager.beginTransaction()
              .replace(R.id.container, fragment, "fragment")
              .commitAllowingStateLoss();
    }
  }

  @Override
  public void onClick(View view) {
    int id = view.getId();
    switch (id) {
      case R.id.message_back:
        finish();
        break;
      case R.id.solved_tab:
        unsolvedText.setTextColor(getResources().getColor(R.color.font_3));
        unsolvedBar.setVisibility(View.GONE);
        solvedText.setTextColor(getResources().getColor(R.color.font_4));
        solvedBar.setVisibility(View.VISIBLE);
        switchFragment(BaseQuestionListFragment.TYPE_PERSONAL_RESOLVED);
        break;
      case R.id.unsolved_tab:
        solvedText.setTextColor(getResources().getColor(R.color.font_3));
        solvedBar.setVisibility(View.GONE);
        unsolvedText.setTextColor(getResources().getColor(R.color.font_4));
        unsolvedBar.setVisibility(View.VISIBLE);
        switchFragment(BaseQuestionListFragment.TYPE_PERSONAL_UNRESOLVED);
        break;
      default:
        break;
    }
  }

}
