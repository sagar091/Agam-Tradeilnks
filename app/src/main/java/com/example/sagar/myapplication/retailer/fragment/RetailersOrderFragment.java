package com.example.sagar.myapplication.retailer.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;


public class RetailersOrderFragment extends Fragment {

    View customView;
    TabLayout tabLayout;
    ViewPager viewPager;
    OrderPagerAdapter adapter;

    public static RetailersOrderFragment newInstance(String param1, String param2) {
        RetailersOrderFragment fragment = new RetailersOrderFragment();

        return fragment;
    }

    public RetailersOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        customView = inflater.inflate(R.layout.fragment_retailers_order, container, false);

        init(customView);

        return customView;
    }

    private void init(View customView) {
        ((RetailerDrawerActivity) getActivity()).setTitle("Orders");
        ((RetailerDrawerActivity) getActivity()).setSubtitle("no");

        tabLayout = (TabLayout) customView.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) customView.findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText("Pending Order"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed Order"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new OrderPagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public class OrderPagerAdapter extends FragmentStatePagerAdapter {
        int numOfTabs;

        public OrderPagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OrderPendingFragment.newInstance();

                case 1:
                    return OrderCompletedFragment.newInstance();

                default:
                    return OrderPendingFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }

}
