package com.example.sagar.myapplication.retailer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sagar.myapplication.R;
import com.example.sagar.myapplication.marketing.activity.MarketingDrawerActivity;
import com.example.sagar.myapplication.retailer.activity.RetailerDrawerActivity;


public class HomeRetailerFragment extends Fragment {

    View parentView;

    public static HomeRetailerFragment newInstance(String param1, String param2) {
        HomeRetailerFragment fragment = new HomeRetailerFragment();

        return fragment;
    }

    public HomeRetailerFragment() {
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
        parentView = inflater.inflate(R.layout.fragment_home_retailer, container, false);
        init(parentView);
        return parentView;
    }

    private void init(View parentView) {
        ((RetailerDrawerActivity) getActivity()).setTitle("Products");
        ((RetailerDrawerActivity) getActivity()).setSubtitle("no");
    }

}
