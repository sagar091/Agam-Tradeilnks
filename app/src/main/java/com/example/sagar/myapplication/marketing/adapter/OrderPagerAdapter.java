package com.example.sagar.myapplication.marketing.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.sagar.myapplication.marketing.fragment.OrderCompletedFragment;
import com.example.sagar.myapplication.marketing.fragment.OrderPendingFragment;

/**
 * Created by sagartahelyani on 23-09-2015.
 */
public class OrderPagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    String filter;

    public OrderPagerAdapter(FragmentManager fm, int numOfTabs, String filter) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.filter = filter;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OrderPendingFragment.newInstance(filter);

            case 1:
                return OrderCompletedFragment.newInstance(filter);

            default:
                return OrderPendingFragment.newInstance(filter);
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
