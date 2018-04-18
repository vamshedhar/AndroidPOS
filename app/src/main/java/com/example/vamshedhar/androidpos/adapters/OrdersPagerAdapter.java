package com.example.vamshedhar.androidpos.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.vamshedhar.androidpos.fragments.CurrentOrdersFragment;
import com.example.vamshedhar.androidpos.fragments.PastOrdersFragment;

import java.util.zip.Inflater;


public class OrdersPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public OrdersPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("POSITIONNUM", position+"");
        switch (position) {
            case 0:
                CurrentOrdersFragment tab1 = new CurrentOrdersFragment();
                return tab1;
            case 1:
                PastOrdersFragment tab2 = new PastOrdersFragment();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}