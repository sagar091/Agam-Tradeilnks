package com.example.sagar.myapplication.marketing.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.marketing.adapter.OrderPagerAdapter;


public class OrderMarketingFragment extends Fragment {

    View customView;
    TabLayout tabLayout;
    ViewPager viewPager;
    OrderPagerAdapter adapter;

    public static OrderMarketingFragment newInstance(String param1, String param2) {
        OrderMarketingFragment fragment = new OrderMarketingFragment();
        return fragment;
    }

    public OrderMarketingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        customView = inflater.inflate(R.layout.fragment_order_marketing, container, false);

        init(customView);

        return customView;
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Orders");
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

}
