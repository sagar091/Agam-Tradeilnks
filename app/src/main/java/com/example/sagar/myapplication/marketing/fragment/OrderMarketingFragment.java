package com.example.sagar.myapplication.marketing.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.marketing.adapter.OrderPagerAdapter;

public class OrderMarketingFragment extends Fragment {

    View customView;
    TabLayout tabLayout;
    ViewPager viewPager;
    OrderPagerAdapter adapter;
    RelativeLayout filterLayout;
    private TextView txtFilter, txtClear;

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

        txtClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLayout.setVisibility(View.GONE);
                ((MarketingDrawerActivity) getActivity()).clearFilter();
            }
        });

        return customView;
    }

    private void init(View customView) {
        ((MarketingDrawerActivity) getActivity()).setTitle("Orders");
        ((MarketingDrawerActivity) getActivity()).setSubtitle("no");
        ((MarketingDrawerActivity) getActivity()).setFilterImage(true);

        filterLayout = (RelativeLayout) customView.findViewById(R.id.filterLayout);
        txtFilter = (TextView) customView.findViewById(R.id.txtFilter);
        txtClear = (TextView) customView.findViewById(R.id.txtClear);
        tabLayout = (TabLayout) customView.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) customView.findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText("Pending Order"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed Order"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new OrderPagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount(), ((MarketingDrawerActivity) getActivity()).getDrawerFilter());

        viewPager.setAdapter(adapter);

        if (((MarketingDrawerActivity) getActivity()).getDrawerFilter().equals("0")) {
            filterLayout.setVisibility(View.GONE);
        } else {
            filterLayout.setVisibility(View.VISIBLE);
            txtFilter.setText("Orders from: " + setFilterText(((MarketingDrawerActivity) getActivity()).getDrawerFilter()));
        }

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

    private String setFilterText(String filter) {
        if (filter.equals("1")) {
            return "1 Week";
        } else if (filter.equals("2")) {
            return "2 Week";
        } else {
            return "3 Week";
        }
    }

}
