package com.bitss.Digital_BIT.Post.adapter;

import com.bitss.Digital_BIT.Post.fragment.HobbyGroupGridFragment;
import com.bitss.Digital_BIT.Post.fragment.PostListFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class PostTypeAdapter extends FragmentStatePagerAdapter {

    private final String[] content;
    private final int[] type;

    public PostTypeAdapter(FragmentManager fm, String[] content, int[] type) {
        super(fm);
        this.content = content;
        this.type = type;
    }

    @Override
    public int getCount() {
        return content.length;
    }

    @Override
    public Fragment getItem(int position) {
    	if(position == 0){
    		return HobbyGroupGridFragment.newInstance();
    		}
    	return PostListFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return content[position];
    }

}
