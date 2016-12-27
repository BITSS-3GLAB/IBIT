package com.bitss.Digital_BIT.Post.activity;

import com.bitss.Digital_BIT.CustomBaseActivity;
import com.bitss.Digital_BIT.R;
import com.bitss.Digital_BIT.Post.adapter.PostTypeAdapter;
import com.bitss.Digital_BIT.Util.Constants;
import com.viewpagerindicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;



public class TabStaggeredGridActivity extends CustomBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_tab);

        mTvNaviTitle.setText(getResources().getString(R.string.tabstaggeredgridactivity_action_bar_title));


        String[] content = new String[2];
        int[] type = new int[2];

        content[0] = getResources().getString(R.string.tabstaggeredgridactivity_tab_hobby_group);
        content[1] = getResources().getString(R.string.tabstaggeredgridactivity_tab_post);

        type[0] = Constants.TYPE_TAB_HOBBY_HROUP;
        type[1] = Constants.TYPE_TAB_POST;

        PostTypeAdapter adapter = new PostTypeAdapter(getSupportFragmentManager(), content, type);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
    

}
